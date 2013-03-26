package eu.execom.fabut.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang3.StringUtils;

import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.graph.NodesList;

/**
 * Util class for reflection logic needed by testutil.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings("unchecked")
public final class ReflectionUtil {

    /** The Constant GET_METHOD_PREFIX. */
    private static final String GET_METHOD_PREFIX = "get";

    /** The Constant IS_METHOD_PREFIX. */
    private static final String IS_METHOD_PREFIX = "is";

    /** The Constant GET_ID. */
    private static final String GET_ID = "getId";

    /** The Constant SET_METHOD_PREFIX. */
    protected static final String SET_METHOD_PREFIX = "set";

    /**
     * Instantiates a new reflection util.
     */
    private ReflectionUtil() {
        super();
    }

    /**
     * Check if specified method of class Object is get method. Primitive boolean type fields have "is" for prefix of
     * their get method and all other types have "get" prefix for their get method so this method checks if field name
     * gotten from method name has a matched field name in the class X. Methods with prefix "is" have to have underlying
     * field of primitive boolean class.
     * 
     * @param classs
     *            class that method belongs to
     * @param method
     *            thats checking
     * @return <code>true</code> if method is "real" get method, <code>false</code> otherwise
     */
    public static boolean isGetMethod(final Class<?> classs, final Method method) {
        try {
            if (method.getName().startsWith(IS_METHOD_PREFIX)) {
                // if field type is primitive boolean
                return classs.getDeclaredField(getFieldName(method)).getType() == boolean.class;
            }
            return method.getName().startsWith(GET_METHOD_PREFIX) && findField(classs, getFieldName(method)) != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Get field name from specified get method. It differs get methods for regular objects and primitive boolean fields
     * by their get method prefix. ("is" is the prefix for primitive boolean get method)
     * 
     * @param method
     *            that is checked
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
     * @param fieldClass
     *            class of the field.
     * @param fieldName
     *            name of the field
     * @return {@link Field} with specified name, otherwise <code>null</code>>
     */
    public static Field findField(final Class<?> fieldClass, final String fieldName) {
        if (fieldClass == null) {
            return null;
        }
        try {
            return fieldClass.getDeclaredField(fieldName);
        } catch (final Exception e) {
            return findField(fieldClass.getSuperclass(), fieldName);
        }
    }

    /**
     * Determines if specified object is instance of {@link List}.
     * 
     * @param the
     *            generic type
     * @param object
     *            unidentified object
     * @return <code>true</code> if specified object is instance of {@link List} , <code>false</code> otherwise
     */
    public static boolean isListType(final Object object) {
        return object instanceof List;
    }

    /**
     * Check if specified class is contained in entity types.
     * 
     * @param object
     *            thats checked
     * @param entityTypes
     *            list of entity types
     * @return <code>true</code> if specified class is contained in entity types, <code>false</code> otherwise.
     */
    public static boolean isEntityType(final Class<?> object, final Map<AssertableType, List<Class<?>>> types) {

        if (types.get(AssertableType.ENTITY_TYPE) != null) {

            final boolean isEntity = types.get(AssertableType.ENTITY_TYPE).contains(object);

            // necessary tweek for hibernate beans witch in some cases are fetched as proxy objects
            final boolean isSuperClassEntity = types.get(AssertableType.ENTITY_TYPE).contains(object.getSuperclass());
            return isEntity || isSuperClassEntity;
        }
        return false;
    }

    /**
     * Check if specified class is contained in complex types.
     * 
     * @param classs
     *            thats checking
     * @param complexTypes
     *            the complex types
     * @return <code>true</code> if specified class is contained in complex types, <code>false</code> otherwise.
     */
    public static boolean isComplexType(final Class<?> classs, final Map<AssertableType, List<Class<?>>> types) {
        return types.get(AssertableType.COMPLEX_TYPE).contains(classs);
    }

    /**
     * Check if specified class is contained in ignored types.
     * 
     * @param classs
     *            thats checked
     * @param ignoredTypes
     *            list of ignored types
     * @return <code>true</code> if specified class is contained in ignored types, <code>false</code> otherwise.
     */
    public static boolean isIgnoredType(final Class<?> classs, final Map<AssertableType, List<Class<?>>> types) {
        return types.get(AssertableType.IGNORED_TYPE).contains(classs);
    }

    /**
     * Checks if object is ignored type.
     * 
     * 
     * @param firstObject
     *            that is checked
     * @param secondObject
     *            that is checked
     * @param ignoredTypes
     *            list of ignored type
     * @return <code>true</code> if type of expected or actual is ignored type, <code>false</code> otherwise.
     */
    public static boolean isIgnoredType(final Object firstObject, final Object secondObject,
            final Map<AssertableType, List<Class<?>>> types) {

        if (secondObject != null) {
            return isIgnoredType(secondObject.getClass(), types);
        }

        if (firstObject != null) {
            return isIgnoredType(firstObject.getClass(), types);
        }

        return false;
    }

    /**
     * Gets entity's id value.
     * 
     * @param type
     *            of entity
     * @param <Id>
     *            entities id type
     * @param entity
     *            - entity from which id is taken
     * @return {@link Number} if specified entity id field and matching get method, <code>null</code> otherwise.
     */
    public static Object getIdValue(final Object entity) {
        try {
            final Method method = entity.getClass().getMethod(GET_ID);
            return method.invoke(entity);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Extracts all "real" get methods for object of class Object in a list and returns them. "Real" get methods are
     * those methods who have matching property in the class with the name equal to get method's name uncapitalized and
     * without "get" prefix.
     * 
     * @param the
     *            generic type
     * @param object
     *            instance of class X
     * @param complexTypes
     *            the complex types
     * @param entityTypes
     *            the entity types
     * @return {@link List} of real "get" methods of class X
     */
    public static List<Method> getGetMethods(final Object object, final Map<AssertableType, List<Class<?>>> types) {

        final List<Method> getMethods = new ArrayList<Method>();
        final List<Method> getMethodsComplexType = new ArrayList<Method>();

        final Method[] allMethods = object.getClass().getMethods();
        for (final Method method : allMethods) {
            if (ReflectionUtil.isGetMethod(object.getClass(), method)) {
                // complex or entity type get methods inside object come last in
                // list
                if (isComplexType(method.getReturnType(), types) || isEntityType(method.getReturnType(), types)) {
                    getMethodsComplexType.add(method);
                } else {
                    getMethods.add(method);
                }
            }
        }
        getMethods.addAll(getMethodsComplexType);
        return getMethods;

    }

    /**
     * Gets the object get method named.
     * 
     * @param the
     *            generic type
     * @param methodName
     *            the method name
     * @param object
     *            the object
     * @return the object get method named
     */
    public static Method getGetMethod(final String methodName, final Object object) throws Exception {
        return object.getClass().getMethod(methodName);
    }

    /**
     * Gets the object type.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @param types
     *            the types
     * @return the object type
     */
    public static AssertableType getObjectType(final Object expected, final Object actual,
            final Map<AssertableType, List<Class<?>>> types) {
        if (expected == null && actual == null) {
            return AssertableType.PRIMITIVE_TYPE;
        }

        final Class<?> typeClass = actual != null ? actual.getClass() : expected.getClass();
        if (List.class.isAssignableFrom(typeClass)) {
            return AssertableType.LIST_TYPE;
        } else if (types.get(AssertableType.COMPLEX_TYPE).contains(typeClass)) {
            return AssertableType.COMPLEX_TYPE;
        } else if (types.get(AssertableType.ENTITY_TYPE).contains(typeClass)) {
            return AssertableType.ENTITY_TYPE;
        } else if (types.get(AssertableType.IGNORED_TYPE).contains(typeClass)) {
            return AssertableType.IGNORED_TYPE;
        } else {
            return AssertableType.PRIMITIVE_TYPE;
        }

    }

    /**
     * Creates a copy of specified object by creating instance with reflection and fills it using get and set method of
     * a class.
     * 
     * @param <T>
     *            type of the object
     * @param object
     *            object for copying
     * @param nodes
     *            list of objects that had been copied
     * @return copied entity
     */
    public static Object createCopyObject(final Object object, final NodesList nodes,
            final Map<AssertableType, List<Class<?>>> types) {

        Object copy = nodes.getExpected(object);
        if (copy != null) {
            return copy;
        }

        copy = createEmptyCopyOf(object);
        if (copy == null) {
            // FIXME is this even possible to happen? we need to add validation of util configuration, every DTO or
            // entity class need to have default constructor
            return copy;
        }
        nodes.addPair(copy, object);

        final Class<?> classObject = object.getClass();
        for (final Method method : classObject.getMethods()) {

            if (ReflectionUtil.isGetMethod(object.getClass(), method) && method.getParameterAnnotations().length == 0) {
                final String propertyName = ReflectionUtil.getFieldName(method);
                final Object propertyForCopying = getPropertyForCopying(object, method);
                final Object copiedProperty = copyProperty(propertyForCopying, nodes, types);
                if (!invokeSetMethod(method, classObject, propertyName, copy, copiedProperty)) {
                    return null;
                }
            }
        }
        return copy;
    }

    /**
     * Creates empty copy of object using reflection to call default constructor.
     * 
     * @param <T>
     *            type of copied object
     * @param object
     *            object for copying
     * @return copied empty instance of specified object or <code>null</code> if default constructor can not be called
     */
    public static <T> T createEmptyCopyOf(final T object) {
        try {
            return (T) object.getClass().getConstructor().newInstance();
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * FIXME comment is not OK, this method is just calling method of a some object...
     * 
     * Gets property for copying using reflection.
     * 
     * @param <T>
     *            type of the object
     * @param object
     *            property's parent
     * @param method
     *            get method for property
     * @return property
     */
    public static <T> Object getPropertyForCopying(final T object, final Method method) {
        try {
            return method.invoke(object);
        } catch (final Exception e) {
            throw new AssertionFailedError(e.getMessage());
        }
    }

    /**
     * Copies property.
     * 
     * @param propertyForCopying
     *            property for copying
     * @param nodes
     *            list of objects that had been copied
     * @return copied property
     */
    public static Object copyProperty(final Object propertyForCopying, final NodesList nodes,
            final Map<AssertableType, List<Class<?>>> types) {
        if (propertyForCopying == null) {
            // its null we shouldn't do anything
            return null;
        }

        if (ReflectionUtil.isComplexType(propertyForCopying.getClass(), types)) {
            // its complex object, we need its copy
            return createCopyObject(propertyForCopying, nodes, types);
        }

        if (ReflectionUtil.isListType(propertyForCopying)) {
            // just creating new list with same elements
            return copyList((List<?>) propertyForCopying);
        }

        // if its not list or some complex type same object will be added.
        return propertyForCopying;

    }

    /**
     * Creates a copy of specified list.
     * 
     * @param <T>
     *            type objects in the list
     * @param list
     *            list for copying
     * @return copied list
     */
    public static <T> List<T> copyList(final List<T> list) {
        return new ArrayList<T>(list);
    }

    /**
     * Create copy of specified object and return its copy.
     * 
     * @param object
     *            object for copying
     * @return copied object
     */
    public static Object createCopy(final Object object, final Map<AssertableType, List<Class<?>>> types) {
        if (object == null) {
            return null;
        }

        if (ReflectionUtil.isListType(object)) {
            final List<?> list = (List<?>) object;
            return copyList(list);
        }

        return createCopyObject(object, new NodesList(), types);
    }

    /**
     * Invokes specified set method via reflection to set property to object.
     * 
     * @param <T>
     *            object type
     * @param method
     *            get method for property
     * @param classObject
     *            parent class for property
     * @param propertyName
     *            property name
     * @param object
     *            copied parent object
     * @param copiedProperty
     *            copied property
     * @return <code>true</code> if set method exists and it's successfully invoked, otherwise <code>false</code>.
     */
    public static <T> boolean invokeSetMethod(final Method method, final Class<?> classObject,
            final String propertyName, final T object, final Object copiedProperty) {
        Method setMethod = null;

        try {
            setMethod = classObject.getMethod(SET_METHOD_PREFIX + StringUtils.capitalize(propertyName),
                    method.getReturnType());
            setMethod.invoke(object, copiedProperty);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

}
