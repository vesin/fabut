package eu.execom.fabut;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Assert;
import eu.execom.fabut.enums.AssertType;
import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.graph.NodesList;
import eu.execom.fabut.pair.AssertPair;
import eu.execom.fabut.property.CopyAssert;
import eu.execom.fabut.property.ISingleProperty;
import eu.execom.fabut.report.FabutReportBuilder;
import eu.execom.fabut.util.ReflectionUtil;

/**
 * ExeCom test util class. Extends {@link FabutObjectAssert} with possibility to assert entire repository (DB ...) .
 * TODO think of better comment.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class FabutRepositoryAssert extends FabutObjectAssert {

    /** The db snapshot. */
    private Map<Class<?>, Map<Object, CopyAssert>> dbSnapshot;
    private IRepositoryFabutTest repositoryFabutTest;
    private final AssertType assertType;

    /**
     * Default constructor.
     */
    public FabutRepositoryAssert(final IRepositoryFabutTest repositoryFabutTest) {
        super(repositoryFabutTest);
        this.repositoryFabutTest = repositoryFabutTest;
        dbSnapshot = new HashMap<Class<?>, Map<Object, CopyAssert>>();
        assertType = AssertType.REPOSITORY_ASSERT;
        getTypes().put(AssertableType.ENTITY_TYPE, repositoryFabutTest.getEntityTypes());
    }

    public FabutRepositoryAssert(final IFabutTest fabutTest) {
        super(fabutTest);
        assertType = AssertType.OBJECT_ASSERT;
    }

    @Override
    protected boolean assertEntityPair(final FabutReportBuilder report, final String propertyName,
            final AssertPair pair, final List<ISingleProperty> properties, final NodesList nodesList) {
        if (assertType == AssertType.OBJECT_ASSERT) {
            return super.assertEntityPair(report, propertyName, pair, properties, nodesList);
        }

        if (pair.isProperty()) {
            return assertEntityById(report, propertyName, pair);
        } else {
            return assertSubfields(report, pair, properties, nodesList);
        }
    }

    /**
     * Asserts that entity has been deleted in after db state.
     * 
     * @param entity
     * @return <code>true</code> if entity is really deleted, <code>false</code> otherwise.
     */
    public boolean assertEntityAsDeleted(final FabutReportBuilder report, final Object entity) {

        final boolean ignoreEntity = ignoreEntity(report, entity);

        final Object findById = findById(entity.getClass(), ReflectionUtil.getIdValue(entity));
        final boolean isDeletedInRepository = findById == null;

        if (!isDeletedInRepository) {
            report.notDeletedInRepositoy(entity);
        }
        return ignoreEntity && isDeletedInRepository;
    }

    /**
     * Ignores the entity.
     * 
     * @param report
     *            the report
     * @param entity
     *            the actual
     * @return <code>true</code> if entity can be found in db snapshot, <code>false</code> otherwise.
     */
    public boolean ignoreEntity(final FabutReportBuilder report, final Object entity) {
        return markAsAsserted(report, entity, entity.getClass());
    }

    /**
     * Asserts specified entity with entity with of same class with same id in db snapshot.
     * 
     * @param report
     * @param entity
     * @param properties
     * @return <code>true</code> if entity can be asserted with one in the snapshot, <code>false</code> otherwise.
     */
    public boolean assertEntityWithSnapshot(final FabutReportBuilder report, final Object entity,
            final List<ISingleProperty> properties) {

        final Object id = ReflectionUtil.getIdValue(entity);

        final Map<Object, CopyAssert> map = dbSnapshot.get(entity.getClass());

        final CopyAssert copyAssert = map.get(id);
        if (copyAssert != null) {
            final Object expected = copyAssert.getEntity();
            return assertObjects(report, expected, entity, properties);
        } else {
            return ASSERT_FAIL;
        }

    }

    @Override
    boolean afterAssertObject(final Object object, final boolean isSubproperty) {
        return afterAssertEntity(new FabutReportBuilder(), object, isSubproperty);
    }

    /**
     * This method needs to be called after every entity assert so it marks that entity has been asserted in snapshot.
     * 
     * @param entity
     * @param isProperty
     * @return <code>true</code> if entity can be marked that is asserted, <code>false</code> otherwise.
     */
    final boolean afterAssertEntity(final FabutReportBuilder report, final Object entity, final boolean isProperty) {
        if (!isProperty) {
            return markAsAsserted(report, entity, entity.getClass());
        } else {
            return ASSERTED;
        }
    }

    /**
     * Find all entities of type entity class in DB.
     * 
     * @param entityClass
     *            the entity class
     * @return the list
     */
    protected List<Object> findAll(final Class<?> entityClass) {
        return repositoryFabutTest.findAll(entityClass);
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
        return repositoryFabutTest.findById(entityClass, id);
    }

    /**
     * Mark entity bean as asserted.
     * 
     * @param entity
     *            AbstractEntity
     */

    /**
     * Marks the specified entity as asserted.
     * 
     * @param entity
     *            the entity
     * @param actualType
     *            the actual type
     * @return <code>true</code> if entity is successfully asserted else return <code>false</code>.
     */
    protected boolean markAsAsserted(final FabutReportBuilder report, final Object entity, final Class<?> actualType) {

        final Object id = ReflectionUtil.getIdValue(entity);
        if (id == null) {
            report.idNull(actualType);
            return ASSERT_FAIL;
        }
        if (!markAsserted(id, entity, actualType)) {
            report.notExistingInSnapshot(entity);
            return ASSERT_FAIL;
        }
        return ASSERTED;
    }

    /**
     * Mark entity bean as asserted in db snapshot map. Go trough all its supper classes and if its possible assert it.
     * TODO tests
     * 
     * @param id
     *            the id
     * @param entity
     *            the entity
     * @param actualType
     *            the actual type
     * @return true, if successful
     */
    protected boolean markAsserted(final Object id, final Object entity, final Class<?> actualType) {
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
        final boolean isSuperSuperTypeSupported = (superClassType != null) && markAsserted(id, entity, superClassType);

        return isTypeSupported || isSuperSuperTypeSupported;
    }

    /**
     * Takes current database snapshot and saves it. TODO rework this so it can report entities that cannot be copied.
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
     * Asserts db snapshot with after db state.
     */
    protected boolean assertDbSnapshot(final FabutReportBuilder report) {
        boolean ok = true;
        // assert entities by classes
        for (final Entry<Class<?>, Map<Object, CopyAssert>> snapshotEntry : dbSnapshot.entrySet()) {

            final Map<Object, Object> afterEntities = getAfterEntities(snapshotEntry.getKey());
            final TreeSet beforeIds = new TreeSet(snapshotEntry.getValue().keySet());
            final TreeSet afterIds = new TreeSet(afterEntities.keySet());

            ok &= checkNotExistingInAfterDbState(beforeIds, afterIds, snapshotEntry.getValue(), report);
            ok &= checkNewToAfterDbState(beforeIds, afterIds, afterEntities, report);
            ok &= assertDbSnapshotWithAfterState(beforeIds, afterIds, snapshotEntry.getValue(), afterEntities, report);

        }
        return ok;

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
                report.noEntityInSnapshot(copyAssert.getEntity());
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
    boolean checkNewToAfterDbState(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, Object> afterEntities, final FabutReportBuilder report) {

        final TreeSet afterIdsCopy = new TreeSet(afterIds);
        boolean ok = ASSERTED;
        // does difference between after db state and db snapshot
        afterIdsCopy.removeAll(beforeIds);
        for (final Object id : afterIdsCopy) {
            final Object entity = afterEntities.get(id);
            ok = ASSERT_FAIL;
            report.entityNotAssertedInAfterState(entity);
        }
        return ok;
    }

    /**
     * TODO comments Assert db snapshot with after state.
     * 
     * @param beforeIds
     *            the before ids
     * @param afterIds
     *            the after ids
     * @param beforeEntities
     *            the before entities
     * @param afterEntities
     *            the after entities
     * @param report
     *            the report
     * @return true, if successful
     */
    boolean assertDbSnapshotWithAfterState(final TreeSet beforeIds, final TreeSet afterIds,
            final Map<Object, CopyAssert> beforeEntities, final Map<Object, Object> afterEntities,
            final FabutReportBuilder report) {

        final TreeSet beforeIdsCopy = new TreeSet(beforeIds);
        // does intersection between db snapshot and after db state
        System.out.println(afterIds);
        System.out.println(beforeIdsCopy);
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
        getTypes().put(AssertableType.ENTITY_TYPE, entityTypes);
    }

    /**
     * Asserts two entities by their id.
     * 
     * @param report
     *            assert report builder
     * @param propertyName
     *            name of current entity
     * @return - <code>true</code> if and only if ids of two specified objects are equal, <code>false</code> otherwise
     */
    boolean assertEntityById(final FabutReportBuilder report, final String propertyName, final AssertPair pair) {

        final Object expectedId = ReflectionUtil.getIdValue(pair.getExpected());
        final Object actualId = ReflectionUtil.getIdValue(pair.getActual());
        try {
            assertEquals(expectedId, actualId);
            report.asserted(pair, propertyName);
            return ASSERTED;
        } catch (final AssertionError e) {
            report.assertFail(pair, propertyName);
            return ASSERT_FAIL;
        }
    }

}
