package eu.execom.testutil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import eu.execom.testutil.enums.ObjectType;
import eu.execom.testutil.graph.NodesList;
import eu.execom.testutil.pair.AssertPair;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.report.FabutReportBuilder;
import eu.execom.testutil.util.ReflectionUtil;

/**
 * ExeCom test util class. Extends {@link FabutObjectAssert} with possibility to assert entire repository (DB ...) .
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
@SuppressWarnings({"unchecked", "rawtypes"})
public class FabutRepositoryAssert extends FabutObjectAssert {

    /** The db snapshot. */
    private final Map<Class<?>, Map<Object, CopyAssert>> dbSnapshot;

    /**
     * Default constructor.
     */
    public FabutRepositoryAssert() {
        super();
        dbSnapshot = new HashMap<Class<?>, Map<Object, CopyAssert>>();
    }

    @Override
    boolean assertPair(final String propertyName, final FabutReportBuilder report, final AssertPair pair,
            final List<ISingleProperty> properties, final NodesList nodesList) {

        if (pair.getObjectType() == ObjectType.ENTITY_TYPE) {
            if (pair.isProperty()) {
                return assertEntityById(report, propertyName, pair);
            } else {
                return assertSubfields(report, pair, properties, nodesList);
            }
        }
        return super.assertPair(propertyName, report, pair, properties, nodesList);
    }

    // TODO comments
    public void assertEntityAsDeleted(final Object actual) {

        ignoreEntity(actual);

        final Object findById = findById(actual.getClass(), ReflectionUtil.getIdValue(actual));
        Assert.assertNull(findById);
    }

    // TODO comments
    public void ignoreEntity(final Object actual) {

        if (ReflectionUtil.isEntityType(actual.getClass(), getTypes())) {
            markAsserted(actual);
        }

    }

    // TODO comments
    public void assertEntityWithSnapshot(final FabutReportBuilder report, final Object entity,
            final List<ISingleProperty> properties) {

        final Object id = ReflectionUtil.getIdValue(entity);
        Assert.assertNotNull("Entity id can't be null " + entity, id);

        final Map<Object, CopyAssert> map = dbSnapshot.get(entity.getClass());

        final CopyAssert copyAssert = map.get(id);
        if (copyAssert != null) {
            final Object expected = copyAssert.getEntity();
            assertObjects(report, expected, entity, properties);
        } else {
            // TODO add reporting to this
            Assert.fail("Entity doesn't exist in snapshot  " + entity);
        }

    }

    public final void afterAssertEntity(final Object object, final boolean isProperty) {
        if (ReflectionUtil.isEntityType(object.getClass(), getTypes()) && !isProperty
                && ReflectionUtil.getIdValue(object) != null) {
            markAsserted(object);
        }
    }

    /**
     * Find all entities of type entity class in DB.
     * 
     * @param entityClass
     *            the entity class
     * @return the list
     */
    // TODO this should be changed to use test instance
    protected List<Object> findAll(final Class<?> entityClass) {
        return Fabut.findAll(entityClass);
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
    // TODO this should be changed to use test instance
    protected Object findById(final Class<?> entityClass, final Object id) {
        return Fabut.findById(entityClass, id);
    }

    /**
     * Mark entity bean as asserted.
     * 
     * @param entity
     *            AbstractEntity
     */
    protected void markAsserted(final Object entity) {
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
    protected boolean markAsAsserted(final Object entity, final Class<?> actualType) {

        final Object id = ReflectionUtil.getIdValue(entity);
        Assert.assertNotNull("Entity id can't be null " + entity, id);
        final Map<Object, CopyAssert> map = dbSnapshot.get(actualType);
        final boolean isTypeSupported = map != null;

        if (isTypeSupported) {
            CopyAssert copyAssert = map.get(id);
            if (copyAssert == null) {
                copyAssert = new CopyAssert(ReflectionUtil.createCopy(entity, getTypes()));
                map.put(ReflectionUtil.getIdValue(entity), copyAssert);
            }
            copyAssert.setAsserted(true);
        }

        final Class<?> superClassType = actualType.getSuperclass();
        final boolean isSuperSuperTypeSupported = (superClassType != null) && markAsAsserted(entity, superClassType);
        return isTypeSupported || isSuperSuperTypeSupported;
    }

    /**
     * Takes current database snapshot and saves it.
     */
    public void takeSnapshot() {
        initDbSnapshot();

        for (final Entry<Class<?>, Map<Object, CopyAssert>> entry : dbSnapshot.entrySet()) {
            final List<?> findAll = findAll(entry.getKey());

            for (final Object abstractEntity : findAll) {
                entry.getValue().put(ReflectionUtil.getIdValue(abstractEntity),
                        new CopyAssert(ReflectionUtil.createCopy(abstractEntity, getTypes())));
            }
        }
    }

    /**
     * Asserts all previously taken snapshots.
     */
    @Override
    protected void assertSnapshot() {
        super.assertSnapshot();
        assertDbState();
    }

    /**
     * Asserts db snapshot with after db state.
     */
    public void assertDbState() {
        boolean ok = true;
        final FabutReportBuilder report = new FabutReportBuilder();

        // assert entities by classes
        for (final Entry<Class<?>, Map<Object, CopyAssert>> snapshotEntry : dbSnapshot.entrySet()) {

            final Map<Object, Object> afterEntities = getAfterEntities(snapshotEntry.getKey());
            final TreeSet beforeIds = new TreeSet(snapshotEntry.getValue().keySet());
            final TreeSet afterIds = new TreeSet(afterEntities.keySet());

            ok &= checkNotExistingInAfterDbState(beforeIds, afterIds, snapshotEntry.getValue(), report);
            ok &= checkAddedToAfterDbState(beforeIds, afterIds, afterEntities, report);
            ok &= assertDbSnapshotWithAfterState(beforeIds, afterIds, snapshotEntry.getValue(), afterEntities, report);

        }
        if (!ok) {
            throw new AssertionFailedError(report.getMessage());
        }

    }

    /**
     * Performs assert check on entities that are contained in db snapshot but do not exist in after db state.
     * 
     * @param beforeIds
     *            the before ids
     * @param afterIds
     *            the after ids
     * @param beforeEntities
     *            the before entities
     * @param report
     *            the report
     * @return <code>true</code> if all entities contained only in db snapshot are asserted, <code>false</code>
     *         otherwise.
     */
    boolean checkNotExistingInAfterDbState(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, CopyAssert> beforeEntities, final FabutReportBuilder report) {

        final TreeSet beforeIdsCopy = new TreeSet(beforeIds);
        boolean ok = ASSERTED;
        // does difference between db snapshot and after db state
        beforeIdsCopy.removeAll(afterIds);
        for (final Object id : beforeIdsCopy) {
            final CopyAssert copyAssert = beforeEntities.get(id);
            if (!copyAssert.isAsserted()) {
                ok = ASSERT_FAIL;
                report.reportNoEntityFailure(beforeEntities.get(id));
            }
        }

        return ok;
    }

    /**
     * Performs check if there is any entity in after db state that has not been asserted and reports them.
     * 
     * @param beforeIds
     *            the before ids
     * @param afterIds
     *            the after ids
     * @param afterEntities
     *            the after entities
     * @param report
     *            the report
     * @return <code>true</code> if all entities in after db state are asserted.
     */
    boolean checkAddedToAfterDbState(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, Object> afterEntities, final FabutReportBuilder report) {

        final TreeSet afterIdsCopy = new TreeSet(afterIds);
        boolean ok = ASSERTED;
        // does difference between after db state and db snapshot
        afterIdsCopy.removeAll(beforeIds);
        for (final Object id : afterIdsCopy) {
            final Object entity = afterEntities.get(id);
            ok = ASSERT_FAIL;
            report.reportEntityIsntAsserted(entity);
        }
        return ok;
    }

    // TODO tests, comments
    boolean assertDbSnapshotWithAfterState(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, CopyAssert> beforeEntities, final Map<Object, Object> afterEntities,
            final FabutReportBuilder report) {

        final TreeSet beforeIdsCopy = new TreeSet(beforeIds);
        // does intersection between db snapshot and after db state
        beforeIdsCopy.retainAll(afterIds);
        boolean ok = ASSERTED;
        for (final Object id : beforeIdsCopy) {
            if (!beforeEntities.get(id).isAsserted()) {
                ok &= assertObjects(report, beforeEntities.get(id).getEntity(), afterEntities.get(id),
                        new LinkedList<ISingleProperty>());
            }
        }
        return ok;
    }

    Map getAfterEntities(final Class<?> clazz) {
        final Map afterEntities = new TreeMap();
        final List entities = findAll(clazz);
        for (final Object entity : entities) {
            final Object id = ReflectionUtil.getIdValue(entity);
            if (id != null) {
                afterEntities.put(id, entity);
            }
        }
        return afterEntities;
    }

    Map<Class<?>, Map<Object, CopyAssert>> getDbSnapshot() {
        return dbSnapshot;
    }

    /**
     * Initialize database snapshot.
     */
    void initDbSnapshot() {
        dbSnapshot.clear();
        for (final Class<?> entityType : getEntityTypes()) {
            getDbSnapshot().put(entityType, new HashMap<Object, CopyAssert>());
        }
    }

    @Override
    protected void customAssertEquals(final Object expected, final Object actual) {
        Assert.assertEquals(expected, actual);
    }

    public void setEntityTypes(final List<Class<?>> entityTypes) {
        getTypes().put(ObjectType.ENTITY_TYPE, entityTypes);
    }

    /**
     * Asserts two entities by their id.
     * 
     * @param <Id>
     *            entities id type
     * @param report
     *            assert report builder
     * @param propertyName
     *            name of current entity
     * @param expected
     *            expected entity
     * @param actual
     *            actual entity
     * @return - <code>true</code> if and only if ids of two specified objects are equal, <code>false</code> otherwise
     */
    boolean assertEntityById(final FabutReportBuilder report, final String propertyName, final AssertPair pair) {

        final Object expectedId = ReflectionUtil.getIdValue(pair.getExpected());
        final Object actualId = ReflectionUtil.getIdValue(pair.getActual());

        boolean ok = true;
        try {
            assertEquals(expectedId, actualId);
            ok = true;
        } catch (final AssertionError e) {
            ok = false;
        }
        report.reportEntityAssert(expectedId, actualId, ok);
        return ok;
    }

}
