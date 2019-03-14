package eu.hlavki.identity.services.google.config;

import eu.hlavki.identity.services.google.NoPrivateKeyException;
import java.io.File;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.Set;

public interface Configuration {

    String getGSuiteDomain();


    String getGSuiteImplicitGroup();


    boolean isSetGSuiteImplicitGroup();


    String getServiceAccountClientId();


    String getServiceAccountSubject();


    Set<String> getServiceAccountScopes();


    String getPrivateKeyLocation();


    PrivateKey readServiceAccountKey() throws NoPrivateKeyException;


    void setServiceAccountKey(String keyFileLocation, String passphrase);


    long getServiceAccountTokenLifetime();


    String getPushServiceHostname();


    void setPushServiceHostname(String hostname);


    File getPushChannelFile();


    boolean isPushEnabled();


    void setPushEnabled(boolean value);


    Duration getPushRefreshInterval();
}
