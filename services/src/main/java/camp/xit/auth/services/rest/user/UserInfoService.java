package camp.xit.auth.services.rest.user;

import camp.xit.auth.services.model.UserDetail;
import camp.xit.auth.services.model.UserInfo;
import camp.xit.auth.services.google.GSuiteDirectoryService;
import camp.xit.auth.services.google.GroupMembershipResponse;
import camp.xit.auth.services.model.UserDetail.Role;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.rs.security.oidc.common.IdToken;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("user")
public class UserInfoService {

    private static final String XIT_DOMAIN = "xit.camp";
    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    @Context
    private OidcClientTokenContext oidcContext;
    private final GSuiteDirectoryService directoryService;


    public UserInfoService(GSuiteDirectoryService directoryService) {
        this.directoryService = directoryService;
    }


    @GET
    @Path("info")
    public UserInfo getUserInfo() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        GroupMembershipResponse response = directoryService.getGroupMembers("cloud.admin@xit.camp");
        log.info("MEMBERS: " + response);
        URI profilePicture = resizeProfilePicture(userInfo.getPicture());
        return new UserInfo(userInfo.getName(), userInfo.getEmail(), profilePicture);
    }


    @GET
    @Path("detail")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDetail getUserDetail() {
        final org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        final IdToken idToken = oidcContext.getIdToken();

//        GroupMembershipResponse response = directoryService.getGroupMembers("saunicka@hlavki.eu");
        UserDetail detail = new UserDetail();
        detail.setGivenName(userInfo.getGivenName());
        detail.setFamilyName(userInfo.getFamilyName());
        detail.setName(userInfo.getName());
        detail.setEmail(userInfo.getEmail());
        detail.setProfilePicture(resizeProfilePicture(userInfo.getPicture()));
        detail.setEmailVerified(userInfo.getEmailVerified());
        String hdParam = idToken.getStringProperty("hd");
        detail.setRole(XIT_DOMAIN.equals(hdParam) ? Role.INTERNAL : Role.EXTERNAL);
        detail.setSaveGSuitePassword(detail.getRole() == Role.INTERNAL);
        return detail;
    }


    private URI resizeProfilePicture(String originalUri) {
        return UriBuilder.fromUri(originalUri).queryParam("sz", "100").build();
    }
}
