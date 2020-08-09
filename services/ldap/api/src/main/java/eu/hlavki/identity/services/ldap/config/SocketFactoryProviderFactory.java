package eu.hlavki.identity.services.ldap.config;

import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustStoreTrustManager;
import java.security.GeneralSecurityException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketFactoryProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(SocketFactoryProviderFactory.class);

    private final boolean enableTls;
    private final String trustStorePath;
    private final char[] trustStorePassword;


    public SocketFactoryProviderFactory(String enableTls, String trustStorePath, String trustStorePassword) {
        this.enableTls = Boolean.parseBoolean(enableTls);
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword.toCharArray();
    }


    public SocketFactory getSocketFactory() {
        log.info("TLS Enabled: {}", enableTls);
        SocketFactory result;
        if (enableTls) {
            result = getSSLSocketFactory();
        } else {
            result = SocketFactory.getDefault();
        }
        return result;
    }


    private SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLUtil sslUtil = new SSLUtil(new TrustStoreTrustManager(trustStorePath, trustStorePassword, "jks", true));
            return sslUtil.createSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
