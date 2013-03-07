package eu.execom.testutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import eu.execom.testutil.enums.ObjectType;
import eu.execom.testutil.graph.NodesList;
import eu.execom.testutil.model.A;
import eu.execom.testutil.model.B;
import eu.execom.testutil.model.C;
import eu.execom.testutil.model.DoubleLink;
import eu.execom.testutil.model.EntityTierOneType;
import eu.execom.testutil.model.EntityTierThreeType;
import eu.execom.testutil.model.EntityTierTwoType;
import eu.execom.testutil.model.IgnoredMethodsType;
import eu.execom.testutil.model.IgnoredType;
import eu.execom.testutil.model.NoGetMethodsType;
import eu.execom.testutil.model.Start;
import eu.execom.testutil.model.TierFiveType;
import eu.execom.testutil.model.TierFourType;
import eu.execom.testutil.model.TierOneType;
import eu.execom.testutil.model.TierSixType;
import eu.execom.testutil.model.TierThreeType;
import eu.execom.testutil.model.TierTwoType;
import eu.execom.testutil.model.TierTwoTypeWithIgnoreProperty;
import eu.execom.testutil.model.TierTwoTypeWithListProperty;
import eu.execom.testutil.model.TierTwoTypeWithPrimitiveProperty;
import eu.execom.testutil.model.UnknownEntityType;
import eu.execom.testutil.pair.AssertPair;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.report.FabutReportBuilder;
import eu.execom.testutil.util.ConversionUtil;

