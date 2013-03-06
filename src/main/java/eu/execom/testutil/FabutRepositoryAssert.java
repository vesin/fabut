package eu.execom.testutil;

import java.util.ArrayList;
import java.util.HashMap;
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
import eu.execom.testutil.report.AssertReportBuilder;
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
    // TODO tests
    boolean assertPair(final String propertyName, final AssertReportBuilder report, final AssertPair pair,
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
    public void assertEntityWithSnapshot(final AssertReportBuilder report, final Object actual,
            final List<ISingleProperty> properties) {

        final Object id = ReflectionUtil.getIdValue(actual);
        Assert.assertNotNull("Entity id can't be null " + actual, id);

        final Map<Object, CopyAssert> map = dbSnapshot.get(actual.getClass());
        final boolean isTypeSupported = map != null;

        // TODO this should be moved to TestUtilAssert, this method should work only with entities that are type
        // supported
        if (isTypeSupported) {
            final CopyAssert copyAssert = map.get(id);
            if (copyAssert != null) {
                final Object expected = copyAssert.getEntity();
                // final TODO remove this null check, test should fail when taking
                // snapshot fails due final to object not final being able to final be copied
                if (expected != null) {
                    assertObjects(report, expected, actual, properties);
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

        // TODO type support check should be handled outside of this method
        if (isTypeSupported) {
            CopyAssert copyAssert = map.get(id);
            if (copyAssert == null) {
                copyAssert = new CopyAssert(ReflectionUtil.createCopy(entity, types));
                map.put(ReflectionUtil.getIdValue(entity), copyAssert);
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

        for (final Entry<Class<?>, Map<Object, CopyAssert>> entry : dbSnapshot.entrySet()) {
            final List<?> findAll = findAll(entry.getKey());

            for (final Object abstractEntity : findAll) {
                entry.getValue().put(ReflectionUtil.getIdValue(abstractEntity),
                        new CopyAssert(ReflectionUtil.createCopy(abstractEntity, types)));
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
     * Asserts current database snapshot with one previously taken.
     */
    public void assertDbState() {
        boolean ok = true;
        final AssertReportBuilder report = new AssertReportBuilder();

        // assert entities by classes
        for (final Entry<Class<?>, Map<Object, CopyAssert>> snapshotEntry : dbSnapshot.entrySet()) {

            final Map<Object, Object> afterEntities = getAfterEntities(snapshotEntry.getKey());
            final TreeSet beforeIds = new TreeSet(snapshotEntry.getValue().keySet());
            final TreeSet afterIds = new TreeSet(afterEntities.keySet());

            ok &= assertBeforeSnapshotDifference(beforeIds, afterIds, snapshotEntry.getValue(), report);
            ok &= assertAfterSnapshotDifference(beforeIds, afterIds, afterEntities, report);
            ok &= assertSnapshots(beforeIds, afterIds, snapshotEntry.getValue(), afterEntities, report);

        }
        if (!ok) {
            throw new AssertionFailedError(report.getMessage());
        }

    }

    // TODO comments
    boolean assertBeforeSnapshotDifference(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, CopyAssert> beforeEntities, final AssertReportBuilder report) {

        final TreeSet beforeIdsCopy = new TreeSet(beforeIds);
        boolean ok = true;
        // TODO remove if
        if (beforeIdsCopy.removeAll(afterIds)) {
            for (final Object id : beforeIdsCopy) {
                final CopyAssert copyAssert = beforeEntities.get(id);
                if (!copyAssert.isAsserted()) {
                    ok = false;
                    report.reportNoEntityFailure(beforeEntities.get(id));
                }
            }
        }
        return ok;
    }

    // TODO comments
    boolean assertAfterSnapshotDifference(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, Object> afterEntities, final AssertReportBuilder report) {

        final TreeSet afterIdsCopy = new TreeSet(afterIds);
        boolean ok = true;
        // TODO remove if
        if (afterIdsCopy.removeAll(beforeIds)) {
            for (final Object id : afterIdsCopy) {
                final Object entity = afterEntities.get(id);
                ok = false;
                report.reportEntityIsntAsserted(entity);
            }
        }
        return ok;
    }

    // TODO tests, comments
    boolean assertSnapshots(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, CopyAssert> beforeEntities, final Map<Object, Object> afterEntities,
            final AssertReportBuilder report) {

        final TreeSet beforeIdsCopy = new TreeSet(beforeIds);
        beforeIdsCopy.retainAll(afterIds);
        boolean ok = true;
        for (final Object id : beforeIdsCopy) {
            if (!beforeEntities.get(id).isAsserted()) {
                ok &= assertEntities(beforeEntities.get(id).getEntity(), afterEntities.get(id), report);
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
     * Calls assertObjects of {@link FabutObjectAssert} to assert two entities.
     * 
     * @param the
     *            generic type
     * @param beforeEntity
     *            entity from before snapshot
     * @param afterEntity
     *            entity from current snapshot
     * @param report
     *            report builder
     * @return <code>true</code> if entities are asserted, i.e. assertObjects doesn't throw {@link AssertionError},
     *         <code>false</code> otherwise
     */
    boolean assertEntities(final Object beforeEntity, final Object afterEntity, final AssertReportBuilder report) {
        try {
            assertObjects(new AssertReportBuilder(), beforeEntity, afterEntity, new ArrayList<ISingleProperty>());
            return true;
        } catch (final AssertionError e) {
            report.reportRepositoryEntityAssertFail(beforeEntity, afterEntity);
            report.append(e.getMessage());
            return false;
        }
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
    // TODO move this method to repository assert
    boolean assertEntityById(final AssertReportBuilder report, final String propertyName, final AssertPair pair) {

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
