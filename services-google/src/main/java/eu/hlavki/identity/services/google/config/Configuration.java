package eu.hlavki.identity.services.google.config;

import eu.hlavki.identity.services.google.NoPrivateKeyException;
import java.io.File;
import java.security.PrivateKey;
import java.time.Duration;

public interface Configuration {

    String getGSuiteDomain();


    String getGSuiteImplicitGroup();


    boolean isSetGSuiteImplicitGroup();


    String getServiceAccountClientId();


    String getServiceAccountSubject();


    String getPrivateKeyLocation();


    PrivateKey readServiceAccountKey() throws NoPrivateKeyException;


    long getServiceAccountTokenLifetime();


    File getPushWatchingsFile();


    Duration getPushRefreshInterval();
}
