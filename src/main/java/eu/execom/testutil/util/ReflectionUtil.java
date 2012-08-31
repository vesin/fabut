package eu.execom.testutil.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Util class for reflection logic needed by testutil.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class ReflectionUtil {

    private static final String GET_METHOD_PREFIX = "get";
    private static final String IS_METHOD_PREFIX = "is";

    /**
     * Check if specified method of class X is get method. Primitive boolean type fields have "is" for prefix of their
     * get method and all other types have "get" prefix for their get method so this method checks if field name gotten
     * from method name has a matched field name in the class X. Methods with prefix "is" have to have underlying field
     * of primitive boolean class.
     * 
     * @param object
     * @param method
     * @return <code>true</code> if method is "real" get method, <code>false</code> otherwise
     */
    public static <X> boolean isGetMethod(final X object, final Method method) {
        try {
            if (method.getName().startsWith(IS_METHOD_PREFIX)) {
                // if field type is primitive boolean
                return object.getClass().getDeclaredField(getFieldName(method)).getType() == boolean.class;
            }
            return method.getName().startsWith(GET_METHOD_PREFIX)
                    && findFieldInInheritance(object.getClass(), getFieldName(method)) != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Get field name from specified get method. It differs get methods for regular objects and primitive boolean fields
     * by their get method prefix. ("is" is the prefix for primitive boolean get method)
     * 
     * @param method
     * @return field name represented by specified get method
     */
    public static String getFieldName(final Method method) {
        String fieldName;
        if (method.getName().startsWith(IS_METHOD_PREFIX)) {
            fieldName = StringUtils.removeStart(method.getName(), IS_METHOD_PREFIX);
        } else {
            fieldName = StringUtils.removeStart(method.getName(), GET_METHOD_PREFIX);
        }
        return StringUtils.uncapitalize(fieldName);
    }

    /**
     * Searches trough property class inheritance tree for field with specified name. Starting from property class
     * method recursively climbs higher in the inheritance tree until it finds field with specified name or reached
     * object in which case returns null.
     * 
     * @param propertyClass
     * @param fieldName
     * @return {@link Field} with specified name, otherwise <code>null</code>>
     */
    public static Field findFieldInInheritance(final Class<?> propertyClass, final String fieldName) {
        if (propertyClass == null) {
            return null;
        }
        try {
            return propertyClass.getDeclaredField(fieldName);
        } catch (final Exception e) {
            return findFieldInInheritance(propertyClass.getSuperclass(), fieldName);
        }
    }

    /**
     * Determines if specified object is instance of {@link List}.
     * 
     * @param object
     *            - unidentified object
     * @return <code>true</code> if specified object is instance of {@link List} , <code>false</code> otherwise
     */
    public static <X> boolean isListType(final X object) {
        return object instanceof List;
    }

    /**
     * Check if specified class is contained in entity types.
     * 
     * @param type
     *            - unidentified type
     * @return <code>true</code> if specified class is contained in entity types, <code>false</code> otherwise.
     */
    public static <X> boolean isEntityType(final X object, final List<Class<?>> entityTypes) {
        return entityTypes.contains(object.getClass());
    }

    /**
     * Check if specified class is contained in complex types.
     * 
     * @param type
     *            - unidentified type
     * @return <code>true</code> if specified class is contained in complex types, <code>false</code> otherwise.
     */
    public static <X> boolean isComplexType(final X object, final List<Class<?>> complexTypes) {
        return complexTypes.contains(object.getClass());
    }

    /**
     * Check if specified class is contained in ignored types.
     * 
     * @param type
     *            unidentified type
     * @return <code>true</code> if specified class is contained in ignored types, <code>false</code> otherwise.
     */
    public static <X> boolean isIgnoredType(final X object, final List<Class<?>> ignoredTypes) {
        return ignoredTypes.contains(object.getClass());
    }

    /**
     * Checks if object is ignored type.
     * 
     * @param <X>
     *            the generic type
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @return - <code>true</code> if type of expected or actual is ignored type, <code>false</code> otherwise.
     */
    public static <X> boolean isIgnoredType(final X expected, final X actual, final List<Class<?>> ignoredTypes) {

        if (actual != null) {
            return isIgnoredType(actual, ignoredTypes);
        }

        if (expected != null) {
            return isIgnoredType(expected, ignoredTypes);
        }

        return false;
    }

}
