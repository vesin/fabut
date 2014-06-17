package eu.execom.fabut;

import junit.framework.AssertionFailedError;

import org.junit.Test;

<<<<<<< HEAD
import fabut.model.Employee;
import fabut.model.EmployeeDTO;
=======
import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.model.NoDefaultConstructorEntity;
import eu.execom.fabut.model.ScalaType;
import eu.execom.fabut.model.TierOneType;
import eu.execom.fabut.model.TierTwoTypeWithListProperty;
import eu.execom.fabut.model.TierTwoTypeWithMap;
import eu.execom.fabut.model.TypeWithAllWaysOfGettingFields;
import eu.execom.fabut.model.test.Address;
import eu.execom.fabut.model.test.Faculty;
import eu.execom.fabut.model.test.Student;
import eu.execom.fabut.model.test.Teacher;
import eu.execom.fabut.property.IgnoredProperty;
import eu.execom.fabut.property.MultiProperties;
import eu.execom.fabut.property.NotNullProperty;
import eu.execom.fabut.property.NullProperty;
import eu.execom.fabut.property.Property;
>>>>>>> master

public class FabutTest extends AbstractFabutRepositoryAssertTest {

	private static final String TEST = "test";

	@Test
	public void testTakeSnapshotScalaEmployee() {
		// setup
		Fabut.beforeTest(this);
		Employee employee = new Employee(1l, "Test");
		getEmployees().add(employee);
		Fabut.takeSnapshot();

		// method
		Fabut.afterTest();
	}

	@Test(expected = AssertionFailedError.class)
	public void testTakeSnapshotScalaEmployeeFail() {
		// setup
		Fabut.beforeTest(this);
		Fabut.takeSnapshot();
		getEmployees().add(new Employee(2l, TEST + TEST));

		// method
		Fabut.afterTest();
	}

	@Test
	public void testAssertObject() {
		// setup
		Fabut.beforeTest(this);
		Employee employee = new Employee(1l, TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObject(employee,
				Fabut.value("fabut$model$Employee$$_id", 1l),
				Fabut.value("fabut$model$Employee$$_name", TEST));
		Fabut.afterTest();
	}

	@Test
	public void testAssertObjectGeneratedFieldNames() {
		// setup
		Fabut.beforeTest(this);
		Employee employee = new Employee(1l, TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObject(employee, Fabut.value("_id", 1l),
				Fabut.value("_name", TEST));
		Fabut.afterTest();
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectFail() {
		// setup
		Fabut.beforeTest(this);
		Employee employee = new Employee(1l, TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObject(employee, Fabut.value("_id", 2l),
				Fabut.value("_name", TEST));
		Fabut.afterTest();
	}

	@Test
	public void testAssertObjectsDTOs() {
		// setup
		Fabut.beforeTest(this);
		EmployeeDTO expected = new EmployeeDTO(1l, TEST);
		EmployeeDTO actual = new EmployeeDTO(1l, TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObjects(expected, actual);
		Fabut.afterTest();
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectsDTOsFail() {
		// setup
		Fabut.beforeTest(this);
		EmployeeDTO expected = new EmployeeDTO(1l, TEST + TEST);
		EmployeeDTO actual = new EmployeeDTO(1l, TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObjects(expected, actual);
		Fabut.afterTest();
	}

	@Test
	public void testAssertObjects() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		Employee actual = new Employee(1l, TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObjects(expected, actual);
		Fabut.afterTest();
	}

	@Test
	public void testAssertObjectsWithChangedProperties() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		Employee actual = new Employee(1l, TEST + TEST);

		// method
		Fabut.takeSnapshot();
		Fabut.assertObjects(expected, actual,
				Fabut.value("fabut$model$Employee$$_name", TEST + TEST));
		Fabut.afterTest();
	}
	/**
	 * Test for
	 * {@link Fabut#assertObjects(Object, Object, eu.execom.fabut.property.IProperty...)}
	 * when specified objects are entities and can be asserted.
	 */
	@Test
	public void testAssertObjectsEntitySuccessScalaType() {
		// setup
		Fabut.beforeTest(this);

		ScalaType expected = new ScalaType();
		expected.id(1l);
		
		ScalaType actual = new ScalaType();
		actual.id(1l);

		// method
		Fabut.takeSnapshot(expected);
		Fabut.assertObjects(expected, actual);

		Fabut.afterTest();
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectsFail() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		Employee actual = new Employee(2l, "");

		// method
		Fabut.takeSnapshot();
		Fabut.assertObjects(expected, actual);
		Fabut.afterTest();
	}

	@Test
	public void testAssertEntityWithSnapshot() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		getEmployees().add(expected);

		// method
		Fabut.takeSnapshot();
		Employee changed = (Employee) getEmployees().get(0);
		changed.name_$eq(TEST + TEST);
		Fabut.assertEntityWithSnapshot(changed,
				Fabut.value("fabut$model$Employee$$_name", TEST + TEST));
		Fabut.afterTest();
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertEntityWithSnapshotFail() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		getEmployees().add(expected);

		// method
		Fabut.takeSnapshot();
		Employee changed = (Employee) getEmployees().get(0);
		changed.name_$eq(TEST + TEST);
		Fabut.assertEntityWithSnapshot(changed, Fabut.value("_name", ""));
		Fabut.afterTest();
	}

	@Test
	public void testMarkAsserted() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		getEmployees().add(expected);

		// method
		Fabut.takeSnapshot();
		Employee changed = (Employee) getEmployees().get(0);
		changed.name_$eq(TEST + TEST);
		Fabut.markAsserted(changed);
		Fabut.afterTest();

	}

	@Test
	public void testAssertEntityAsDeleted() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		getEmployees().add(expected);

		// method
		Fabut.takeSnapshot();
		Employee changed = (Employee) getEmployees().get(0);
		getEmployees().remove(0);
		Fabut.assertEntityAsDeleted(changed);
		Fabut.afterTest();

	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertEntityAsDeletedFail() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		getEmployees().add(expected);

		// method
		Fabut.takeSnapshot();
		Employee changed = (Employee) getEmployees().get(0);
		Fabut.assertEntityAsDeleted(changed);
		Fabut.afterTest();

	}

	@Test
	public void testIgnoreEntity() {
		// setup
		Fabut.beforeTest(this);
		Employee expected = new Employee(1l, TEST);
		getEmployees().add(expected);

		// method
		Fabut.takeSnapshot();
		Employee changed = (Employee) getEmployees().get(0);
		changed.name_$eq(TEST + TEST);
		Fabut.ignoreEntity(changed);
		Fabut.afterTest();

	}
}
