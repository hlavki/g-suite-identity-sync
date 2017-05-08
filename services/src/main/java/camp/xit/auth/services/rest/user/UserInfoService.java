package camp.xit.auth.services.rest.user;

import camp.xit.auth.services.model.UserDetail;
import camp.xit.auth.services.model.UserInfo;
import camp.xit.auth.services.google.GSuiteDirectoryService;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("user")
public class UserInfoService {

    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    @Context
    private OidcClientTokenContext oidcContext;
    private final GSuiteDirectoryService directoryService;
    private final WebClient peopleServiceClient;


    public UserInfoService(GSuiteDirectoryService directoryService, WebClient peopleServiceClient) {
        this.directoryService = directoryService;
        this.peopleServiceClient = peopleServiceClient;
    }


    @GET
    @Path("info")
    public UserInfo getUserInfo() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
//        GroupMembershipResponse response = directoryService.getGroupMembers("saunicka@hlavki.eu");
        URI profilePicture = resizeProfilePicture(userInfo.getPicture());
        return new UserInfo(userInfo.getName(), userInfo.getEmail(), profilePicture);
    }


    @GET
    @Path("detail")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDetail getUserDetail() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
//        GroupMembershipResponse response = directoryService.getGroupMembers("saunicka@hlavki.eu");
        UserDetail detail = new UserDetail();
        detail.setGivenName(userInfo.getGivenName());
        detail.setFamilyName(userInfo.getFamilyName());
        detail.setName(userInfo.getName());
        detail.setEmail(userInfo.getEmail());
        detail.setProfilePicture(resizeProfilePicture(userInfo.getPicture()));
        detail.setEmailVerified(userInfo.getEmailVerified());
        detail.setRole(detail.getEmail().endsWith("xit.camp") ? UserDetail.Role.INTERNAL : UserDetail.Role.EXTERNAL);
        return detail;
    }


    private URI resizeProfilePicture(String originalUri) {
        return UriBuilder.fromUri(originalUri).queryParam("sz", "100").build();
    }


    private Response getInfoFromPeople() {
        ClientAccessToken accessToken = oidcContext.getToken();
        peopleServiceClient.authorization(accessToken);
        return peopleServiceClient.get();
    }
}
