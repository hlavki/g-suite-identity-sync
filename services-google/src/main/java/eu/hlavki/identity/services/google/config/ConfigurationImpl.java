package eu.hlavki.identity.services.google.config;

import eu.hlavki.identity.services.google.NoPrivateKeyException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationImpl implements Configuration {

    static final String CLIENT_ID_PROP = "oauth2.serviceAccount.clientId";
    static final String SUBJECT_PROP = "oauth2.serviceAccount.subject";
    static final String PRIVATE_KEY_PROP = "oauth2.serviceAccount.privateKey.file";
    static final String PRIVATE_KEY_PASS_PROP = "oauth2.serviceAccount.privateKey.passphrase";
    static final String GSUITE_DOMAIN_PROP = "gsuite.domain";
    static final String GSUITE_IMPLICIT_GROUP = "gsuite.implicit.group";
    static final String TOKEN_LIFETIME_PROP = "gsuite.serviceAccount.tokenLifetime";
    static final long TOKEN_LIFETIME_DEFAULT = 3600;

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationImpl.class);
    private static final String PID = "eu.hlavki.identity.google";
    public static final String CONFIG_PROP = "config";
    private static final String COLLECTIONS_VALUE_SEPARATOR = "|";
    private org.osgi.service.cm.Configuration osgiConfig;
    private final ConfigurationAdmin cfgAdmin;


    public ConfigurationImpl(ConfigurationAdmin cfgAdmin) {
        this.cfgAdmin = cfgAdmin;
        try {
            this.osgiConfig = this.cfgAdmin.getConfiguration(PID);
        } catch (IOException e) {
            LOG.warn("Can't load configuration", e);
        }
    }


    public String get(String name) {
        Object value = osgiConfig.getProperties().get(name);
        return value != null ? String.valueOf(value) : null;
    }


    public String get(String name, String defaultValue) {
        String value = get(name);
        return value != null ? value : defaultValue;
    }


    public boolean isSet(String name) {
        return get(name) != null;
    }


    public Long getLong(String name, Long defaultValue) {
        Long result = defaultValue;
        try {
            String strValue = get(name);
            if (strValue != null) {
                result = Long.parseLong(strValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage(), e);
        }
        return result;
    }


    public Long getLong(String name) {
        return getLong(name, null);
    }


    public Integer getInt(String name) {
        return getInt(name, null);
    }


    public Integer getInt(String name, Integer defaultValue) {
        Integer result = defaultValue;
        try {
            String strValue = get(name);
            if (strValue != null) {
                result = Integer.parseInt(strValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage(), e);
        }
        return result;
    }


    public Boolean getBoolean(String name, Boolean defaultValue) {
        String strValue = get(name);
        return (strValue != null) ? Boolean.valueOf(strValue) : defaultValue;
    }


    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }


    public Double getDouble(String key) {
        return getDouble(key, null);
    }


    public Double getDouble(String key, Double defaultValue) {
        Double result = defaultValue;
        try {
            String strValue = get(key);
            if (strValue != null) {
                result = Double.parseDouble(strValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage(), e);
        }
        return result;
    }


    public BigDecimal getDecimal(String name, BigDecimal defaultValue) {
        BigDecimal result = defaultValue;
        try {
            String strValue = get(name);
            if (strValue != null) {
                result = new BigDecimal(strValue);
            }
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage(), e);
        }
        return result;
    }


    public BigDecimal getDecimal(String name) {
        return getDecimal(name, null);
    }


    public Set<String> getSet(String name) {
        return getSet(name, COLLECTIONS_VALUE_SEPARATOR);
    }


    public Set<String> getSet(String name, String separator) {
        Set<String> result;
        String str = get(name);
        if (str == null || "".equals(str)) {
            result = Collections.<String>emptySet();
        } else {
            StringTokenizer t = new StringTokenizer(str, separator);
            result = new HashSet<>();
            while (t.hasMoreTokens()) {
                result.add(t.nextToken().trim());
            }
        }
        return result;
    }


    public List<List<String>> getListOfList(String name) {
        return getListOfList(name, COLLECTIONS_VALUE_SEPARATOR);
    }


    public List<List<String>> getListOfList(String name, String separator) {
        List<List<String>> result = new ArrayList<>();
        int idx = 0;
        while (get(name + "." + String.valueOf(idx)) != null) {
            result.add(getList(name + "." + String.valueOf(idx++), separator));
        }
        if (result.isEmpty() && get(name) != null) {
            result.add(getList(name));
        }
        return result;
    }


    public List<String> getList(String name) {
        return getList(name, COLLECTIONS_VALUE_SEPARATOR);
    }


    public List<String> getList(String name, String separator) {
        List<String> result;
        String str = get(name);
        if (str == null || "".equals(str)) {
            result = Collections.<String>emptyList();
        } else {
            StringTokenizer t = new StringTokenizer(str, separator);
            result = new ArrayList<>();
            while (t.hasMoreTokens()) {
                result.add(t.nextToken().trim());
            }
        }
        return result;
    }


    @Override
    public String getGSuiteDomain() {
        return get(GSUITE_DOMAIN_PROP);
    }


    @Override
    public String getGSuiteImplicitGroup() {
        return get(GSUITE_IMPLICIT_GROUP);
    }


    @Override
    public boolean isSetGSuiteImplicitGroup() {
        return isSet(GSUITE_IMPLICIT_GROUP);
    }


    @Override
    public String getServiceAccountClientId() {
        return get(CLIENT_ID_PROP);
    }


    @Override
    public String getServiceAccountSubject() {
        return get(SUBJECT_PROP);
    }


    @Override
    public String getPrivateKeyLocation() {
        return get(PRIVATE_KEY_PROP);
    }


    @Override
    public PrivateKey getServiceAccountKey() throws NoPrivateKeyException {
        String keyFile = get(PRIVATE_KEY_PROP);
        PrivateKey result = null;
        if (keyFile != null) {
            try (InputStream is = new FileInputStream(keyFile)) {
                KeyStore store = KeyStore.getInstance("PKCS12");
                char[] password = get(PRIVATE_KEY_PASS_PROP).toCharArray();
                store.load(is, password);
                result = (PrivateKey) store.getKey("privateKey", password);
            } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                throw new NoPrivateKeyException("Could not load private key", e);
            }
        }
        return result;
    }


    @Override
    public long getServiceAccountTokenLifetime() {
        return getLong(TOKEN_LIFETIME_PROP, TOKEN_LIFETIME_DEFAULT);
    }
}
