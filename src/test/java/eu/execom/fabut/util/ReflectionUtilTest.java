package eu.execom.fabut.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.execom.fabut.enums.AssertableType;
import fabut.model.Employee;
import fabut.model.EmployeeDTO;

/**
 * Tests for {@link ReflectionUtil}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class ReflectionUtilTest extends Assert {

	private static final String TEST = "test";

	Map<AssertableType, List<Class<?>>> types;

	/**
	 * Test for isGetMethod of {@link ReflectionUtil} when method does not
	 * starts with "get" prefix and there is matching field in the class.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@Before
	public void setup() {
		types = new EnumMap<AssertableType, List<Class<?>>>(
				AssertableType.class);
		final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
		types.put(AssertableType.COMPLEX_TYPE, complexTypes);

		final List<Class<?>> entityTypes = new LinkedList<Class<?>>();
		types.put(AssertableType.ENTITY_TYPE, entityTypes);
	}

	public static final String SETTER_SUFIX = "_$eq";
	public static final String ID = "id";

	@Test
	public void testGetFieldNameFromSetter() throws NoSuchMethodException {
		// setup
		Employee employee = new Employee(1l, "Test");
		Method method = employee.getClass().getMethod("id" + SETTER_SUFIX,
				long.class);

		// method
		String fieldName = ReflectionUtil.getFieldNameFromSetter(method);

		// assert
		assertEquals("_id", fieldName);

	}

	@Test
	public void testGetFieldNameFromGetterEntity() throws NoSuchMethodException {
		// setup
		Employee employee = new Employee(1l, "Test");
		Method method = employee.getClass().getMethod("id");

		// method
		String fieldName = ReflectionUtil.getFieldNameFromGetter(method);

		// assert
		assertEquals("fabut$model$Employee$$_id", fieldName);
	}

	@Test
	public void testGetFieldNameFromGetterDTO() throws NoSuchMethodException {
		// setup
		EmployeeDTO employeeDTO = new EmployeeDTO(1l, "Test");
		Method method = employeeDTO.getClass().getMethod("id");

		// method
		String fieldName = ReflectionUtil.getFieldNameFromGetter(method);

		// assert
		assertEquals("id", fieldName);
	}

	@Test
	public void testFindField() throws NoSuchFieldException {
		// setup
		Employee employee = new Employee(1l, "Test");
		Field expected = employee.getClass().getDeclaredField(
				"fabut$model$Employee$$_id");

		// method
		Field actual = ReflectionUtil.findField(Employee.class, "_id");

		// assert
		assertEquals(expected, actual);
	}

	@Test
	public void testIsGetMethod() throws NoSuchMethodException {
		// setup
		Employee employee = new Employee(1l, "Test");
		Method method = employee.getClass().getMethod("id");

		// method
		boolean isGetMethod = ReflectionUtil
				.isGetMethod(Employee.class, method);

		// assert
		assertTrue(isGetMethod);
	}

	@Test
	public void testGetFieldsForAssertFromMethods() {
		// setup
		Employee employee = new Employee(1l, "Test");

		// method
		Map<String, Object> actual = ReflectionUtil
				.getFieldsForAssertFromMethods(employee);

		// assert
		assertEquals(2, actual.size());
		assertEquals(1l, actual.get("fabut$model$Employee$$_id"));
		assertEquals("Test", actual.get("fabut$model$Employee$$_name"));
	}

	@Test
	public void testGetFieldValue() throws Exception {
		// setup
		Employee employee = new Employee(1l, "Test");

		// method
		Object value = ReflectionUtil.getFieldValue(employee, "_name");

		// assert
		assertEquals("Test", value);
	}

	@Test
	public void testCreateCopyObject() throws Exception {
		// method
		Employee employeeCopy = (Employee) ReflectionUtil
				.createCopy(new Employee(1l, "Test"));

		// assert
		assertNotNull(employeeCopy);
		assertEquals(1l, employeeCopy.id());
		assertEquals("Test", employeeCopy.name());
	}

	@Test
	public void testGetIdValue() {
		// method
		Object id = ReflectionUtil.getIdValue(new Employee(1l, "Test"));
		// assert
		assertEquals(1l, id);
	}

	/**
	 * Test for findFieldInInheritance of {@link ReflectionUtil} when null is
	 * specified as a class.
	 */
	@Test
	public void testFindFieldInInheritanceNullClass() {
		// method
		final Field field = ReflectionUtil.findField(null, TEST);

		// assert
		assertNull(field);
	}

	@Test
	public void testGetRubbishScalaPrefix() {
		// method
		String rubbish = ReflectionUtil.getRubbishScalaPrefix(Employee.class);

		// assert
		assertEquals("fabut$model$Employee$$", rubbish);
	}

}
