package eu.execom.fabut.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/*
	 * Is specified method for specified class a get method.
	 */
	public static boolean isGetMethod(Class<?> clazz, Method method) {
		String fieldName = getFieldNameFromGetter(method);
		Field field = findField(clazz, fieldName);
		return field != null && method.getReturnType().equals(field.getType());
	}

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

	public static AssertableType getObjectType(Object expected, Object actual,
			Map<AssertableType, List<Class<?>>> types) {

		if (expected == null && actual == null) {
			return AssertableType.PRIMITIVE_TYPE;
		}

		final Class<?> typeClass = actual != null ? actual.getClass()
				: expected.getClass();

		if (scala.collection.immutable.List.class.isAssignableFrom(typeClass)) {
			return AssertableType.IGNORED_TYPE;
		} else if (scala.collection.immutable.Map.class
				.isAssignableFrom(typeClass)) {
			return AssertableType.IGNORED_TYPE;

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

	public static Object getNullValueForType(Class<?> type) {
		if (long.class.equals(type)) {
			return 0l;
		} else if (int.class.equals(type)) {
			return 0;
		} else if (float.class.equals(type)) {
			return 0f;
		} else if (double.class.equals(type)) {
			return 0.0;
		} else if (boolean.class.equals(type)) {
			return false;
		} else {
			return null;
		}
	}

	public static Object getIdValue(Object entity) {
		try {
			Method method = entity.getClass().getMethod(ID);
			return method.invoke(entity);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getRubbishScalaPrefix(Class<?> clazz) {
		return clazz.getName().replaceAll("\\.", "\\$") + "$$";
	}
}
