package eu.execom.fabut.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.exception.CopyException;

/**
 * Util class for reflection logic needed by testutil.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public final class ReflectionUtil {

	public static final String SETTER_SUFIX = "_$eq";
	public static final String ID = "id";

	private ReflectionUtil() {
		super();
	}

	/**
	 * Gets field name from setter.
	 * 
	 * @param fromMethod
	 * @return
	 */
	public static String getFieldNameFromSetter(Method fromMethod) {
		return "_"
				+ fromMethod.getName().substring(0,
						fromMethod.getName().indexOf(SETTER_SUFIX));
	}

	/**
	 * Gets field name from getter, ensures that field actually exists. Does
	 * three checks as there are 3 ways of naming fields atm.
	 * 
	 * @param fromMethod
	 * @return
	 */
	public static String getFieldNameFromGetter(Method fromMethod) {
		Class<?> ownerClass = fromMethod.getDeclaringClass();
		try {
			Field field = ownerClass.getDeclaredField(fromMethod.getName());
			return field.getName();
		} catch (Exception e) {
		}

		try {
			Field field = ownerClass.getDeclaredField("_"
					+ fromMethod.getName());
			return field.getName();
		} catch (Exception e) {
		}

		try {
			Field field = ownerClass
					.getDeclaredField(getRubbishScalaPrefix(ownerClass) + "_"
							+ fromMethod.getName());
			return field.getName();
		} catch (Exception e) {
		}

		return null;
	}

	/**
	 * Searches trough property class inheritance tree for field with specified
	 * name. Starting from property class method recursively climbs higher in
	 * the inheritance tree until it finds field with specified name or reached
	 * object in which case returns null.
	 * 
	 * @param fieldClass
	 *            class of the field.
	 * @param fieldName
	 *            name of the field
	 * @return {@link Field} with specified name, otherwise <code>null</code>>
	 */
	public static Field findField(Class<?> fieldClass, String fieldName) {
		if (fieldClass == null) {
			return null;
		}
		try {
			return fieldClass.getDeclaredField(fieldName);
		} catch (final Exception e) {
		}
		// try to find field with scala rubbish prefix
		try {
			return fieldClass.getDeclaredField(ReflectionUtil
					.getRubbishScalaPrefix(fieldClass) + fieldName);
		} catch (final Exception e) {
		}
		return findField(fieldClass.getSuperclass(), fieldName);

	}

	/**
	 * Is specified method for specified class a get method.
	 */
	public static boolean isGetMethod(Class<?> clazz, Method method) {
		String fieldName = getFieldNameFromGetter(method);
		Field field = findField(clazz, fieldName);
		return field != null && method.getReturnType().equals(field.getType());

	}

	/**
	 * Gets all fields that need to be asserted within given object.
	 * 
	 * @param object
	 * @return
	 */
	public static Map<String, Object> getFieldsForAssertFromMethods(
			final Object object) {
		final Map<String, Object> fieldsForAssert = new HashMap<String, Object>();
		final Method[] allMethods = object.getClass().getMethods();
		for (final Method method : allMethods) {
			if (isGetMethod(object.getClass(), method)) {
				try {
					final Object value = method.invoke(object);
					fieldsForAssert.put(getFieldNameFromGetter(method), value);
				} catch (final Exception e) {
				}
			}
		}
		return fieldsForAssert;
	}

	/**
	 * Gets field value from given object for given field name.
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public static Object getFieldValue(Object object, String fieldName)
			throws Exception {
		try {
			int indexOfUnderScore = fieldName.lastIndexOf("_");
			Method getter = object.getClass().getMethod(
					fieldName.substring(indexOfUnderScore + 1));
			return getter.invoke(object);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Gets assertable type from expected/actual pair.
	 * 
	 * @param expected
	 * @param actual
	 * @param types
	 * @return
	 */
	public static AssertableType getAssertableTypeFrom(Object expected,
			Object actual, Map<AssertableType, List<Class<?>>> types) {

		if (expected == null && actual == null) {
			return AssertableType.PRIMITIVE_TYPE;
		}

		final Class<?> typeClass = actual != null ? actual.getClass()
				: expected.getClass();

		if (scala.collection.immutable.List.class.isAssignableFrom(typeClass)) {
			return AssertableType.SCALA_LIST_TYPE;
		} else if (scala.collection.immutable.Map.class
				.isAssignableFrom(typeClass)) {
			return AssertableType.SCALA_MAP_TYPE;

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
	 * Creates copy of an given object
	 * 
	 * @param object
	 * @return
	 * @throws CopyException
	 */
	public static Object createCopy(Object object) throws CopyException {
		for (Constructor<?> constructor : object.getClass().getConstructors()) {
			if (constructor.getParameterTypes().length == 1
					&& constructor.getParameterTypes()[0] == object.getClass()) {
				try {
					return constructor.newInstance(object);
				} catch (Exception e) {
					throw new CopyException(object.getClass().getName());
				}
			}
		}
		throw new CopyException(object.getClass().getName());
	}

	/**
	 * Gets id value from given entity.
	 * 
	 * @param entity
	 * @return
	 */
	public static Object getIdValue(Object entity) {
		try {
			Method method = entity.getClass().getMethod(ID);
			return method.invoke(entity);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * In some cases scala compiler appends "rubbish" prefix to field name. This
	 * method return "rubbish" prefix from for given class.
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getRubbishScalaPrefix(Class<?> clazz) {
		return clazz.getName().replaceAll("\\.", "\\$") + "$$";
	}

	/**
	 * Checks if given object is instance of scala.Some or scala.None.
	 * 
	 * @param actual
	 */
	public static void checkIfOption(Object actual) {
		if (actual != null
				&& (actual.getClass().equals(scala.Some.class) || actual
						.getClass().equals(scala.None.class))) {
			throw new AssertionFailedError(
					"Object is option, please pass option value to assert!");
		}
	}

	/**
	 * Fabut cannot assert scala lists or maps.
	 * 
	 * @param actual
	 */
	public static void checkIfListOrMap(Object actual) {
		if (actual != null
				&& scala.collection.immutable.List.class
						.isAssignableFrom(actual.getClass())) {
			throw new AssertionFailedError("Lists cannot be asserted!");
		}
		if (actual != null
				&& scala.collection.immutable.Map.class.isAssignableFrom(actual
						.getClass())) {
			throw new AssertionFailedError("Lists cannot be asserted!");
		}
	}
}
