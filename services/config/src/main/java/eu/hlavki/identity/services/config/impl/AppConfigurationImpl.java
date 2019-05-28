package eu.hlavki.identity.services.config.impl;

import eu.hlavki.identity.services.config.AppConfiguration;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfigurationImpl implements AppConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfigurationImpl.class);

    private static final String PID = "eu.hlavki.identity";
    private static final String EXTERNAL_USER_GROUP_PROP = "gsuite.external.accounts.group";
    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("([^@]*)@([^@]*)");

    public static final String CONFIG_PROP = "config";

    private static final String COLLECTIONS_VALUE_SEPARATOR = ",";
    private org.osgi.service.cm.Configuration osgiConfig;
    private final ConfigurationAdmin cfgAdmin;


    public AppConfigurationImpl(ConfigurationAdmin cfgAdmin) {
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


    public Optional<String> getOpt(String name) {
        return Optional.ofNullable(get(name));
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


    public void update(Consumer<Dictionary<String, Object>> consumer) {
        try {
            Dictionary<String, Object> props = osgiConfig.getProperties();
            consumer.accept(props);
            osgiConfig.update(props);
        } catch (IOException e) {
            LOG.warn("Cannot update configuration", e);
        }
    }


    public void set(String name, Object value) {
        update(props -> {
            if (value == null) props.remove(name);
            else props.put(name, value);
        });
    }


    public void remove(String name) {
        update(props -> {
            props.remove(name);
        });
    }


    @Override
    public Optional<String> getExternalAccountsGroup() {
        return getOpt(EXTERNAL_USER_GROUP_PROP);
    }


    @Override
    public void setExternalAccountsGroup(String groupName) {
        Matcher matcher = GROUP_NAME_PATTERN.matcher(groupName);
        set(EXTERNAL_USER_GROUP_PROP, matcher.matches() ? matcher.group(1) : null);
    }
}
