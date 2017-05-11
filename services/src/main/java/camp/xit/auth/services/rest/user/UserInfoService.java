package camp.xit.auth.services.rest.user;

import camp.xit.auth.services.model.UserDetail;
import camp.xit.auth.services.model.UserInfo;
import camp.xit.auth.services.google.GSuiteDirectoryService;
import camp.xit.auth.services.google.model.GroupList;
import camp.xit.auth.services.model.UserDetail.Role;
import camp.xit.auth.services.util.Configuration;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
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

@Path("user")
public class UserInfoService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    private static final String GSUITE_DOMAIN_PROP = "gsuite.domain";

    @Context
    private OidcClientTokenContext oidcContext;
    private final GSuiteDirectoryService directoryService;
    private final WebClient peopleServiceClient;
    private Configuration config;


    public UserInfoService(GSuiteDirectoryService directoryService, WebClient peopleServiceClient) {
        this.directoryService = directoryService;
        this.peopleServiceClient = peopleServiceClient;
    }


    private void configure() {
        log.info("Configuring UserInfoService ...");
    }


    @GET
    @Path("info")
    public UserInfo getUserInfo() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        URI profilePicture = resizeProfilePicture(userInfo.getPicture());
        return new UserInfo(userInfo.getName(), userInfo.getEmail(), profilePicture);
    }


    @GET
    @Path("detail")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDetail getUserDetail() {
        final org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        final IdToken idToken = oidcContext.getIdToken();

        UserDetail detail = new UserDetail();
        detail.setGivenName(userInfo.getGivenName());
        detail.setFamilyName(userInfo.getFamilyName());
        detail.setName(userInfo.getName());
        detail.setEmails(getVerifiedEmails());
        detail.setProfilePicture(resizeProfilePicture(userInfo.getPicture()));
        detail.setEmailVerified(userInfo.getEmailVerified());
        String hdParam = idToken.getStringProperty("hd");
        detail.setRole(config.get(GSUITE_DOMAIN_PROP).equals(hdParam) ? Role.INTERNAL : Role.EXTERNAL);
        detail.setSaveGSuitePassword(detail.getRole() == Role.INTERNAL);

        GroupList list = directoryService.getGroups(userInfo.getSubject());
        if (list.getGroups() != null) {
            detail.setGroups(list.getGroups().stream().map(UserDetail.Group::map).collect(Collectors.toList()));
        }
        return detail;
    }


    private URI resizeProfilePicture(String originalUri) {
        return UriBuilder.fromUri(originalUri).queryParam("sz", "100").build();
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            this.config = (Configuration) event.getProperty(Configuration.CONFIG_PROP);
            configure();
        }
    }


    private List<UserDetail.EmailAddress> getVerifiedEmails() {
        List<UserDetail.EmailAddress> result = new ArrayList<>();
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
                result.add(new UserDetail.EmailAddress(value, primary, verified));
            }
        }
        return result;
    }
}
