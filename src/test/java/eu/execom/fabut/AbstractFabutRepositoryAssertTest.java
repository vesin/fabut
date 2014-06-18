package eu.execom.fabut;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import fabut.model.Contract;
import fabut.model.Employee;
import fabut.model.EmployeeDTO;
import fabut.model.WithListProperty;
import fabut.model.WithMapProperty;

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class AbstractFabutRepositoryAssertTest extends Assert
		implements IFabutRepositoryTest {
	// mock lists
	private List<Object> entityTierOneTypes = new ArrayList<Object>();
	private List<Object> entityTierTwoTypes = new ArrayList<Object>();
	private List<Object> noDefaultConstructorEntities = new ArrayList<Object>();
	private List<Object> employees = new ArrayList<Object>();
	private FabutRepositoryAssert fabutRepositoryAssert;

	public AbstractFabutRepositoryAssertTest() {

	}

	@Override
	public List<Class<?>> getEntityTypes() {
		final List<Class<?>> entityTypes = new LinkedList<Class<?>>();
		entityTypes.add(Employee.class);
		return entityTypes;
	}

	@Override
	public List<Object> findAll(final Class<?> entityClass) {
		if (entityClass == Employee.class) {
			return employees;
		}
		return null;
	}

	@Override
	public Object findById(final Class<?> entityClass, final Object id) {
		if (entityClass == Employee.class) {
			for (final Object entity : employees) {
				if (id.equals(((Employee) entity).id())) {
					return entity;
				}
			}
		}
		return null;
	}

	public List<Object> getEntityTierOneTypes() {
		return entityTierOneTypes;
	}

	public void setEntityTierOneTypes(final List<Object> list1) {
		entityTierOneTypes = list1;
	}

	public List<Object> getEntityTierTwoTypes() {
		return entityTierTwoTypes;
	}

	public void setEntityTierTwoTypes(final List<Object> list2) {
		entityTierTwoTypes = list2;
	}

	public List<Object> getNoDefaultConstructorEntities() {
		return noDefaultConstructorEntities;
	}

	public void setNoDefaultConstructorEntities(
			final List<Object> noDefaultConstructorEntities) {
		this.noDefaultConstructorEntities = noDefaultConstructorEntities;
	}

	@Override
	@Before
	public void fabutBeforeTest() {
		fabutRepositoryAssert = new FabutRepositoryAssert(this);
	}

	@Override
	@After
	public void fabutAfterTest() {
	}

	@Override
	public List<Class<?>> getComplexTypes() {
		final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
		complexTypes.add(Contract.class);
		complexTypes.add(Technology.class);
		complexTypes.add(EmployeeDTO.class);
		complexTypes.add(WithListProperty.class);
		complexTypes.add(WithMapProperty.class);
		return complexTypes;
	}

	@Override
	public List<Class<?>> getIgnoredTypes() {
		final List<Class<?>> ignoredTypes = new LinkedList<Class<?>>();
		return ignoredTypes;
	}

	@Override
	public void customAssertEquals(final Object expected, final Object actual) {
		Assert.assertEquals(expected, actual);

	}

	public FabutRepositoryAssert getFabutRepositoryAssert() {
		return fabutRepositoryAssert;
	}

	public List<Object> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Object> employees) {
		this.employees = employees;
	}

}
