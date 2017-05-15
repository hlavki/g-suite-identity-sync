package camp.xit.identity.services.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michal Hlavac <hlavki@hlavki.eu>
 */
public class StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";


    private static void toString(String alias, Object obj, StringBuffer sb, List<Object> objects) {
        if (obj == null) {
            return;
        }
        if (objects.contains(obj)) {
            return;
        }
        objects.add(obj);
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                PropertyDescriptor descriptor = descriptors[i];
                // workaround for JAXB bug. Read method with Boolean result type and prefix is
                Method readMethod = descriptor.getReadMethod();
                if (readMethod == null && Boolean.class.equals(descriptor.getPropertyType())) {
                    String name = descriptor.getName();
                    String readMethodStr = "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    readMethod = obj.getClass().getMethod(readMethodStr);
                }
                if (readMethod == null || readMethod.getDeclaringClass().equals(Object.class)) {
                    log.debug("property defined on Object class: skipping: " + descriptor.getName());
                    continue;
                }
                Object value = null;
                try {
                    value = readMethod.invoke(obj, (Object[]) null);
                } catch (Throwable ex) {
                    log.debug("Can't read value of attribute " + obj.getClass().getName() + "." + descriptor.getName());
                }
                if (value == null) {
                    log.debug("property value is null: skipping: " + descriptor.getName());
                    continue;
                }
                Class<?> returnType = readMethod.getReturnType();
                if (!returnType.isPrimitive() && !returnType.isArray() && !returnType.getName().startsWith("java.")
                        && !returnType.isEnum()) {
                    log.debug("property is unknown type or not primitive [" + returnType.getName() + "]");
                    String simpleName = value.getClass().getName().substring(returnType.getName().lastIndexOf('.') + 1);
                    sb.append(alias).append(".").append(descriptor.getName()).append(" = new ").append(simpleName).append("()\n");
                    toString(alias + "." + descriptor.getName(), value, sb, objects);
                    continue;
                }
                if (value instanceof java.util.Collection<?>) {
                    try {
                        Iterator<?> iter = ((Collection<?>) value).iterator();
                        int num = 0;
                        while (iter.hasNext()) {
                            Object item = iter.next();
                            toString(alias + "." + descriptor.getName() + "[" + num + "]", item, sb, objects);
                            num++;
                        }
                    } catch (Throwable t) {
                        log.debug("Can't read value of attribute: " + alias + "." + descriptor.getName());
                    }
                    continue;
                } else if (value.getClass().isArray()) {
                    try {
                        Object[] valueArray = (Object[]) value;
                        for (int idx = 0; idx < valueArray.length; idx++) {
                            toString(alias + "." + descriptor.getName() + "[" + idx + "]", valueArray[idx], sb, objects);
                        }
                    } catch (Throwable t) {
                        log.debug("Can't read value of attribute: " + alias + "." + descriptor.getName());
                    }
                    continue;
                } else if (value instanceof java.util.Calendar) {
                    DateFormat format = new SimpleDateFormat(DATE_FORMAT);
                    value = format.format(((java.util.Calendar) value).getTime());
                }
                sb.append(alias).append(".").append(descriptor.getName()).append(" = ").append(String.valueOf(value)).append('\n');
            }
        } catch (Exception e) {
            log.error("hql example criterion building error", e);
        }
    }


    public static String objectToString(String alias, Object obj) {
        StringBuffer buff = new StringBuffer();
        toString(alias, obj, buff, new ArrayList<>());
        return buff.toString();
    }


    public static String collectionToString(String alias, Collection<?> collection) {
        StringBuffer buff = new StringBuffer();
        int idx = 0;
        for (Object obj : collection) {
            toString(alias + "[" + idx++ + "]", obj, buff, new ArrayList<>());
        }
        return buff.toString();
    }


    public static String toStringLine(Object obj) {
        if (obj == null) {
            return "";
        }
        String clazz = obj.getClass().getName();
        String simpleClazz = clazz.substring(clazz.lastIndexOf('.') + 1);
        StringBuffer sb = new StringBuffer(simpleClazz).append("[");
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                PropertyDescriptor descriptor = descriptors[i];
                if (descriptor.getPropertyType() == null
                        || Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
                    log.debug("collection handling not supported: skipping property: " + descriptor.getName());
                    continue;
                }
                if (descriptor.getReadMethod().getDeclaringClass().equals(Object.class)) {
                    log.debug("property defined on Object class: skipping: " + descriptor.getName());
                    continue;
                }
                Object value = descriptor.getReadMethod().invoke(obj, (Object[]) null);
                if (value == null) {
                    log.debug("property value is null: skipping: " + descriptor.getName());
                    continue;
                }
                Class<?> returnType = descriptor.getReadMethod().getReturnType();
                if (!returnType.isPrimitive() && !returnType.getName().startsWith("java.") && !returnType.isEnum()) {
                    log.debug("property is unknown type or not primitive [" + returnType.getName() + "]");
                    continue;
                }
                sb.append(descriptor.getName()).append(" = ").append(value).append(" | ");
            }
        } catch (Exception e) {
            log.error("hql example criterion building error", e);
            return null;
        }
        sb.append("]");
        return sb.toString();
    }


    public static String arrayToString(Object[] objs) {
        StringBuilder sb = new StringBuilder("[");
        for (int idx = 0; idx < objs.length; idx++) {
            Object obj = objs[idx];
            if (obj == null) {
                sb.append("null");
            } else if (obj.getClass().getName().startsWith("java.")) {
                sb.append(obj.toString());
            } else {
                toStringLine(obj);
            }
            if (idx < objs.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
