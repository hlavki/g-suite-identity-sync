package eu.hlavki.identity.services.rest.account;

import com.unboundid.ldap.sdk.LDAPException;
import eu.hlavki.identity.plugin.api.ProcessException;
import eu.hlavki.identity.plugin.api.UserInterceptor;
import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.config.Configuration;
import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.InvalidPasswordException;
import eu.hlavki.identity.services.google.model.GSuiteUser;
import eu.hlavki.identity.services.google.model.GroupList;
import eu.hlavki.identity.services.ldap.LdapAccountService;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.model.Role;
import eu.hlavki.identity.services.model.*;
import eu.hlavki.identity.services.sync.AccountSyncService;
import eu.hlavki.identity.services.util.AccountUtil;
import static eu.hlavki.identity.services.util.AccountUtil.isInternalAccount;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("account")
public class UserAccountService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    @Context
    private OidcClientTokenContext oidcContext;
    private final AppConfiguration config;
    private final GSuiteDirectoryService gsuiteDirService;
    private final LdapAccountService ldapService;
    private final AccountSyncService syncService;
    private final List<UserInterceptor> userPlugins;


    public UserAccountService(Configuration config, GSuiteDirectoryService directoryService,
        LdapAccountService ldapService, AccountSyncService syncService, List<UserInterceptor> userPlugins) {
        this.config = config;
        this.gsuiteDirService = directoryService;
        this.ldapService = ldapService;
        this.syncService = syncService;
        this.userPlugins = userPlugins;
        configure();
    }


    private void configure() {
        log.info("Configuring UserAccountService ...");
    }


    @GET
    @Path("prepare")
    public PrepareAccountData prepareAccount() {
        final UserInfo userInfo = oidcContext.getUserInfo();
        PrepareAccountData detail = new PrepareAccountData();
        detail.setGivenName(userInfo.getGivenName());
        detail.setFamilyName(userInfo.getFamilyName());
        detail.setName(userInfo.getName());
        detail.setEmail(userInfo.getEmail());
        detail.setEmails(getAccountAliases(userInfo, config));
        detail.setEmailVerified(userInfo.getEmailVerified());
        detail.setRole(AccountUtil.getAccountRole(config, userInfo));
        detail.setSaveGSuitePassword(detail.getRole() == Role.INTERNAL && config.isGsuiteSyncPassword());
        GroupList userGroups = gsuiteDirService.getGroups(userInfo.getSubject());
        if (userGroups.getGroups() != null) {
            detail.setGroups(userGroups.getGroups().stream().map(PrepareAccountData.Group::map).collect(Collectors.toList()));
        }
        return detail;
    }


    @POST
    public Response createAccount(@Valid CreateAccountData data) {
        ResponseBuilder response;
        UserInfo userInfo = oidcContext.getUserInfo();
        String subject = userInfo.getSubject();
        try {
            if (!ldapService.accountExists(subject)) {
                Set<String> emails = getAccountEmails(userInfo, config);
                LdapAccount account = LdapAccount.from(config, userInfo, emails, data);
                ldapService.createAccount(account);
                if (data.isSaveGSuitePassword() && isInternalAccount(userInfo, config)) {
                    try {
                        gsuiteDirService.updateUserPassword(subject, data.getPassword());
                    } catch (InvalidPasswordException e) {
                        log.warn("Can't update gsuite password", e);
                    }
                }
                for (UserInterceptor plugin : userPlugins) {
                    try {
                        plugin.userCreated(account.toCreated());
                    } catch (ProcessException e) {
                        log.warn("User plugin execution failed!", e);
                    }
                }

                response = Response.ok();
            } else {
                response = Response.ok().status(Response.Status.CONFLICT);
            }
        } catch (LDAPException e) {
            log.error("Can't create account", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @PUT
    public Response updateAccount(@Valid UpdateAccountData data) {
        ResponseBuilder response;
        UserInfo userInfo = oidcContext.getUserInfo();
        String subject = userInfo.getSubject();
        try {
            if (ldapService.accountExists(subject)) {
                Set<String> emails = getAccountEmails(userInfo, config);
                LdapAccount account = LdapAccount.from(config, userInfo, emails, data);
                ldapService.updateAccount(account);
                response = Response.ok();
                if (data.isSaveGSuitePassword() && isInternalAccount(userInfo, config)) {
                    try {
                        gsuiteDirService.updateUserPassword(subject, data.getPassword());
                    } catch (InvalidPasswordException e) {
                        log.warn("Can't update gsuite password", e);
                    }
                }
            } else {
                response = Response.ok().status(Response.Status.CONFLICT);
            }
        } catch (LDAPException e) {
            log.error("Can't create account", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @PUT
    @Path("groups")
    public Response syncGroups() {
        ResponseBuilder response;
        UserInfo userInfo = oidcContext.getUserInfo();
        try {
            syncService.synchronizeUserGroups(userInfo);
            response = Response.ok();
        } catch (LDAPException e) {
            log.error("Can't create account", e);
            response = ServerError.toResponse("SYNC_ERR", e);
        }
        return response.build();
    }


    @GET
    public Response getAccountInfo() {
        ResponseBuilder response;
        String subject = oidcContext.getUserInfo().getSubject();
        try {
            AccountInfo info = ldapService.getAccountInfo(subject);
            if (info != null) {
                response = Response.ok(info);
            } else {
                response = Response.ok().status(Response.Status.NOT_FOUND);
            }
        } catch (LDAPException e) {
            log.error("Can't obtain account info", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }


    private Set<String> getAccountEmails(UserInfo userInfo, AppConfiguration cfg) {
        Set<String> result = new HashSet<>();
        result.add(userInfo.getEmail());
        return result;
    }


    private Set<String> getAccountAliases(UserInfo userInfo, AppConfiguration cfg) {
        Set<String> result = new HashSet<>();
        result.add(userInfo.getEmail());
        if (isInternalAccount(userInfo, cfg)) {
            GSuiteUser user = gsuiteDirService.getUser(userInfo.getSubject());
            result.addAll(user.getAliases());
        }
        return result;
    }
}
