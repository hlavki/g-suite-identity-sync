package eu.hlavki.identity.services.rest.account;

import eu.hlavki.identity.services.rest.config.Configuration;
import eu.hlavki.identity.services.rest.config.ConfigurationImpl;
import eu.hlavki.identity.services.rest.model.UserInfo;
import eu.hlavki.identity.services.rest.util.AccountUtil;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("user")
public class UserInfoService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    @Context
    private OidcClientTokenContext oidcContext;
    private final Configuration config;


    public UserInfoService(Configuration config) {
        this.config = config;
        configure();
    }


    private void configure() {
        log.info("Configuring UserInfoService ...");
    }


    @GET
    public UserInfo getUserInfo() {
        org.apache.cxf.rs.security.oidc.common.UserInfo userInfo = oidcContext.getUserInfo();
        URI profilePicture = resizeProfilePicture(userInfo.getPicture());
        boolean amAdmin = AccountUtil.isAmAdmin(config, userInfo);
        return new UserInfo(userInfo.getName(), userInfo.getEmail(), amAdmin, profilePicture);
    }


    private URI resizeProfilePicture(String originalUri) {
        return UriBuilder.fromUri(originalUri).queryParam("sz", "100").build();
    }


    @Override
    public void handleEvent(Event event) {
        if (ConfigurationImpl.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }
}
