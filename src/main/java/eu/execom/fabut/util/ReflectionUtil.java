package eu.execom.fabut.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang3.StringUtils;

import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.exception.CopyException;
import eu.execom.fabut.graph.NodesList;

/**
 * Util class for reflection logic needed by testutil.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
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
	 * Check if specified method of class Object is get method. Primitive
	 * boolean type fields have "is" for prefix of their get method and all
	 * other types have "get" prefix for their get method so this method checks
	 * if field name gotten from method name has a matched field name in the
	 * class X. Methods with prefix "is" have to have underlying field of
	 * primitive boolean class.
	 * 
	 * @param clazz class that method belongs to
	 * @param method thats checking
	 * @return <code>true</code> if method is "real" get method,
	 *         <code>false</code> otherwise
	 */
	public static boolean isGetMethod(final Class<?> clazz, final Method method) {
		if (method.getName().startsWith(IS_METHOD_PREFIX)) {
			// if field type is primitive boolean
			return findField(clazz, getFieldName(method)) != null && findField(clazz, getFieldName(method)).getType() == boolean.class;
		} else if (method.getName().startsWith(GET_METHOD_PREFIX)) {
			return method.getName().startsWith(GET_METHOD_PREFIX) && findField(clazz, getFieldName(method)) != null;
		} else {
			return findField(clazz, method.getName()) != null && method.getReturnType().equals(findField(clazz, method.getName()).getType());
		}
	}

	/**
	 * Get field name from specified get method. It differs get methods for
	 * regular objects and primitive boolean fields by their get method prefix.
	 * ("is" is the prefix for primitive boolean get method)
	 * 
	 * @param method that is checked
	 * @return field name represented by specified get method
	 */
	public static String getFieldName(final Method method) {
		String fieldName;
		if (method.getName().startsWith(IS_METHOD_PREFIX)) {
			fieldName = StringUtils.removeStart(method.getName(), IS_METHOD_PREFIX);
		} else if (method.getName().startsWith(GET_METHOD_PREFIX)) {
			fieldName = StringUtils.removeStart(method.getName(), GET_METHOD_PREFIX);
		} else {
			fieldName = method.getName();
		}
		return StringUtils.uncapitalize(fieldName);
	}

	/**
	 * Searches trough property class inheritance tree for field with specified
	 * name. Starting from property class method recursively climbs higher in
	 * the inheritance tree until it finds field with specified name or reached
	 * object in which case returns null.
	 * 
	 * @param fieldClass class of the field.
	 * @param fieldName name of the field
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
	 * @param the generic type
	 * @param object unidentified object
	 * @return <code>true</code> if specified object is instance of {@link List}
	 *         , <code>false</code> otherwise
	 */
	public static boolean isListType(final Object object) {
		return object instanceof List;
	}

	/**
	 * Determines if specified object is instance of {@link Map}.
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isMapType(final Object object) {
		return object instanceof Map;
	}

	/**
	 * Check if specified class is contained in entity types.
	 * 
	 * @param object thats checked
	 * @param entityTypes list of entity types
	 * @return <code>true</code> if specified class is contained in entity
	 *         types, <code>false</code> otherwise.
	 */
	public static boolean isEntityType(final Class<?> object, final Map<AssertableType, List<Class<?>>> types) {

		if (types.get(AssertableType.ENTITY_TYPE) != null) {

			final boolean isEntity = types.get(AssertableType.ENTITY_TYPE).contains(object);

			// necessary tweek for hibernate beans witch in some cases are
			// fetched as proxy objects
			final boolean isSuperClassEntity = types.get(AssertableType.ENTITY_TYPE).contains(object.getSuperclass());
			return isEntity || isSuperClassEntity;
		}
		return false;
	}

	/**
	 * Check if specified class is contained in complex types.
	 * 
	 * @param classs thats checking
	 * @param complexTypes the complex types
	 * @return <code>true</code> if specified class is contained in complex
	 *         types, <code>false</code> otherwise.
	 */
	public static boolean isComplexType(final Class<?> classs, final Map<AssertableType, List<Class<?>>> types) {
		return types.get(AssertableType.COMPLEX_TYPE).contains(classs);
	}

	/**
	 * Check if specified class is contained in ignored types.
	 * 
	 * @param classs thats checked
	 * @param ignoredTypes list of ignored types
	 * @return <code>true</code> if specified class is contained in ignored
	 *         types, <code>false</code> otherwise.
	 */
	public static boolean isIgnoredType(final Class<?> classs, final Map<AssertableType, List<Class<?>>> types) {
		return types.get(AssertableType.IGNORED_TYPE).contains(classs);
	}

	/**
	 * Checks if object is ignored type.
	 * 
	 * @param firstObject that is checked
	 * @param secondObject that is checked
	 * @param ignoredTypes list of ignored type
	 * @return <code>true</code> if type of expected or actual is ignored type,
	 *         <code>false</code> otherwise.
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
	 * @param type of entity
	 * @param <Id> entities id type
	 * @param entity - entity from which id is taken
	 * @return {@link Number} if specified entity id field and matching get
	 *         method, <code>null</code> otherwise.
	 */
	public static Object getIdValue(final Object entity) {
		try {
			Method getMethod = entity.getClass().getMethod(GET_ID);
			return getMethod.invoke(entity);
		} catch (final Exception e) {
		}
		
		try {
			Method getMethod = entity.getClass().getMethod("id");
			return getMethod.invoke(entity);
		} catch (final Exception e) {
		}
		
		return null;
		
	}

	/**
	 * Finds fields for asserting if there is field and getter for it. Getters
	 * are regular java getters and scala like getters(have same name as fields
	 * that they get).
	 * 
	 * @param the generic type
	 * @param object instance of class X
	 * @param complexTypes the complex types
	 * @param entityTypes the entity types
	 * @return {@link List} of real "get" methods of class X
	 */
	public static Map<String, Object> getFieldsForAssertFromMethods(final Object object, final Map<AssertableType, List<Class<?>>> types) {
		final Map<String, Object> fieldsForAssert = new HashMap<String, Object>();
		final Method[] allMethods = object.getClass().getMethods();
		for (final Method method : allMethods) {
			if (isGetMethod(object.getClass(), method)) {
				try {
					final Object value = method.invoke(object);
					fieldsForAssert.put(getFieldName(method), value);
				} catch (final Exception e) {}
			}
		}
		return fieldsForAssert;
	}

	/**
	 * Gets the object get method named.
	 * 
	 * @param the generic type
	 * @param methodName the method name
	 * @param object the object
	 * @return the object get method named
	 */
	public static Method getGetMethod(final String methodName, final Object object) throws Exception {
		return object.getClass().getMethod(methodName);
	}

	/**
	 * Gets the object type.
	 * 
	 * @param expected the expected
	 * @param actual the actual
	 * @param types the types
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
		} else if (Map.class.isAssignableFrom(typeClass)) {
			return AssertableType.MAP_TYPE;
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
	 * Creates a copy of specified object by creating instance with reflection
	 * and fills it using get and set method of a class.
	 * 
	 * @param object object for copying
	 * @param nodes list of objects that had been copied
	 * @param types the types
	 * @return copied entity
	 * @throws CopyException the copy exception
	 */
	public static Object createCopyObject(final Object object, final NodesList nodes,
			final Map<AssertableType, List<Class<?>>> types) throws CopyException {
		Object copy = nodes.getExpected(object);
		if (copy != null) {
			return copy;
		}

		copy = createEmptyCopyOf(object);
		if (copy == null) {
			throw new CopyException(object.getClass().getSimpleName());
		}
		nodes.addPair(copy, object);

		final Class<?> clazz = object.getClass();
		final Map<String, Object> fieldsForCopying = ReflectionUtil.getAssertFields(object, types);
		for (final Entry<String, Object> field : fieldsForCopying.entrySet()) {
			final Object copiedProperty = copyProperty(field.getValue(), nodes, types);
			if (!copyValueTo(clazz, field.getValue().getClass(), field.getKey(), copy, copiedProperty)) {
				throw new CopyException(clazz.getSimpleName());
			}
		}
		return copy;
	}

	/**
	 * Creates empty copy of object using reflection to call default
	 * constructor.
	 * 
	 * @param object object for copying
	 * @return copied empty instance of specified object or <code>null</code> if
	 *         default constructor can not be called
	 */
	public static Object createEmptyCopyOf(final Object object) {
		try {
			return object.getClass().newInstance();
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets property for copying using reflection.
	 * 
	 * @param object property's parent
	 * @param method get method for property
	 * @return property
	 */
	public static Object getPropertyForCopying(final Object object, final Method method) {
		try {
			return method.invoke(object);
		} catch (final Exception e) {
			throw new AssertionFailedError(e.getMessage());
		}
	}

	/**
	 * Copies property.
	 * 
	 * @param propertyForCopying property for copying
	 * @param nodes list of objects that had been copied
	 * @return copied property
	 */
	public static Object copyProperty(final Object propertyForCopying, final NodesList nodes,
			final Map<AssertableType, List<Class<?>>> types) throws CopyException {
		if (propertyForCopying == null) {
			// its null we shouldn't do anything
			return null;
		}

		if (isComplexType(propertyForCopying.getClass(), types)) {
			// its complex object, we need its copy
			return createCopyObject(propertyForCopying, nodes, types);
		}

		if (isListType(propertyForCopying)) {
			// just creating new list with same elements
			return copyList((List<?>)propertyForCopying, types);
		}

		if (isMapType(propertyForCopying)) {
			return copyMap((Map)propertyForCopying, types);
		}

		// if its not list or some complex type same object will be added.
		return propertyForCopying;

	}

	/**
	 * Creates a copy of specified map.
	 * 
	 * @param propertyForCopying
	 * @param types
	 * @return
	 * @throws CopyException
	 */
	public static Object copyMap(final Map propertyForCopying, final Map<AssertableType, List<Class<?>>> types)
			throws CopyException {
		final Map mapCopy = new HashMap();
		for (final Object key : propertyForCopying.keySet()) {
			mapCopy.put(key, copyProperty(propertyForCopying.get(key), new NodesList(), types));
		}
		return mapCopy;
	}

	/**
	 * Creates a copy of specified list.
	 * 
	 * @param <T> type objects in the list
	 * @param list list for copying
	 * @return copied list
	 */
	public static <T> List<T> copyList(final List<T> list, final Map<AssertableType, List<Class<?>>> types)
			throws CopyException {
		final List<T> copyList = new ArrayList<T>();
		for (final T t : list) {
			copyList.add((T)copyProperty(t, new NodesList(), types));
		}
		return copyList;
	}

	/**
	 * Create copy of specified object and return its copy.
	 * 
	 * @param object object for copying
	 * @param types the types
	 * @return copied object
	 * @throws CopyException the copy exception
	 */
	public static Object createCopy(final Object object, final Map<AssertableType, List<Class<?>>> types)
			throws CopyException {
		if (object == null) {
			return null;
		}

		if (isListType(object)) {
			final List<?> list = (List<?>)object;
			return copyList(list, types);
		}

		return createCopyObject(object, new NodesList(), types);

	}

	/**
	 * Sets copied property to copied object.
	 * 
	 * @param <T> object type
	 * @param method get method for property
	 * @param classObject parent class for property
	 * @param propertyName property name
	 * @param object copied parent object
	 * @param copiedProperty copied property
	 * @return <code>true</code> if property is successfully set,
	 *         <code>false</code> otherwise.
	 */
	public static <T> boolean copyValueTo(final Class<?> classObject, final Class<?> setterParameterType,
			final String propertyName, final T object, final Object copiedProperty) {

		try {
			final Field field = findField(object.getClass(), propertyName);
			if (field != null && Modifier.isPublic(field.getModifiers())) {
				field.set(object, copiedProperty);
				return true;
			}

			final Method setter = findMethodWithParameter(object.getClass(), SET_METHOD_PREFIX + StringUtils.capitalize(propertyName), setterParameterType);
			if (setter != null) {
				setter.invoke(object, copiedProperty);
				return true;
			}

			final Method equalNameSetter = findMethodWithParameter(object.getClass(), propertyName, setterParameterType);
			if (equalNameSetter != null) {
				equalNameSetter.invoke(object, copiedProperty);
				return true;
			}

			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	public static Method findMethodWithParameter(final Class<?> clazz, final String methodName, final Class<?> parameterType) {
		final Method setter = findMethod(clazz, methodName, parameterType);
		if (setter != null) {
			return setter;
		}

		final Method primitiveSetter = findMethodWithPrimitiveParameter(clazz, methodName, parameterType);
		if (primitiveSetter != null) {
			return primitiveSetter;
		}
		return null;
	}

	public static Method findMethodWithPrimitiveParameter(final Class<?> clazz, final String methodName, final Class<?> parameterType) {

		Class<?> primitiveClass = null;
		if (parameterType.equals(Integer.class)) {
			primitiveClass = Integer.TYPE;
		} else if (parameterType.equals(Boolean.class)) {
			primitiveClass = Boolean.TYPE;
		} else if (parameterType.equals(Double.class)) {
			primitiveClass = Double.TYPE;
		} else if (parameterType.equals(Long.class)) {
			primitiveClass = Long.TYPE;
		} else if (parameterType.equals(Float.class)) {
			primitiveClass = Float.TYPE;
		} else if (parameterType.equals(Character.class)) {
			primitiveClass = Character.TYPE;
		} else if (parameterType.equals(Short.class)) {
			primitiveClass = Short.TYPE;
		} else if (parameterType.equals(Byte.class)) {
			primitiveClass = Byte.TYPE;
		}

		return findMethod(clazz, methodName, primitiveClass);
	}

	/**
	 * Gets all the fields that need to be asserted within specified object. Its
	 * a combination of all public, non static members and fields that can be
	 * obtained with getters.
	 * 
	 * @param object
	 * @param types
	 * @return
	 */
	public static Map<String, Object> getAssertFields(final Object object, final Map<AssertableType, List<Class<?>>> types) {
		final Map<String, Object> fieldsForAsserting = getFieldsForAssertFromMethods(object, types);
		return getPublicFieldValues(object.getClass(), object, fieldsForAsserting, types);
	}

	/**
	 * Returns all public, nonstatic, field values from specified object.
	 * 
	 * @param clazz
	 * @param object
	 * @param fieldsForAsserting
	 * @param types
	 * @return
	 */
	public static Map<String, Object> getPublicFieldValues(final Class<?> clazz, final Object object, final Map<String, Object> fieldsForAsserting, final Map<AssertableType, List<Class<?>>> types) {
		if (clazz.getSuperclass() == null) {
			return fieldsForAsserting;
		}
		for (final Field field : object.getClass().getFields()) {
			final int fieldModifier = field.getModifiers();
			if (!Modifier.isStatic(fieldModifier)) {
				try {
					fieldsForAsserting.put(field.getName(), field.get(object));
				} catch (final Exception e) {}
			}
		}
		return getPublicFieldValues(clazz.getSuperclass(), object, fieldsForAsserting, types);
	}

	/**
	 * Gets value of the field with specified name.
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public static Object getValueForFieldWithName(final Object object, final String fieldName) throws Exception {
		try {

			final Field field = findField(object.getClass(), fieldName);
			if (field != null && Modifier.isPublic(field.getModifiers())) {
				return field.get(object);
			}

			final Method getter = hasMethod(object.getClass(), GET_METHOD_PREFIX + StringUtils.capitalize(fieldName));
			if (getter != null) {
				return getter.invoke(object);
			}

			final Method booleanGetter = hasMethod(object.getClass(), IS_METHOD_PREFIX + StringUtils.capitalize(fieldName));
			if (booleanGetter != null) {
				return booleanGetter.invoke(object);
			}

			final Method equalNameGetter = hasMethod(object.getClass(), fieldName);
			if (equalNameGetter != null) {
				return equalNameGetter.invoke(object);
			}
		} catch (final Exception e) {}
		throw new Exception();
	}

	/**
	 * Determines if specified class has method with name.
	 * 
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static Method hasMethod(final Class<?> clazz, final String methodName) {
		try {
			return clazz.getMethod(methodName);
		} catch (final Exception e) {
			return null;
		}
	}

	public static Method findMethod(final Class<?> clazz, final String methodName, final Class<?> parameterType) {
		try {
			return clazz.getMethod(methodName, parameterType);
		} catch (final Exception e) {
			return null;
		}
	}
}
