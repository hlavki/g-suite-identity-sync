package eu.hlavki.identity.services.rest.service;

import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.google.config.Configuration;
import eu.hlavki.identity.services.google.model.GroupMembership;
import eu.hlavki.identity.services.google.model.ServiceAccount;
import eu.hlavki.identity.services.rest.model.GoogleSettingsData;
import eu.hlavki.identity.services.rest.model.UserInfo;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;

@Path("google/settings")
public class GoogleSettingsService {

    @Context
    private OidcClientTokenContext oidcContext;
    private final Configuration googleConfig;
    private final eu.hlavki.identity.services.rest.config.Configuration config;
    private final GSuiteDirectoryService gsuiteDirService;


    public GoogleSettingsService(Configuration googleConfig, GSuiteDirectoryService gsuiteDirService,
            eu.hlavki.identity.services.rest.config.Configuration config) {
        this.googleConfig = googleConfig;
        this.gsuiteDirService = gsuiteDirService;
        this.config = config;
    }


    @GET
    public GoogleSettingsData getSettings() {
        String domain = googleConfig.getGSuiteDomain();
        UserInfo userInfo = new UserInfo(oidcContext.getUserInfo().getName(), oidcContext.getUserInfo().getEmail());
        return new GoogleSettingsData(domain, userInfo);
    }


    @PUT
    @Path("service-account")
    public void configureServiceAccount(ServiceAccount serviceAccount) {
        String privateKey = serviceAccount.getPrivateKey()
                .replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\n", "");
        String me = oidcContext.getUserInfo().getSubject();
        googleConfig.setServiceAccount(serviceAccount.getClientEmail(), privateKey, me, serviceAccount.getTokenUri());
        String adminGroup = config.getAdminGroup() + "@" + gsuiteDirService.getDomainName();
        GroupMembership admins = gsuiteDirService.getGroupMembers(adminGroup);
        if (!admins.isMember(me)) {
            googleConfig.resetServiceAccount();
            throw new ResourceNotFoundException("You are not member of " + adminGroup + " group!");
        }
    }
}
