package camp.xit.auth.services.rest.account;

import camp.xit.auth.services.model.PrepareAccountData;
import camp.xit.auth.services.google.GSuiteDirectoryService;
import camp.xit.auth.services.google.model.GroupList;
import camp.xit.auth.services.model.AccountInfo;
import camp.xit.auth.services.model.CreateAccountData;
import camp.xit.auth.services.model.PrepareAccountData.Role;
import camp.xit.auth.services.model.ServerError;
import camp.xit.auth.services.config.Configuration;
import camp.xit.auth.services.ldap.UserLdapService;
import camp.xit.auth.services.util.StringUtils;
import com.unboundid.ldap.sdk.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.json.basic.JsonMapObject;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oidc.common.IdToken;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("account")
public class UserAccountService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);
    private static final String GSUITE_DOMAIN_PROP = "gsuite.domain";

    @Context
    private OidcClientTokenContext oidcContext;
    private final GSuiteDirectoryService directoryService;
    private final WebClient peopleServiceClient;
    private final UserLdapService ldapService;

    private Configuration config;


    public UserAccountService(GSuiteDirectoryService directoryService, WebClient peopleServiceClient,
            UserLdapService ldapService) {
        this.directoryService = directoryService;
        this.peopleServiceClient = peopleServiceClient;
        this.ldapService = ldapService;
    }


    private void configure() {
        log.info("Configuring UserInfoService ...");
    }


    @GET
    @Path("prepare")
    public PrepareAccountData prepareAccount() {
        final org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();

        PrepareAccountData detail = new PrepareAccountData();
        detail.setGivenName(userInfo.getGivenName());
        detail.setFamilyName(userInfo.getFamilyName());
        detail.setName(userInfo.getName());
        detail.setEmails(getVerifiedEmails());
        detail.setEmailVerified(userInfo.getEmailVerified());
        detail.setRole(getRole());
        detail.setSaveGSuitePassword(detail.getRole() == Role.INTERNAL);

        GroupList list = directoryService.getGroups(userInfo.getSubject());
        if (list.getGroups() != null) {
            detail.setGroups(list.getGroups().stream().map(PrepareAccountData.Group::map).collect(Collectors.toList()));
        }
        return detail;
    }


    @POST
    public Response createAccount(CreateAccountData data) {
        ResponseBuilder response;
        String subject = oidcContext.getUserInfo().getSubject();
        try {
            if (!ldapService.accountExists(subject)) {
                data.setRole(getRole());
                ldapService.createAccount(oidcContext.getUserInfo(), data);
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
    public Response updateAccount(CreateAccountData data) {
        ResponseBuilder response;
        String subject = oidcContext.getUserInfo().getSubject();
        try {
            if (ldapService.accountExists(subject)) {
                ldapService.updateAccount(oidcContext.getUserInfo(), data);
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
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            this.config = (Configuration) event.getProperty(Configuration.CONFIG_PROP);
            configure();
        }
    }


    private Role getRole() {
        String hdParam = oidcContext.getIdToken().getStringProperty("hd");
        return config.get(GSUITE_DOMAIN_PROP).equals(hdParam) ? Role.INTERNAL : Role.EXTERNAL;
    }


    private List<PrepareAccountData.EmailAddress> getVerifiedEmails() {
        List<PrepareAccountData.EmailAddress> result = new ArrayList<>();
        ClientAccessToken accessToken = oidcContext.getToken();
        peopleServiceClient.authorization(accessToken);
        log.info("Reading email addresses");
        JsonMapObject jsonMap = peopleServiceClient.query("requestMask.includeField", "person.email_addresses").get().readEntity(JsonMapObject.class);
        List<Object> emails = CastUtils.cast((List<?>) jsonMap.getProperty("emailAddresses"));
        for (Object email : emails) {
            Map<String, Object> emailMap = CastUtils.cast((Map<String, Object>) email);
            String value = (String) emailMap.get("value");
            Map<String, Object> metadata = CastUtils.cast((Map<String, Object>) emailMap.get("metadata"));
            boolean primary = metadata.containsKey("primary") ? (boolean) metadata.get("primary") : false;
            boolean verified = metadata.containsKey("verified") ? (boolean) metadata.get("verified") : false;
            if (verified) {
                result.add(new PrepareAccountData.EmailAddress(value, primary, verified));
            }
        }
        return result;
    }
}
