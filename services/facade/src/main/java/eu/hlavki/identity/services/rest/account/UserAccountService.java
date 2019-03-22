package eu.hlavki.identity.services.rest.account;

import eu.hlavki.identity.plugin.api.ProcessException;
import eu.hlavki.identity.plugin.api.UserInterceptor;
import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.InvalidPasswordException;
import eu.hlavki.identity.services.google.model.GSuiteUser;
import eu.hlavki.identity.services.google.model.GroupList;
import eu.hlavki.identity.services.ldap.LdapAccountService;
import eu.hlavki.identity.services.ldap.LdapSystemException;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.rest.config.Configuration;
import eu.hlavki.identity.services.rest.model.AccountInfo;
import eu.hlavki.identity.services.rest.model.CreateAccountData;
import eu.hlavki.identity.services.rest.model.PrepareAccountData;
import eu.hlavki.identity.services.rest.model.Role;
import eu.hlavki.identity.services.rest.model.ServerError;
import eu.hlavki.identity.services.rest.model.UpdateAccountData;
import eu.hlavki.identity.services.rest.util.AccountUtil;
import static eu.hlavki.identity.services.rest.util.AccountUtil.isInternalAccount;
import eu.hlavki.identity.services.sync.AccountSyncService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("account")
public class UserAccountService {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    @Context
    private OidcClientTokenContext oidcContext;
    private final Configuration config;
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
    }


    @GET
    @Path("prepare")
    public PrepareAccountData prepareAccount() {
        final UserInfo userInfo = oidcContext.getUserInfo();
        String gsuiteDomain = gsuiteDirService.getDomainName();
        PrepareAccountData detail = new PrepareAccountData();
        detail.setGivenName(userInfo.getGivenName());
        detail.setFamilyName(userInfo.getFamilyName());
        detail.setName(userInfo.getName());
        detail.setEmail(userInfo.getEmail());
        detail.setEmails(getAccountAliases(userInfo, gsuiteDomain));
        detail.setEmailVerified(userInfo.getEmailVerified());
        detail.setRole(AccountUtil.getAccountRole(userInfo, gsuiteDomain));
        detail.setSaveGSuitePassword(detail.getRole() == Role.INTERNAL && config.isGsuiteSyncPassword());
        GroupList userGroups = gsuiteDirService.getUserGroups(userInfo.getSubject());
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
                Set<String> emails = Collections.singleton(userInfo.getEmail());
                String gsuiteDomain = gsuiteDirService.getDomainName();
                LdapAccount account = AccountUtil.toLdapAccount(gsuiteDomain, userInfo, emails, data);
                ldapService.createAccount(account);
                if (data.isSaveGSuitePassword() && isInternalAccount(userInfo, gsuiteDomain)) {
                    try {
                        gsuiteDirService.updateUserPassword(subject, data.getPassword());
                    } catch (InvalidPasswordException e) {
                        log.warn("Can't update gsuite password", e);
                    }
                }
                for (UserInterceptor plugin : userPlugins) {
                    try {
                        plugin.userCreated(AccountUtil.toCreated(account));
                    } catch (ProcessException e) {
                        log.warn("User plugin execution failed!", e);
                    }
                }

                response = Response.ok();
            } else {
                response = Response.ok().status(Response.Status.CONFLICT);
            }
        } catch (LdapSystemException e) {
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
                String gsuiteDomain = gsuiteDirService.getDomainName();
                Set<String> emails = Collections.singleton(userInfo.getEmail());
                LdapAccount account = AccountUtil.toLdapAccount(gsuiteDomain, userInfo, emails, data);
                ldapService.updateAccount(account);
                response = Response.ok();
                if (data.isSaveGSuitePassword() && isInternalAccount(userInfo, gsuiteDomain)) {
                    try {
                        gsuiteDirService.updateUserPassword(subject, data.getPassword());
                    } catch (InvalidPasswordException e) {
                        log.warn("Can't update gsuite password", e);
                    }
                }
            } else {
                response = Response.ok().status(Response.Status.CONFLICT);
            }
        } catch (LdapSystemException e) {
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
        } catch (LdapSystemException e) {
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
            Optional<LdapAccount> ldapAcc = ldapService.searchBySubject(subject);
            Optional<AccountInfo> info = ldapAcc.map(AccountInfo::new);
            response = info.map(Response::ok).orElse(Response.ok().status(NOT_FOUND));
        } catch (LdapSystemException e) {
            log.error("Can't obtain account info", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    private Set<String> getAccountAliases(UserInfo userInfo, String gsuiteDomain) {
        Set<String> result = new HashSet<>();
        result.add(userInfo.getEmail());
        if (isInternalAccount(userInfo, gsuiteDomain)) {
            GSuiteUser user = gsuiteDirService.getUser(userInfo.getSubject());
            result.addAll(user.getAliases());
        }
        return result;
    }
}
