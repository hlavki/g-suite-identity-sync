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


    String getServiceAccountEmail();


    String getServiceAccountSubject();


    Set<String> getServiceAccountScopes();


    PrivateKey readServiceAccountKey() throws NoPrivateKeyException;


    void setServiceAccount(String clientEmail, String privateKey, String tokenUri);


    String getServiceAccountTokenUri();


    long getServiceAccountTokenLifetime();


    String getPushServiceHostname();


    void setPushServiceHostname(String hostname);


    File getPushChannelFile();


    boolean isPushEnabled();


    void setPushEnabled(boolean value);


    Duration getPushRefreshInterval();
}
