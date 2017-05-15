package camp.xit.identity.services.rest.account;

import camp.xit.identity.services.model.UserInfo;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;

@Path("user")
public class UserInfoService {

    @Context
    private OidcClientTokenContext oidcContext;


    @GET
    public UserInfo getUserInfo() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        URI profilePicture = resizeProfilePicture(userInfo.getPicture());
        return new UserInfo(userInfo.getName(), userInfo.getEmail(), profilePicture);
    }


    private URI resizeProfilePicture(String originalUri) {
        return UriBuilder.fromUri(originalUri).queryParam("sz", "100").build();
    }
}
