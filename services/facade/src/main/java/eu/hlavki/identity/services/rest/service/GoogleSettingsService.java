package eu.hlavki.identity.services.rest.service;

import eu.hlavki.identity.services.google.config.Configuration;
import eu.hlavki.identity.services.google.model.ServiceAccount;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("google/settings")
public class GoogleSettingsService {

    private final Configuration googleConfig;


    public GoogleSettingsService(Configuration googleConfig) {
        this.googleConfig = googleConfig;
    }


    @PUT
    @Path("service-account")
    public void configureServiceAccount(ServiceAccount serviceAccount) {
        String privateKey = serviceAccount.getPrivateKey()
                .replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\n", "");
        googleConfig.setServiceAccount(serviceAccount.getClientEmail(), privateKey, serviceAccount.getTokenUri());
    }
}
