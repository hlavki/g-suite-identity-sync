package camp.xit.identity.services.rest.account;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.model.PrepareAccountData;
import camp.xit.identity.services.google.GSuiteDirectoryService;
import camp.xit.identity.services.google.model.GroupList;
import camp.xit.identity.services.model.AccountInfo;
import camp.xit.identity.services.model.CreateAccountData;
import camp.xit.identity.services.model.PrepareAccountData.Role;
import camp.xit.identity.services.model.ServerError;
import camp.xit.identity.services.config.Configuration;
import camp.xit.identity.services.google.model.GSuiteUser;
import com.unboundid.ldap.sdk.*;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import camp.xit.identity.services.ldap.LdapAccountService;
import camp.xit.identity.services.ldap.model.LdapAccount;
import camp.xit.identity.services.model.*;
import camp.xit.identity.services.sync.AccountSyncService;
import camp.xit.identity.services.util.AccountUtil;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

@Path("account")
public class UserAccountService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    @Context
    private OidcClientTokenContext oidcContext;
    private final AppConfiguration config;
    private final GSuiteDirectoryService directoryService;
    private final WebClient peopleServiceClient;
    private final LdapAccountService ldapService;
    private final AccountSyncService syncService;


    public UserAccountService(Configuration config, GSuiteDirectoryService directoryService,
            WebClient peopleServiceClient, LdapAccountService ldapService, AccountSyncService syncService) {
        this.config = config;
        this.directoryService = directoryService;
        this.peopleServiceClient = peopleServiceClient;
        this.ldapService = ldapService;
        this.syncService = syncService;
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
        detail.setEmails(getUserEmails(userInfo.getSubject()));
        detail.setEmailVerified(userInfo.getEmailVerified());
        detail.setRole(AccountUtil.getAccountRole(config, userInfo));
        detail.setSaveGSuitePassword(detail.getRole() == Role.INTERNAL);
        GroupList userGroups = directoryService.getGroups(userInfo.getSubject());
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
                Set<String> emails = getUserEmails(userInfo.getSubject());
                LdapAccount account = LdapAccount.from(config, userInfo, emails, data);
                ldapService.createAccount(account);
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


    @PUT
    public Response updateAccount(@Valid UpdateAccountData data) {
        ResponseBuilder response;
        UserInfo userInfo = oidcContext.getUserInfo();
        String subject = userInfo.getSubject();
        try {
            if (ldapService.accountExists(subject)) {
                Set<String> emails = getUserEmails(userInfo.getSubject());
                LdapAccount account = LdapAccount.from(config, userInfo, emails, data);
                ldapService.updateAccount(account);
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


    private Set<String> getUserEmails(String userKey) {
        GSuiteUser user = directoryService.getUser(userKey);
        Set<String> result = new HashSet<>(user.getAliases());
        result.add(user.getPrimaryEmail());
        return result;
    }
}