/**
 * Tests for {@link FabutRepositoryAssert}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class FabutRepositoryAssertTest extends AbstractExecomRepositoryAssertTest {

    private static final String TEST = "test";
    private static final String PROPERTY = "property";

    @Before
    public void before() {
        final List<Class<?>> entityTypes = new LinkedList<Class<?>>();
        entityTypes.add(EntityTierOneType.class);
        entityTypes.add(EntityTierTwoType.class);
        setEntityTypes(entityTypes);

        final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
        complexTypes.add(A.class);
        complexTypes.add(B.class);
        complexTypes.add(C.class);
        complexTypes.add(TierOneType.class);
        complexTypes.add(TierTwoType.class);
        complexTypes.add(TierThreeType.class);
        complexTypes.add(TierFourType.class);
        complexTypes.add(TierFiveType.class);
        complexTypes.add(TierSixType.class);
        complexTypes.add(NoGetMethodsType.class);
        complexTypes.add(IgnoredMethodsType.class);
        complexTypes.add(TierTwoTypeWithIgnoreProperty.class);
        complexTypes.add(TierTwoTypeWithListProperty.class);
        complexTypes.add(TierTwoTypeWithPrimitiveProperty.class);

        complexTypes.add(DoubleLink.class);
        complexTypes.add(Start.class);
        setComplexTypes(complexTypes);

        final List<Class<?>> ignoredTypes = new LinkedList<Class<?>>();
        ignoredTypes.add(IgnoredType.class);
        setIgnoredTypes(ignoredTypes);

        initDbSnapshot();
        initParametersSnapshot();

        getTypes().put(ObjectType.ENTITY_TYPE, entityTypes);
        getTypes().put(ObjectType.COMPLEX_TYPE, complexTypes);
        getTypes().put(ObjectType.IGNORED_TYPE, ignoredTypes);
    }

    /**
     * Test for assertDbState of {@link FabutRepositoryAssert} when before snapshot matches after snapshot.
     */
    @Test
    public void testAssertDbStateTrue() {
        // setup
        final List<Object> beforeList1 = new ArrayList<Object>();
        beforeList1.add(new EntityTierOneType(TEST + TEST, 1));
        setList1(beforeList1);

        final List<Object> beforeList2 = new ArrayList<Object>();
        beforeList2.add(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, 4, new EntityTierOneType(TEST + TEST
                + TEST + TEST, 4)));
        setList2(beforeList2);

        takeSnapshot();

        final List<Object> afterList1 = new ArrayList<Object>();
        afterList1.add(new EntityTierOneType(TEST + TEST, 1));
        setList1(afterList1);

        final List<Object> afterist2 = new ArrayList<Object>();
        afterist2.add(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, 4, new EntityTierOneType(TEST + TEST + TEST
                + TEST, 4)));
        setList2(afterist2);

        // method
        assertDbState();
    }

    /**
     * Test for assertDbState of {@link FabutRepositoryAssert} when before snapshot doesn't match after snapshot.
     */
    @Test(expected = AssertionError.class)
    // TODO(nolah) test is probably bad now that test util isnt extended anymore
    public void testAssertDbStateFalse() {
        // setup

        final List<Object> beforeList1 = new ArrayList<Object>();
        beforeList1.add(new EntityTierOneType(TEST, 1));
        setList1(beforeList1);

        final List<Object> beforeList2 = new ArrayList<Object>();
        beforeList2.add(new EntityTierTwoType(PROPERTY, 4, new EntityTierOneType(TEST, 7)));
        setList2(beforeList2);

        takeSnapshot();

        final List<Object> afterList1 = new ArrayList<Object>();
        afterList1.add(new EntityTierOneType(TEST + TEST, 1));
        setList1(afterList1);

        final List<Object> afterList2 = new ArrayList<Object>();
        afterList2.add(new EntityTierTwoType(PROPERTY + PROPERTY, 4, new EntityTierOneType(TEST + TEST, 7)));
        setList2(afterList2);

        // method
        assertDbState();
    }

    /**
     * Test for markEntityAsDeleted of {@link FabutRepositoryAssert} when entity is marked as deleted.
     */
    @Test
    public void testAssertEntityAsDeletedEntity() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        list1.add(new EntityTierOneType(TEST, 2));
        setList1(list1);

        final EntityTierOneType actual = new EntityTierOneType(TEST, 1);
        takeSnapshot();

        final List<Object> list2 = new ArrayList<Object>();
        list2.add(new EntityTierOneType(TEST, 2));
        setList1(list2);

        // method
        assertEntityAsDeleted(actual);
        assertDbState();
    }

    /**
     * Test for markEntityAsDeleted of {@link FabutRepositoryAssert} when specified object is not entity.
     */
    @Test
    public void testAssertAsDeletedNotEntity() {
        // method
        takeSnapshot();
        assertEntityAsDeleted(new TierOneType());
    }

    /**
     * Test for ignoreEntity of {@link FabutRepositoryAssert} when specified entity is ignored.
     */
    @Test
    public void testIgnoreEntityEntity() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        list1.add(new EntityTierOneType(TEST, 2));
        setList1(list1);

        final EntityTierOneType actual = new EntityTierOneType(TEST, 1);
        takeSnapshot();

        final List<Object> list2 = new ArrayList<Object>();
        list2.add(new EntityTierOneType(TEST, 2));
        setList1(list2);

        // method
        ignoreEntity(actual);
        assertDbState();
    }

    /**
     * Test for ignoreEntity of {@link FabutRepositoryAssert} when specified object is not entity.
     */
    @Test
    public void testIngoreEntityNotEntity() {
        // method
        takeSnapshot();
        ignoreEntity(new TierOneType());
    }

    /**
     * Test for afterAssertEntity of {@link FabutRepositoryAssert} when specified object is entity and it is not
     * property.
     */
    @Test
    public void testAfterAssertEntityParentEntityNotProperty() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        list1.add(new EntityTierOneType(TEST, 2));
        setList1(list1);

        final EntityTierOneType actual = new EntityTierOneType(TEST, 1);
        takeSnapshot();

        final List<Object> list2 = new ArrayList<Object>();
        list2.add(new EntityTierOneType(TEST, 2));
        setList1(list2);

        // method
        afterAssertEntity(actual, false);
        assertDbState();
    }

    /**
     * Test for afterAssertEntity of {@link FabutRepositoryAssert} when specified object is entity and it is property of
     * another entity.
     */
    @Test(expected = AssertionError.class)
    public void testAfterAssertEntityIsProperty() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        list1.add(new EntityTierOneType(TEST, 2));
        setList1(list1);

        final EntityTierOneType actual = new EntityTierOneType(TEST, 1);
        takeSnapshot();

        final List<Object> list2 = new ArrayList<Object>();
        list2.add(new EntityTierOneType(TEST, 2));
        setList1(list2);

        // method
        afterAssertEntity(actual, true);
        assertDbState();
    }

    /**
     * Test for afterAssertEntity of {@link FabutRepositoryAssert} when specified object is entity and it is property of
     * another entity.
     */
    @Test(expected = AssertionError.class)
    public void testAfterAssertEntityNotEntity() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        list1.add(new EntityTierOneType(TEST, 2));
        setList1(list1);

        final TierOneType actual = new TierOneType();
        takeSnapshot();

        final List<Object> list2 = new ArrayList<Object>();
        list2.add(new EntityTierOneType(TEST, 2));
        setList1(list2);

        // method
        afterAssertEntity(actual, false);
        assertDbState();
    }

    /**
     * Test for afterAssertEntity of {@link FabutRepositoryAssert} when specified object is entity and it is not
     * property of another entity but its id is <code>null</code>.
     */
    @Test
    public void testAfterAssertEntityWithoutID() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        list1.add(new EntityTierOneType(TEST, 2));
        setList1(list1);

        final EntityTierOneType actual = new EntityTierOneType(TEST, 1);
        takeSnapshot();

        // method
        afterAssertEntity(actual, false);
        assertDbState();
    }

    /**
     * Test for markAsAsserted of {@link FabutRepositoryAssert} when specified type is not type supported.
     */
    @Test
    public void testMarkAssertedNotTypeSupportedTrue() {
        // setup
        final List<Object> list = new ArrayList<Object>();
        setList2(list);

        // method
        takeSnapshot();
        list.add(new EntityTierTwoType(TEST, 1, new EntityTierOneType(PROPERTY, 10)));
        final boolean assertValue = markAsAsserted(
                new EntityTierThreeType(TEST, 1, new EntityTierOneType(PROPERTY, 10)), EntityTierThreeType.class);

        // assert
        assertDbState();
        assertTrue(assertValue);
    }

    /**
     * Test for markAsAsserted of {@link FabutRepositoryAssert} when specified type and its superclass are not
     * supported.
     */
    @Test
    public void testMarkAssertedNotTyrpeSupportedFalse() {
        // method
        takeSnapshot();
        final boolean assertValue = markAsAsserted(new UnknownEntityType(4), UnknownEntityType.class);

        // assert
        assertDbState();
        assertFalse(assertValue);
    }

    /**
     * Test for markAsAsserted of {@link FabutRepositoryAssert} when there is no {@link CopyAssert} in db snapshot.
     */
    @Test
    public void testMarkAssertedCopyAssertNull() {
        // setup
        final List<Object> list = new ArrayList<Object>();
        setList2(list);

        // method
        takeSnapshot();
        list.add(new EntityTierTwoType(TEST, 1, new EntityTierOneType(PROPERTY, 10)));
        final boolean assertValue = markAsAsserted(new EntityTierTwoType(TEST, 1, new EntityTierOneType(PROPERTY, 10)),
                EntityTierTwoType.class);

        // assert
        assertDbState();
        assertTrue(assertValue);
    }

    /**
     * Test for markAsAsserted of {@link FabutRepositoryAssert} when {@link CopyAssert} exists in db snapshot.
     */
    @Test
    public void testMarkAssertedCopyAssertNotNull() {
        // setup
        final EntityTierTwoType entity = new EntityTierThreeType(TEST, 1, new EntityTierOneType(PROPERTY, 10));
        final List<Object> list = new ArrayList<Object>();
        list.add(entity);
        setList2(list);

        // method
        takeSnapshot();
        entity.setProperty(TEST + TEST);
        final boolean assertValue = markAsAsserted(new EntityTierTwoType(TEST, 1, new EntityTierOneType(PROPERTY, 10)),
                EntityTierTwoType.class);

        // assert
        assertDbState();
        assertTrue(assertValue);
    }

    // TODO(nolah) fix this
    // /**
    // * Test for takeSnapshot of {@link AbstractExecomRepositoryAssert} if it initializes snapshot properly.
    // */
    // @Test
    // public void testTakeSnapshot() {
    // // setup
    // final EntityTierTwoType entity = new EntityTierThreeType(TEST, 1, new EntityTierOneType(PROPERTY, 10));
    // final List<Object> list = new ArrayList<Object>();
    // list.add(entity);
    // setList2(list);
    //
    // // method
    // takeSnapshot();
    // final Map<Class<?>, Map<Integer, CopyAssert<Type>>> dbSnapshot = getDbSnapshot();
    //
    // // assert
    // assertEquals(2, dbSnapshot.size());
    // for (final Entry<Class<?>, Map<Integer, CopyAssert<Type>>> classEntry : dbSnapshot.entrySet()) {
    // if (classEntry.getKey() == EntityTierOneType.class) {
    // assertEquals(0, classEntry.getValue().size());
    // }
    //
    // if (classEntry.getKey() == EntityTierTwoType.class) {
    // assertEquals(1, classEntry.getValue().size());
    // final Object object = classEntry.getValue().get(1);
    // final CopyAssert copyAssert = (CopyAssert) object;
    // final EntityTierTwoType assertEntity = copyAssert.getEntity();
    // assertEquals(1, assertEntity.getId());
    // assertEquals(TEST, assertEntity.getProperty());
    // assertEquals(PROPERTY, assertEntity.getSubProperty().getProperty());
    // assertEquals(new Integer(10), assertEntity.getSubProperty().getId());
    // }
    // }
    // }

    // TODO this should be reworked when takeSnapshot functionality is changed

    // /**
    // * Test for assertEntityWithSnapshot from {@link AbstractExecomRepositoryAssert} when id is null.
    // */
    // @Test(expected = AssertionError.class)
    // public void testAssertEntityWithSnapshotNullId() {
    // // setup
    // final EntityTierOneType entityTierOneType = new EntityTierOneType(TEST, null);
    //
    // // method
    // takeSnapshot();
    // assertEntityWithSnapshot(entityTierOneType);
    // }
    //
    // /**
    // * Test for assertEntityWithSnapshot from {@link AbstractExecomRepositoryAssert} when entity type isn't supported.
    // */
    // @Test(expected = AssertionError.class)
    // public void testAssertEntityWithSnapshotTypeNotSupported() {
    // // setup
    // final TierOneType expected = new TierOneType();
    //
    // // method
    // takeSnapshot();
    // assertEntityWithSnapshot(expected);
    // }
    //
    // /**
    // * Test for assertEntityWithSnapshot from {@link AbstractExecomRepositoryAssert} when entity doesn't exist in
    // * snapshot.
    // */
    // @Test(expected = AssertionError.class)
    // public void testAssertEntityWithSnapshotNoEntityInSnapshot() {
    // // setup
    // setList1(new ArrayList<Object>());
    //
    // // method
    // takeSnapshot();
    // assertEntityWithSnapshot(new EntityTierOneType(TEST, 1));
    // }
    //
    // /**
    // * Test for assertEntityWithSnapshot from {@link AbstractExecomRepositoryAssert} when entity exists in snapshot
    // and
    // * is asserted with new changed properties.
    // */
    // @Test
    // public void testAssertEntityWithSnapshotEntityAsserted() {
    // // setup
    // final List<Object> list = new ArrayList<Object>();
    // final EntityTierOneType expected = new EntityTierOneType(TEST, 1);
    // list.add(expected);
    // setList1(list);
    //
    // // method
    // takeSnapshot();
    // expected.setProperty(TEST + TEST);
    // assertEntityWithSnapshot(expected, PropertyFactory.value(EntityTierOneType.PROPERTY, TEST + TEST));
    // }

    /**
     * Test for {@link FabutRepositoryAssert#checkNotExistingInAfterDbState(TreeSet, TreeSet, Map, FabutReportBuilder)}
     * when there are more entities in after snapshot but are asserted.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testCheckNotExistingInAfterDbStateTrue() {
        // setup
        final TreeSet beforeIds = new TreeSet();
        beforeIds.add(new Integer(1));
        beforeIds.add(new Integer(2));
        beforeIds.add(new Integer(3));

        final TreeSet afterIds = new TreeSet();
        afterIds.add(1);
        afterIds.add(3);

        final Map<Object, CopyAssert> beforeEntities = new HashMap<Object, CopyAssert>();

        final CopyAssert copyAssert1 = new CopyAssert(new EntityTierOneType());
        beforeEntities.put(new Integer(1), copyAssert1);

        final CopyAssert copyAssert2 = new CopyAssert(new EntityTierOneType());
        copyAssert2.setAsserted(true);
        beforeEntities.put(new Integer(2), copyAssert2);

        final CopyAssert copyAssert3 = new CopyAssert(new EntityTierOneType());
        beforeEntities.put(new Integer(3), copyAssert3);

        // method
        final boolean assertResult = checkNotExistingInAfterDbState(beforeIds, afterIds, beforeEntities,
                new FabutReportBuilder());

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#checkNotExistingInAfterDbState(TreeSet, TreeSet, Map, FabutReportBuilder)}
     * when one of the elements in before snapshot isn't asserted.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testCheckNotExistingInAfterDbStateFalse() {
        // setup
        final TreeSet beforeIds = new TreeSet();
        beforeIds.add(new Integer(1));
        beforeIds.add(new Integer(2));
        beforeIds.add(new Integer(3));

        final TreeSet afterIds = new TreeSet();
        afterIds.add(1);
        afterIds.add(3);

        final Map<Object, CopyAssert> beforeEntities = new HashMap<Object, CopyAssert>();

        final CopyAssert copyAssert1 = new CopyAssert(new EntityTierOneType());
        beforeEntities.put(new Integer(1), copyAssert1);

        final CopyAssert copyAssert2 = new CopyAssert(new EntityTierOneType());
        beforeEntities.put(new Integer(2), copyAssert2);

        final CopyAssert copyAssert3 = new CopyAssert(new EntityTierOneType());
        beforeEntities.put(new Integer(3), copyAssert3);

        // method
        final boolean assertResult = checkNotExistingInAfterDbState(beforeIds, afterIds, beforeEntities,
                new FabutReportBuilder());

        // assert
        assertFalse(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#checkAddedToAfterDbState(TreeSet, TreeSet, Map, FabutReportBuilder)} when
     * there new element in after snapshot.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testCheckAddedToAfterDbStateFalse() {
        // setup
        final TreeSet beforeIds = new TreeSet();
        beforeIds.add(new Integer(1));
        beforeIds.add(new Integer(2));

        final TreeSet afterIds = new TreeSet();
        afterIds.add(1);
        afterIds.add(3);

        final Map<Object, Object> afterEntities = new HashMap<Object, Object>();
        afterEntities.put(new Integer(1), new EntityTierOneType());
        afterEntities.put(new Integer(3), new EntityTierOneType());

        // method
        final boolean assertResult = checkAddedToAfterDbState(beforeIds, afterIds, afterEntities,
                new FabutReportBuilder());

        // assert
        assertFalse(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#checkAddedToAfterDbState(TreeSet, TreeSet, Map, FabutReportBuilder)} when
     * there new element in after snapshot.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testCheckAddedToAfterDbStateTrue() {
        // setup
        final TreeSet beforeIds = new TreeSet();
        beforeIds.add(new Integer(1));
        beforeIds.add(new Integer(3));

        final TreeSet afterIds = new TreeSet();
        afterIds.add(1);
        afterIds.add(3);

        final Map<Object, Object> afterEntities = new HashMap<Object, Object>();
        afterEntities.put(new Integer(1), new EntityTierOneType());
        afterEntities.put(new Integer(3), new EntityTierOneType());

        // method
        final boolean assertResult = checkAddedToAfterDbState(beforeIds, afterIds, afterEntities,
                new FabutReportBuilder());

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for
     * {@link FabutRepositoryAssert#assertDbSnapshotWithAfterState(TreeSet, TreeSet, Map, Map, FabutReportBuilder)} when
     * snapshots don't match.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testAssertDbSnapshotWithAfterStateTrue() {
        // setup
        final TreeSet beforeIds = new TreeSet();
        beforeIds.add(new Integer(1));
        beforeIds.add(new Integer(3));

        final TreeSet afterIds = new TreeSet();
        afterIds.add(1);
        afterIds.add(3);

        final Map<Object, Object> afterEntities = new HashMap<Object, Object>();
        afterEntities.put(new Integer(1), new EntityTierOneType());
        afterEntities.put(new Integer(3), new EntityTierOneType(TEST, new Integer(3)));

        final Map<Object, CopyAssert> beforeEntities = new HashMap<Object, CopyAssert>();

        final CopyAssert copyAssert1 = new CopyAssert(new EntityTierOneType(TEST, new Integer(1)));
        copyAssert1.setAsserted(true);
        beforeEntities.put(new Integer(1), copyAssert1);

        final CopyAssert copyAssert2 = new CopyAssert(new EntityTierOneType());
        copyAssert2.setAsserted(true);
        beforeEntities.put(new Integer(2), copyAssert2);

        final CopyAssert copyAssert3 = new CopyAssert(new EntityTierOneType(TEST, new Integer(3)));
        beforeEntities.put(new Integer(3), copyAssert3);

        // method
        final boolean assertResult = assertDbSnapshotWithAfterState(beforeIds, afterIds, beforeEntities, afterEntities,
                new FabutReportBuilder());

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for
     * {@link FabutRepositoryAssert#assertDbSnapshotWithAfterState(TreeSet, TreeSet, Map, Map, FabutReportBuilder)} when
     * db snapshot matches after db state.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testAssertDbSnapshotWithAfterStateFalse() {
        // setup
        final TreeSet beforeIds = new TreeSet();
        beforeIds.add(new Integer(1));
        beforeIds.add(new Integer(3));

        final TreeSet afterIds = new TreeSet();
        afterIds.add(1);
        afterIds.add(3);

        final Map<Object, Object> afterEntities = new HashMap<Object, Object>();
        afterEntities.put(new Integer(1), new EntityTierOneType());
        afterEntities.put(new Integer(3), new EntityTierOneType(TEST + TEST, new Integer(3)));

        final Map<Object, CopyAssert> beforeEntities = new HashMap<Object, CopyAssert>();

        final CopyAssert copyAssert1 = new CopyAssert(new EntityTierOneType(TEST, new Integer(1)));
        copyAssert1.setAsserted(true);
        beforeEntities.put(new Integer(1), copyAssert1);

        final CopyAssert copyAssert2 = new CopyAssert(new EntityTierOneType());
        copyAssert2.setAsserted(true);
        beforeEntities.put(new Integer(2), copyAssert2);

        final CopyAssert copyAssert3 = new CopyAssert(new EntityTierOneType(TEST, new Integer(3)));
        beforeEntities.put(new Integer(3), copyAssert3);

        // method
        final boolean assertResult = assertDbSnapshotWithAfterState(beforeIds, afterIds, beforeEntities, afterEntities,
                new FabutReportBuilder());

        // assert
        assertFalse(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#assertPair(String, FabutReportBuilder, AssertPair, List, NodesList)} when
     * entities are not properties and can be asserted.
     */
    @Test
    public void testAssertPairNotPropertyAsserted() {
        // setup
        final AssertPair entityPair = ConversionUtil.createAssertPair(new EntityTierOneType(TEST, new Integer(1)),
                new EntityTierOneType(TEST, new Integer(1)), getTypes());

        // method
        final boolean assertResult = assertPair("", new FabutReportBuilder(), entityPair,
                new LinkedList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#assertPair(String, FabutReportBuilder, AssertPair, List, NodesList)} when
     * entities are not properties and assert fails.
     */
    @Test
    public void testAssertPairNotPropertyAssertFail() {
        // setup
        final AssertPair entityPair = ConversionUtil.createAssertPair(
                new EntityTierOneType(TEST + TEST, new Integer(1)), new EntityTierOneType(TEST, new Integer(1)),
                getTypes());

        // method
        final boolean assertResult = assertPair("", new FabutReportBuilder(), entityPair,
                new LinkedList<ISingleProperty>(), new NodesList());

        // assert
        assertFalse(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#assertPair(String, FabutReportBuilder, AssertPair, List, NodesList)} when
     * entities are properties and can be asserted.
     */
    @Test
    public void testAssertPairPropertyAsserted() {
        // setup
        final AssertPair entityPair = ConversionUtil.createAssertPair(new EntityTierOneType(TEST, new Integer(1)),
                new EntityTierOneType(TEST, new Integer(1)), getTypes());
        entityPair.setProperty(true);

        // method
        final boolean assertResult = assertPair("", new FabutReportBuilder(), entityPair,
                new LinkedList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#assertPair(String, FabutReportBuilder, AssertPair, List, NodesList)} when
     * entities are properties and assert fails.
     */
    @Test
    public void testAssertPairPropertyAssertFail() {
        // setup
        final AssertPair entityPair = ConversionUtil.createAssertPair(
                new EntityTierOneType(TEST + TEST, new Integer(1)), new EntityTierOneType(TEST, new Integer(2)),
                getTypes());
        entityPair.setProperty(true);

        // method
        final boolean assertResult = assertPair("", new FabutReportBuilder(), entityPair,
                new LinkedList<ISingleProperty>(), new NodesList());

        // assert
        assertFalse(assertResult);
    }
}
