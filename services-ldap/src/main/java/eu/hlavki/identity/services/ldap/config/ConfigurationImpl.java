package eu.hlavki.identity.services.ldap.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationImpl implements Configuration {

    private static final String LDAP_USERS_BASE_DN_PROP = "ldap.users.baseDN";
    private static final String LDAP_BASE_DN_PROP = "ldap.baseDN";
    private static final String LDAP_GROUPS_BASE_DN_PROP = "ldap.groups.baseDN";
    private static final String LDAP_GROUPS_OBJECT_CLASS_PROP = "ldap.groups.objectClass";
    private static final String LDAP_GROUPS_OBJECT_CLASS_DEFAULT = "groupOfUniqueNames";
    private static final String LDAP_GROUPS_MEMBER_ATTR_PROP = "ldap.groups.memberAttr";
    private static final String LDAP_GROUPS_MEMBER_ATTR_DEFAULT = "uniqueMember";
    private static final String LDAP_USER_ATTR_PROP = "ldap.users.attr";
    private static final String LDAP_USER_ATTR_DEFAULT = "uid";

    private static final Logger log = LoggerFactory.getLogger(ConfigurationImpl.class);
    private static final String PID = "eu.hlavki.identity.ldap";
    public static final String CONFIG_PROP = "config";
    private static final String COLLECTIONS_VALUE_SEPARATOR = "|";
    private org.osgi.service.cm.Configuration osgiConfig;
    private final ConfigurationAdmin cfgAdmin;


    public ConfigurationImpl(ConfigurationAdmin cfgAdmin) {
        this.cfgAdmin = cfgAdmin;
        try {
            this.osgiConfig = this.cfgAdmin.getConfiguration(PID);
        } catch (IOException e) {
            log.warn("Can't load configuration", e);
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
            log.warn(e.getMessage(), e);
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
            log.warn(e.getMessage(), e);
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
            log.warn(e.getMessage(), e);
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
            log.warn(e.getMessage(), e);
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
    public String getBaseDN() {
        return get(LDAP_BASE_DN_PROP);
    }


    @Override
    public String getLdapUserBaseDN() {
        return get(LDAP_USERS_BASE_DN_PROP) + "," + getBaseDN();
    }


    @Override
    public String getLdapGroupsBaseDN() {
        return get(LDAP_GROUPS_BASE_DN_PROP) + "," + getBaseDN();
    }


    @Override
    public String getLdapGroupsObjectClass() {
        return get(LDAP_GROUPS_OBJECT_CLASS_PROP, LDAP_GROUPS_OBJECT_CLASS_DEFAULT);
    }


    @Override
    public String getLdapGroupsMemberAttr() {
        return get(LDAP_GROUPS_MEMBER_ATTR_PROP, LDAP_GROUPS_MEMBER_ATTR_DEFAULT);
    }

    @Override
    public String getUserAttr() {
        return get(LDAP_USER_ATTR_PROP, LDAP_USER_ATTR_DEFAULT);
    }
}
