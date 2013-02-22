package eu.execom.testutil.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.execom.testutil.ITestUtil;
import eu.execom.testutil.enums.ObjectType;

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

    private static final String FIND_ALL = "findAll";
    private static final String GET_ENTITY_TYPES = "getEntityTypes";
    private static final String GET_COMPLEX_TYPES = "getComplexTypes";
    private static final String GET_IGNORED_TYPES = "getIgnoredTypes";
    private static final String FIND_BY_ID = "findById";

    /**
     * Instantiates a new reflection util.
     */
    private ReflectionUtil() {
        super();
    }

    /**
     * Check if specified method of class X is get method. Primitive boolean type fields have "is" for prefix of their
     * get method and all other types have "get" prefix for their get method so this method checks if field name gotten
     * from method name has a matched field name in the class X. Methods with prefix "is" have to have underlying field
     * of primitive boolean class.
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
            return method.getName().startsWith(GET_METHOD_PREFIX)
                    && findFieldInInheritance(classs, getFieldName(method)) != null;
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
    public static Field findFieldInInheritance(final Class<?> fieldClass, final String fieldName) {
        if (fieldClass == null) {
            return null;
        }
        try {
            return fieldClass.getDeclaredField(fieldName);
        } catch (final Exception e) {
            return findFieldInInheritance(fieldClass.getSuperclass(), fieldName);
        }
    }

    /**
     * Determines if specified object is instance of {@link List}.
     * 
     * @param <X>
     *            the generic type
     * @param object
     *            unidentified object
     * @return <code>true</code> if specified object is instance of {@link List} , <code>false</code> otherwise
     */
    public static <X> boolean isListType(final X object) {
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
    public static boolean isEntityType(final Class<?> object, final List<Class<?>> entityTypes) {

        final boolean isEntity = entityTypes.contains(object);

        // necessary tweek for hibernate beans witch in some cases are fetched as proxy objects
        final boolean isSuperClassEntity = entityTypes.contains(object.getSuperclass());

        return isEntity || isSuperClassEntity;
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
    public static boolean isComplexType(final Class<?> classs, final List<Class<?>> complexTypes) {
        return complexTypes.contains(classs);
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
    public static boolean isIgnoredType(final Class<?> classs, final List<Class<?>> ignoredTypes) {
        return ignoredTypes.contains(classs);
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
            final List<Class<?>> ignoredTypes) {

        if (secondObject != null) {
            return isIgnoredType(secondObject.getClass(), ignoredTypes);
        }

        if (firstObject != null) {
            return isIgnoredType(firstObject.getClass(), ignoredTypes);
        }

        return false;
    }

    /**
     * Gets entity's id value.
     * 
     * @param <X>
     *            type of entity
     * @param <Id>
     *            entities id type
     * @param entity
     *            - entity from which id is taken
     * @return {@link Number} if specified entity id field and matching get method, <code>null</code> otherwise.
     */
    public static <X, Id> Id getIdValue(final X entity) {
        try {
            final Method method = entity.getClass().getMethod(GET_ID);
            return (Id) method.invoke(entity);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Extracts all "real" get methods for object of class X in a list and returns them. "Real" get methods are those
     * methods who have matching property in the class with the name equal to get method's name uncapitalized and
     * without "get" prefix.
     * 
     * @param <X>
     *            the generic type
     * @param object
     *            instance of class X
     * @param complexTypes
     *            the complex types
     * @param entityTypes
     *            the entity types
     * @return {@link List} of real "get" methods of class X
     */
    public static <X> List<Method> getObjectGetMethods(final X object, final Map<ObjectType, List<Class<?>>> types) {

        // final List<Method> getMethods = new ArrayList<Method>();
        // final List<Method> getMethodsComplexType = new ArrayList<Method>();
        //
        // final Method[] allMethods = object.getClass().getMethods();
        // for (final Method method : allMethods) {
        // if (ReflectionUtil.isGetMethod(object.getClass(), method)) {
        // // complex or entity type get methods inside object come last in
        // // list
        // if (complexTypes.contains(method.getReturnType()) || entityTypes.contains(method.getReturnType())) {
        // getMethodsComplexType.add(method);
        // } else {
        // getMethods.add(method);
        // }
        // }
        // }
        // getMethods.addAll(getMethodsComplexType);
        // return getMethods;
        // TODO requires new implementation
        return null;
    }

    /**
     * Gets the object get method named.
     * 
     * @param <X>
     *            the generic type
     * @param methodName
     *            the method name
     * @param object
     *            the object
     * @return the object get method named
     */
    public static <X> Method getObjectGetMethodNamed(final String methodName, final X object) throws Exception {
        return object.getClass().getMethod(methodName);
    }

    public static Method getFindAllMethod(final Class<?> declaringClass) {
        try {
            final Object testIntance = declaringClass.newInstance();
            if (testIntance instanceof ITestUtil) {
                final Method method = declaringClass.getMethod("findAll", Class.class);
            } else {
                throw new IllegalStateException("Test: " + declaringClass.getName() + ", has to implement ITestUtil!");
            }
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // TODO(nolah) refactor his, duplicate code
    public static List<Class<?>> getEntityTypes(final Class<?> testClass) {
        final Object testInstance = ReflectionUtil.newInstance(testClass);
        try {
            final Method getEntityTypesMethod = testClass.getMethod(GET_ENTITY_TYPES);
            return (List<Class<?>>) getEntityTypesMethod.invoke(testInstance);
        } catch (final Exception e) {
            return null;
        }
    }

    public static List<Class<?>> getComplexTypes(final Class<?> testClass) {
        final Object testInstance = ReflectionUtil.newInstance(testClass);
        try {
            final Method getEntityTypesMethod = testClass.getMethod(GET_COMPLEX_TYPES);
            return (List<Class<?>>) getEntityTypesMethod.invoke(testInstance);
        } catch (final Exception e) {
            return null;
        }
    }

    public static List<Class<?>> getIgnoredTypes(final Class<?> testClass) {
        final Object testInstance = ReflectionUtil.newInstance(testClass);
        try {
            final Method getEntityTypesMethod = testClass.getMethod(GET_IGNORED_TYPES);
            return (List<Class<?>>) getEntityTypesMethod.invoke(testInstance);
        } catch (final Exception e) {
            return null;
        }
    }

    public static Object newInstance(final Class<?> testClass) {
        try {
            return testClass.newInstance();
        } catch (final IllegalAccessException e) {
            return null;
        } catch (final InstantiationException e) {
            throw new IllegalStateException("Test: " + testClass.getSimpleName() + " must have default constructor");
        }
    }

    public static Class<?> getTestClassFromStackTrace() {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // TODO find first class in stack trace that implements ITestUtil ...
        final String className = stackTraceElements[3].getClassName();
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Test class: " + className + " not found!");
        }
    }

    public static List<?> findAll(final Class<?> testClass, final Class<?> entityClass) {
        try {

            final Object testInstance = ReflectionUtil.newInstance(testClass);
            if (testInstance instanceof ITestUtil) {
                final Method findAll = testClass.getMethod(FIND_ALL, Class.class);
                return (List<?>) findAll.invoke(testInstance, entityClass);
            } else {
                throw new IllegalStateException("NOT INTSTANCE OF ITESTUTIL");
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Method findAll not found");
        }
    }

    public static Object findById(final Class<?> testClass, final Class<?> entityClass, final Object id) {
        final Object testIntstance = ReflectionUtil.newInstance(testClass);
        try {
            final Method findById = testClass.getMethod(FIND_BY_ID, entityClass, Object.class);
            return findById.invoke(testIntstance, entityClass, id);
        } catch (final Exception e) {
            throw new IllegalStateException("findById is uninvokable!");
        }
    }

    public static ObjectType getObjectType(final Object expected, final Object actual,
            final Map<ObjectType, List<Class<?>>> types) {
        // TODO IMPLEMENT THIS
        return null;
    }
}
