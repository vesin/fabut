package eu.execom.fabut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import eu.execom.fabut.FabutRepositoryAssert;
import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.graph.NodesList;
import eu.execom.fabut.model.A;
import eu.execom.fabut.model.B;
import eu.execom.fabut.model.C;
import eu.execom.fabut.model.DoubleLink;
import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.model.EntityTierThreeType;
import eu.execom.fabut.model.EntityTierTwoType;
import eu.execom.fabut.model.IgnoredMethodsType;
import eu.execom.fabut.model.IgnoredType;
import eu.execom.fabut.model.NoGetMethodsType;
import eu.execom.fabut.model.Start;
import eu.execom.fabut.model.TierFiveType;
import eu.execom.fabut.model.TierFourType;
import eu.execom.fabut.model.TierOneType;
import eu.execom.fabut.model.TierSixType;
import eu.execom.fabut.model.TierThreeType;
import eu.execom.fabut.model.TierTwoType;
import eu.execom.fabut.model.TierTwoTypeWithIgnoreProperty;
import eu.execom.fabut.model.TierTwoTypeWithListProperty;
import eu.execom.fabut.model.TierTwoTypeWithPrimitiveProperty;
import eu.execom.fabut.model.UnknownEntityType;
import eu.execom.fabut.pair.AssertPair;
import eu.execom.fabut.property.CopyAssert;
import eu.execom.fabut.property.ISingleProperty;
import eu.execom.fabut.property.PropertyFactory;
import eu.execom.fabut.report.FabutReportBuilder;
import eu.execom.fabut.util.ConversionUtil;

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

        getTypes().put(AssertableType.ENTITY_TYPE, entityTypes);
        getTypes().put(AssertableType.COMPLEX_TYPE, complexTypes);
        getTypes().put(AssertableType.IGNORED_TYPE, ignoredTypes);
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
        final boolean assertResult = assertDbState();

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for assertDbState of {@link FabutRepositoryAssert} when before snapshot doesn't match after snapshot.
     */
    @Test
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
        final boolean assertResult = assertDbState();

        // assert
        assertFalse(assertResult);
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
        final boolean assertEnitityAsDeleted = assertEntityAsDeleted(actual);
        final boolean assertDbState = assertDbState();

        // assert
        assertTrue(assertEnitityAsDeleted);
        assertTrue(assertDbState);
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
        final boolean ignoreEntity = ignoreEntity(actual);
        final boolean assertDbState = assertDbState();

        // assert
        assertTrue(ignoreEntity);
        assertTrue(assertDbState);
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
        final boolean afterAssertEntity = afterAssertEntity(actual, false);
        final boolean assertDbState = assertDbState();

        // assert
        assertTrue(afterAssertEntity);
        assertTrue(assertDbState);
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
        final boolean assertResult = assertDbState();

        // assert
        assertTrue(assertResult);
    }

    /**
     * Test for afterAssertEntity of {@link FabutRepositoryAssert} when specified object is entity and it is property of
     * another entity.
     */
    @Test
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
        final boolean assertResult = assertDbState();

        // assert
        assertFalse(assertResult);
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
        final boolean afterAssertEntity = afterAssertEntity(actual, false);
        final boolean assertDbState = assertDbState();

        // assert
        assertTrue(afterAssertEntity);
        assertTrue(assertDbState);
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
        assertTrue(assertDbState());
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
        assertTrue(assertDbState());
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
        assertTrue(assertDbState());
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
        assertTrue(assertDbState());
        assertTrue(assertValue);
    }

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
     * Test for {@link FabutRepositoryAssert#checkNewToAfterDbState(TreeSet, TreeSet, Map, FabutReportBuilder)} when
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
        final boolean assertResult = checkNewToAfterDbState(beforeIds, afterIds, afterEntities,
                new FabutReportBuilder());

        // assert
        assertFalse(assertResult);
    }

    /**
     * Test for {@link FabutRepositoryAssert#checkNewToAfterDbState(TreeSet, TreeSet, Map, FabutReportBuilder)} when
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
        final boolean assertResult = checkNewToAfterDbState(beforeIds, afterIds, afterEntities,
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

    /**
     * Test for {@link FabutRepositoryAssert#assertEntityWithSnapshot(FabutReportBuilder, Object, List)} when specified
     * entity can be asserted.
     */
    @Test
    public void testAssertEntityWithSnapshotTrue() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(new EntityTierOneType(TEST, 1));
        setList1(list1);
        final EntityTierOneType entity = new EntityTierOneType(TEST + TEST, new Integer(1));
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(EntityTierOneType.PROPERTY, TEST + TEST));

        // method
        takeSnapshot();
        final boolean assertEntityWithSnapshot = assertEntityWithSnapshot(new FabutReportBuilder(), entity, properties);

        // assert
        assertTrue(assertEntityWithSnapshot);
        assertTrue(assertDbState());

    }

    /**
     * Test for {@link FabutRepositoryAssert#assertEntityWithSnapshot(FabutReportBuilder, Object, List)} when specified
     * entity cannot be asserted.
     */
    @Test
    public void testAssertEntityWithSnapshotFalse() {
        // setup
        final List<Object> list1 = new ArrayList<Object>();
        setList1(list1);
        final EntityTierOneType entity = new EntityTierOneType(TEST + TEST, new Integer(1));
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(EntityTierOneType.PROPERTY, TEST + TEST));

        // method
        takeSnapshot();
        final boolean assertEntityWithSnapshot = assertEntityWithSnapshot(new FabutReportBuilder(), entity, properties);

        // assert
        assertFalse(assertEntityWithSnapshot);
        assertTrue(assertDbState());

    }
}
