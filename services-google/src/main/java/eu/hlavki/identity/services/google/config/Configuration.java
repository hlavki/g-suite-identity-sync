package eu.hlavki.identity.services.google.config;

import eu.hlavki.identity.services.google.NoPrivateKeyException;
import java.security.PrivateKey;

public interface Configuration {

    String getGSuiteDomain();


    String getGSuiteImplicitGroup();


    boolean isSetGSuiteImplicitGroup();


    String getServiceAccountClientId();


    String getServiceAccountSubject();


    String getPrivateKeyLocation();


    PrivateKey getServiceAccountKey() throws NoPrivateKeyException;


    long getServiceAccountTokenLifetime();
}
