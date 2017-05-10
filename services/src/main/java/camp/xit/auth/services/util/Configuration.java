package camp.xit.auth.services.util;

import java.math.BigDecimal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private static final String COLLECTIONS_VALUE_SEPARATOR = "|";
    private final Map<String, Object> properties;


    public Configuration(Map<String, Object> properties) {
        this.properties = properties;
    }


    public String get(String name) {
        return String.valueOf(properties.get(name));
    }


    public String get(String name, String defaultValue) {
        String value = get(name);
        return value != null ? value : defaultValue;
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

}
