package eu.execom.fabut;

import junit.framework.AssertionFailedError;

import org.junit.Test;

import fabut.model.Employee;
import fabut.model.EmployeeDTO;
import fabut.model.WithListProperty;
import fabut.model.WithMapProperty;

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

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectsWithScalaListAsProperty() {
		// setup
		Fabut.beforeTest(this);
		WithListProperty expected = new WithListProperty();
		expected.populateList("test1");
		WithListProperty actual = new WithListProperty();
		actual.populateList("test2");

		// method
		Fabut.assertObjects(expected, actual);
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectWithScalaListAsProperty() {
		// setup
		Fabut.beforeTest(this);
		WithListProperty expected = new WithListProperty();
		expected.populateList("test1");

		// method
		Fabut.assertObject(expected, Fabut.value("list", "Test"));
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectsWithScalaMapAsProperty() {
		// setup
		Fabut.beforeTest(this);
		WithMapProperty expected = new WithMapProperty();
		expected.populateMap("test1");
		WithMapProperty actual = new WithMapProperty();
		actual.populateMap("test2");

		// method
		Fabut.assertObjects(expected, actual);
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertObjectWithScalaMapAsProperty() {
		// setup
		Fabut.beforeTest(this);
		WithMapProperty expected = new WithMapProperty();
		expected.populateMap("test1");

		// method
		Fabut.assertObject(expected, Fabut.value("map", "Test"));
	}

	@Test(expected = AssertionFailedError.class)
	public void testAssertList() {
		// setup
		Fabut.beforeTest(this);
		WithListProperty expected = new WithListProperty();
		expected.populateList("test1");

		// method
		Fabut.assertObject(expected.list(), Fabut.value("list", "Test"));
	}
}
