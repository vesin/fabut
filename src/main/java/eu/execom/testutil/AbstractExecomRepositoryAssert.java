package eu.execom.testutil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;

import eu.execom.testutil.graph.NodesList;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.report.AssertReportBuilder;
import eu.execom.testutil.util.ReflectionUtil;

/**
 * ExeCom test util class. Extends {@link AbstractExecomAssert} with possibility to assert entire repository (DB ...) .
 * TODO think of better comment.
 * 
 * @param <EntityType>
 *            Entity type that all tested entities must implement.
 * @param <EntityId>
 *            type of entity id
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings({"unchecked"})
// TODO its not Abstract any more.
public class AbstractExecomRepositoryAssert<EntityType, EntityId> extends AbstractExecomAssert<EntityType> implements
        ExecomRepositoryAssert<EntityType, EntityId> {

    /** The Constant SET_METHOD_PREFIX. */
    protected static final String SET_METHOD_PREFIX = "set";

    /** The db snapshot. */
    private final Map<Class<?>, Map<EntityId, CopyAssert<Object>>> dbSnapshot;

    /** Parameters passed into method. */
    private final List<Object> parameters;

    /** Snapshot of parameters passed into method. */
    private final List<Object> parametersSnapshot;

    /**
     * Default constructor.
     */
    public AbstractExecomRepositoryAssert() {
        super();
        dbSnapshot = new HashMap<Class<?>, Map<EntityId, CopyAssert<Object>>>();
        parameters = new ArrayList<Object>();
        parametersSnapshot = new ArrayList<Object>();
    }

    @Override
    public void assertEntityAsDeleted(final EntityType actual) {

        ignoreEntity(actual);

        final Object findById = findById(actual.getClass(), ReflectionUtil.getIdValue(actual));
        Assert.assertNull(findById);
    }

    @Override
    public void ignoreEntity(final EntityType actual) {

        if (ReflectionUtil.isEntityType(actual.getClass(), entityTypes)) {
            markAsserted(actual);
        }

    }

    @Override
    public <X extends EntityType> void assertEntityWithSnapshot(final X actual, final ISingleProperty... properties) {
        assertEntityWithSnapshot(EMPTY_STRING, actual, properties);
    }

    @Override
    public <X extends EntityType> void assertEntityWithSnapshot(final String message, final X actual,
            final ISingleProperty... properties) {

        final EntityId id = ReflectionUtil.getIdValue(actual);
        Assert.assertNotNull("Entity id can't be null " + actual, id);

        final Map<EntityId, CopyAssert<Object>> map = dbSnapshot.get(actual.getClass());
        final boolean isTypeSupported = map != null;

        if (isTypeSupported) {
            final CopyAssert<Object> copyAssert = map.get(id);
            if (copyAssert != null) {
                final Object expected = copyAssert.getEntity();
                if (expected != null) {
                    assertObjects(message, expected, actual, properties);
                } else {
                    Assert.fail("There is no valid copy in snapshot for entity of class "
                            + actual.getClass().getSimpleName());
                }
            } else {
                Assert.fail("Entity doesn't exist in snapshot  " + actual);
            }
        } else {
            Assert.fail("Type of entity is not supported  " + actual.getClass());
        }

    }

    @Override
    protected final <X> void afterAssertEntity(final X object, final boolean isProperty) {
        if (ReflectionUtil.isEntityType(object.getClass(), entityTypes) && !isProperty
                && ReflectionUtil.getIdValue((EntityType) object) != null) {
            markAsserted((EntityType) object);
        }
    }

    /**
     * Find all entities of type entity class in DB.
     * 
     * @param entityClass
     *            the entity class
     * @return the list
     */
    protected List<?> findAll(final Class<?> entityClass) {
        return TestUtilAssert.findAll(entityClass);
    }

    /**
     * Find specific entity of type entity class and with specific id in DB.
     * 
     * @param entityClass
     *            the entity class
     * @param id
     *            the id
     * @return the entity type
     */
    protected Object findById(final Class<?> entityClass, final Object id) {
        return TestUtilAssert.findById(entityClass, id);
    }

    /**
     * Mark entity bean as asserted.
     * 
     * @param entity
     *            AbstractEntity
     */
    protected void markAsserted(final EntityType entity) {
        final boolean markAsserted = markAsAsserted(entity, entity.getClass());
        Assert.assertTrue("Type " + entity.getClass() + " is not currently supported.", markAsserted);
    }

    /**
     * Mark entity bean as asserted in db snapshot map. Go trough all its supper classes and if its possible assert it.
     * 
     * @param entity
     *            the entity
     * @param actualType
     *            the actual type
     * @return <code>true</code> if entity is successfully asserted else return <code>false</code>.
     */
    protected boolean markAsAsserted(final EntityType entity, final Class<?> actualType) {

        final EntityId id = ReflectionUtil.getIdValue(entity);
        Assert.assertNotNull("Entity id can't be null " + entity, id);
        final Map<EntityId, CopyAssert<Object>> map = dbSnapshot.get(actualType);
        final boolean isTypeSupported = map != null;

        if (isTypeSupported) {
            CopyAssert<Object> copyAssert = map.get(id);
            if (copyAssert == null) {
                copyAssert = new CopyAssert<Object>(createCopy(entity));
                map.put((EntityId) ReflectionUtil.getIdValue(entity), copyAssert);
            }
            copyAssert.setAsserted(true);
        }

        final Class<?> superClassType = actualType.getSuperclass();
        final boolean isSuperClassTypeSupported = (superClassType != null) && markAsAsserted(entity, superClassType);
        return isTypeSupported || isSuperClassTypeSupported;
    }

    /**
     * Takes current database snapshot and saves it.
     */
    public void takeSnapshot() {
        initDbSnapshot();
        for (final Entry<Class<?>, Map<EntityId, CopyAssert<Object>>> enties : dbSnapshot.entrySet()) {
            final List<?> findAll = findAll(enties.getKey());
            for (final Object abstractEntity : findAll) {
                enties.getValue().put((EntityId) ReflectionUtil.getIdValue(abstractEntity),
                        new CopyAssert<Object>(createCopy(abstractEntity)));
            }
        }
    }

    /**
     * Takes current parameters snapshot and original parameters, and saves them.
     * 
     * @param parameters
     *            array of parameters
     */
    protected void takeSnapshot(final Object... parameters) {
        initParametersSnapshot();

        for (final Object object : parameters) {
            this.parameters.add(object);
            parametersSnapshot.add(createCopy(object));
        }
    }

    /**
     * Asserts current database snapshot with one previously taken.
     */
    public void assertDbState() {
        boolean ok = true;
        final AssertReportBuilder report = new AssertReportBuilder();
        for (final Entry<Class<?>, Map<EntityId, CopyAssert<Object>>> entry : dbSnapshot.entrySet()) {
            final List<Object> afterEnitities = (List<Object>) findAll(entry.getKey());
            ok &= assertByEntity(entry.getValue(), afterEnitities, report);
        }
        if (!ok) {
            throw new AssertionError(report.getMessage());
        }
    }

    /**
     * Asserts all previously taken snapshots.
     */
    protected void assertSnapshots() {
        assertDbState();
        assertParameters();
    }

    /**
     * Asserts all entities of same type in current snapshot with ones from before snapshot.
     * 
     * @param <X>
     *            type of the entities
     * @param beforeEntities
     *            entities from before snapshot
     * @param afterEntities
     *            entities from current snapshot
     * @param report
     *            report builder
     * @return <code>true</code> if all entities from before snapshot are asserted with entities from current snapshot,
     *         <code>false</code> otherwise
     */
    <X> boolean assertByEntity(final Map<EntityId, CopyAssert<X>> beforeEntities, final List<X> afterEntities,
            final AssertReportBuilder report) {

        boolean ok = true;
        final List<EntityId> ids = new ArrayList<EntityId>();
        for (final Entry<EntityId, CopyAssert<X>> beforeEntry : beforeEntities.entrySet()) {
            ok &= validateEntry(ids, beforeEntry, afterEntities, report);
        }
        ok &= removeAfterEntitites(ids, afterEntities);
        report.reportEntityIsntAsserted(afterEntities);
        return ok;
    }

    /**
     * Asserts current parameters states with snapshot previously taken.
     */
    // TODO its not clear what its doing, basically it test not persisted objects that can be changed inside tested
    // method
    // ALSO it shoul'd be part of abstract execom assert, it doesn't have anything to do with repository
    void assertParameters() {
        boolean ok = true;
        final AssertReportBuilder report = new AssertReportBuilder();

        for (int i = 0; i < parameters.size(); i++) {
            final Object copy = parametersSnapshot.get(i);
            if (copy != null) {
                ok &= assertParameterPair(copy, parameters.get(i), report);
            } else {
                report.reportNoValidCopy(parameters.get(i));
                ok = false;
            }
        }

        initParametersSnapshot();

        if (!ok) {
            throw new AssertionError(report.getMessage());
        }
    }

    Map<Class<?>, Map<EntityId, CopyAssert<Object>>> getDbSnapshot() {
        return dbSnapshot;
    }

    List<Object> getParameters() {
        return parameters;
    }

    List<Object> getParametersSnapshot() {
        return parametersSnapshot;
    }

    /**
     * Validates entity by checking if entity is already asserted if not, then checks if entity from before snapshot has
     * its match in current snapshot.
     * 
     * @param <X>
     *            type of the entities
     * @param ids
     *            asserted entity ids
     * @param beforeEntry
     *            entity from before snapshot
     * @param afterEntities
     *            entities from current snapshot
     * @param report
     *            report builder
     * @return <code>true</code> if entity from before snapshot is already asserted or if it has match in current
     *         snapshot and is asserted with it, <code>false</code> otherwise
     */
    <X> boolean validateEntry(final List<EntityId> ids, final Entry<EntityId, CopyAssert<X>> beforeEntry,
            final List<X> afterEntities, final AssertReportBuilder report) {

        final X afterEntity = popEntityFromList(beforeEntry.getKey(), afterEntities);
        final CopyAssert<X> beforeAssertEntity = beforeEntry.getValue();

        if (beforeAssertEntity.isAsserted()) {
            return true;
        }

        if (beforeAssertEntity.getEntity() == null) {
            report.reportNoValidCopy(afterEntity);
            return false;
        }

        if (afterEntity == null) {
            report.reportNoEntityFailure(beforeAssertEntity.getEntity().toString());
            return false;
        }

        ids.add(beforeEntry.getKey());
        return assertEntities(beforeAssertEntity.getEntity(), afterEntity, report);
    }

    /**
     * Calls assertObjects of {@link AbstractExecomAssert} to assert two entities.
     * 
     * @param <X>
     *            the generic type
     * @param beforeEntity
     *            entity from before snapshot
     * @param afterEntity
     *            entity from current snapshot
     * @param report
     *            report builder
     * @return <code>true</code> if entities are asserted, i.e. assertObjects doesn't throw {@link AssertionError},
     *         <code>false</code> otherwise
     */
    <X> boolean assertEntities(final X beforeEntity, final X afterEntity, final AssertReportBuilder report) {
        try {
            assertObjects(beforeEntity, afterEntity);
            return true;
        } catch (final AssertionError e) {
            report.reportRepositoryEntityAssertFail(beforeEntity, afterEntity);
            report.append(e.getMessage());
            return false;
        }
    }

    /**
     * Pops entity from list of entities and returns it.
     * 
     * @param <X>
     *            entity type
     * @param id
     *            id of the desired entity
     * @param afterEntities
     *            list of entities
     * @return entity with specified id
     */
    <X> X popEntityFromList(final EntityId id, final List<X> afterEntities) {
        final Iterator<X> iterator = afterEntities.iterator();
        while (iterator.hasNext()) {
            final X entity = iterator.next();
            if (ReflectionUtil.getIdValue(entity).equals(id)) {
                iterator.remove();
                return entity;
            }
        }
        return null;
    }

    /**
     * Removes all entities from list of entities from current snapshot with matching id in list of ids.
     * 
     * @param <X>
     *            type of entity
     * @param ids
     *            list of ids
     * @param afterEntities
     *            entities from current snapshot
     * @return <code>true</code> if all entities from current snapshot have matching id in list of ids,
     *         <code>false</code> otherwise
     */
    <X> boolean removeAfterEntitites(final List<EntityId> ids, final List<X> afterEntities) {
        final Iterator<X> iterator = afterEntities.iterator();
        while (iterator.hasNext()) {
            if (ids.contains(ReflectionUtil.getIdValue(iterator.next()))) {
                iterator.remove();
            }
        }
        // FIXME WTF?
        return afterEntities.size() > 0 ? false : true;
    }

    /**
     * Calls assertObjects of {@link AbstractExecomAssert} to assert two parameters.
     * 
     * @param beforeParameter
     *            parameter from snapshot
     * @param afterParameter
     *            current parameter
     * @param report
     *            report builder
     * @return <code>true</code> if parameters are asserted, i.e. assertObjects doesn't throw {@link AssertionError},
     *         <code>false</code> otherwise
     */
    boolean assertParameterPair(final Object beforeParameter, final Object afterParameter,
            final AssertReportBuilder report) {
        try {
            assertObjects(beforeParameter, afterParameter);
            return true;
        } catch (final AssertionError e) {
            report.reportParametersAssertFail(beforeParameter, afterParameter);
            report.append(e.getMessage());
            return false;
        }
    }

    /**
     * Create copy of specified object and return its copy.
     * 
     * @param <T>
     *            type of the object
     * @param object
     *            object for copying
     * @return copied object
     */
    <T> T createCopy(final T object) {
        if (object == null) {
            return null;
        }

        if (ReflectionUtil.isListType(object)) {
            final List<?> list = (List<?>) object;
            return (T) copyList(list);
        }

        return createCopyObject(object, new NodesList());
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
    // FIXME add shallow in the name of the method or create list of copy objects
    <T> List<T> copyList(final List<T> list) {
        return new ArrayList<T>(list);
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
    // TODO move to reflection util
    <T> Object getPropertyForCopying(final T object, final Method method) {
        try {
            return method.invoke(object);
        } catch (final Exception e) {
            fail(e.getMessage());
        }
        return null;
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
    Object copyProperty(final Object propertyForCopying, final NodesList nodes) {
        if (propertyForCopying == null) {
            // its null we shouldn't do anything
            return null;
        }

        if (ReflectionUtil.isComplexType(propertyForCopying.getClass(), complexTypes)) {
            // its complex object, we need its copy
            return createCopyObject(propertyForCopying, nodes);
        }

        if (ReflectionUtil.isListType(propertyForCopying)) {
            // just creating new list with same elements
            return copyList((List<?>) propertyForCopying);
        }

        // if its not list or some complex type same object will be added.
        return propertyForCopying;

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
    <T> boolean invokeSetMethod(final Method method, final Class<?> classObject, final String propertyName,
            final T object, final Object copiedProperty) {
        Method setMethod = null;

        try {
            // TODO(nolah) maybe move this to ReflectionUtil
            setMethod = classObject.getMethod(SET_METHOD_PREFIX + StringUtils.capitalize(propertyName),
                    method.getReturnType());
            setMethod.invoke(object, copiedProperty);
            return true;
        } catch (final Exception e) {
            fail(e.getMessage());
            return false;
        }
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
    // TODO move to reflection util
    <T> T createEmptyCopyOf(final T object) {
        try {
            return (T) object.getClass().getConstructor().newInstance();
        } catch (final Exception e) {
            return null;
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
    private <T> T createCopyObject(final T object, final NodesList nodes) {

        T copy = nodes.getExpected(object);
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
                final Object copiedProperty = copyProperty(propertyForCopying, nodes);
                if (!invokeSetMethod(method, classObject, propertyName, copy, copiedProperty)) {
                    return null;
                }
            }
        }
        return copy;
    }

    /**
     * Initialize database snapshot.
     */
    void initDbSnapshot() {
        dbSnapshot.clear();
        for (final Class<?> entityType : entityTypes) {
            getDbSnapshot().put(entityType, new HashMap<EntityId, CopyAssert<Object>>());
        }
    }

    /**
     * Initialize parameters snapshot.
     */
    void initParametersSnapshot() {
        parameters.clear();
        parametersSnapshot.clear();
    }

    @Override
    protected <X> void customAssertEquals(final X expected, final X actual) {
        Assert.assertEquals(expected, actual);
    }

}
