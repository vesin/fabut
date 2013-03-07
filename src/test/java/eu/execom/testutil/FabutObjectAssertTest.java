package eu.execom.testutil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eu.execom.testutil.enums.AssertableType;
import eu.execom.testutil.enums.ReferenceCheckType;
import eu.execom.testutil.graph.NodesList;
import eu.execom.testutil.model.A;
import eu.execom.testutil.model.B;
import eu.execom.testutil.model.C;
import eu.execom.testutil.model.DoubleLink;
import eu.execom.testutil.model.EntityTierOneType;
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
import eu.execom.testutil.model.UnknownType;
import eu.execom.testutil.pair.AssertPair;
import eu.execom.testutil.pair.SnapshotPair;
import eu.execom.testutil.property.IMultiProperties;
import eu.execom.testutil.property.IProperty;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.property.IgnoredProperty;
import eu.execom.testutil.property.NotNullProperty;
import eu.execom.testutil.property.NullProperty;
import eu.execom.testutil.property.Property;
import eu.execom.testutil.property.PropertyFactory;
import eu.execom.testutil.report.FabutReportBuilder;

/**
 * Tests methods from {@link FabutObjectAssert}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings("rawtypes")
public class FabutObjectAssertTest extends AbstractExecomAssertTest {
    private static final String EMPTY_STRING = "";
    private static final String TEST = "test";
    private static final String DOT = ".";

    @Before
    public void before() {

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

        final Map<AssertableType, List<Class<?>>> types = new EnumMap<AssertableType, List<Class<?>>>(AssertableType.class);
        types.put(AssertableType.COMPLEX_TYPE, complexTypes);
        types.put(AssertableType.IGNORED_TYPE, ignoredTypes);
        types.put(AssertableType.ENTITY_TYPE, new LinkedList<Class<?>>());
        setTypes(types);
    }

    /**
     * Test for assertObject if it ignores types added to ignore list.
     */
    @Test
    public void testAssertObjectIgnoreType() {
        // setup
        final IgnoredType ignoredType = new IgnoredType();
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();

        // method
        assertObjectWithProperties(new FabutReportBuilder(), ignoredType, properties);
    }

    /**
     * Test if assertObject only recognizes getMethods for getting properties.
     */
    @Test
    public void testAssertObjectNoGetMethodsType() {
        // setup
        final NoGetMethodsType noGetMethodsType = new NoGetMethodsType(TEST);
        final Property<String> jokerProperty = PropertyFactory.value(NoGetMethodsType.PROPERTY, TEST + TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(jokerProperty);

        // method
        assertObjectWithProperties(new FabutReportBuilder(), noGetMethodsType, properties);
    }

    /**
     * Test if assertObject throws {@link AssertionError} when there is no property associated to a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectNoProperty() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test if assertObject throws {@link AssertionError} when type has null value property and we assert it with
     * {@link NotNullProperty} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectNotNullPropertyActuallyNull() {
        // setup
        final TierOneType tierOneType = new TierOneType(null);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.notNull(TierOneType.PROPERTY));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObject with {@link NotNullProperty} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectNotNullProperty() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.notNull(TierOneType.PROPERTY));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

    }

    /**
     * Test if assertObject throws {@link AssertionError} when type has not null value property and we assert it with
     * {@link NullProperty} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectNullPropertyActuallyNotNull() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.nulll(TierOneType.PROPERTY));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObject with {@link NullProperty} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectNullProperty() {
        // setup
        final TierOneType tierOneType = new TierOneType(null);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.nulll(TierOneType.PROPERTY));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);
    }

    /**
     * Test for assertObject with {@link IgnoredProperty} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectIgnoreProperty() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.ignored(TierOneType.PROPERTY));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);
    }

    /**
     * Test for asssertObject with {@link Property} when expected value is null and actual value is null with a
     * {@link TierOneType}.
     */
    @Test
    public void testAssertObjectChangedPropertyExpectedNullActualNull() {
        // setup
        final TierOneType tierOneType = new TierOneType(null);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, null));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);
    }

    /**
     * Test if assertObject throws {@link AssertionError} when expected value is not null and actual value is null and
     * we assert it with {@link Property} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectChangedPropertyActualNull() {
        // setup
        final TierOneType tierOneType = new TierOneType(null);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, TEST));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test if assertObject throws {@link AssertionError} when expected value is null and actual value is not null and
     * we assert it with {@link Property} with a {@link TierOneType}.
     */
    @Test
    public void testAssertObjectChangedPropertyExpectedNull() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, null));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObject with {@link Property} when expected value is equal to actual value with a
     * {@link TierOneType}.
     */
    @Test
    public void testAssertObjectChangedPropertyEqual() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, TEST));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);
    }

    /**
     * Test for assertObject with {@link Property} when expected value is not equal to actual value with a
     * {@link TierOneType}.
     */
    @Test
    public void testAssertObjectChangedPropertyNotEqual() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, TEST + TEST));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierOneType, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObject when ignored type is property of complex object.
     */
    @Test
    public void testAssertObjectChangedPropertyWithIgnoredType() {
        // setup
        final TierTwoTypeWithIgnoreProperty tierTwoTypeWithIgnoreProperty = new TierTwoTypeWithIgnoreProperty(
                new IgnoredType());
        final Property<IgnoredType> jokerProperty = PropertyFactory.value(TierTwoTypeWithIgnoreProperty.IGNORED_TYPE,
                new IgnoredType());
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(jokerProperty);

        // method
        assertObjectWithProperties(new FabutReportBuilder(), tierTwoTypeWithIgnoreProperty, properties);
    }

    /**
     * Test if assertObject throws {@link AssertionError} when size of actual list is not equal to expected list with a
     * {@link TierTwoTypeWithListProperty}.
     */
    @Test
    public void testAssertObjectChangedPropertyWithListNotEqualSize() {
        // setup
        final TierTwoTypeWithListProperty tierTwoTypeWithListProperty = new TierTwoTypeWithListProperty(
                new ArrayList<String>());
        final List<String> jokerList = new ArrayList<String>();
        jokerList.add(TEST);

        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierTwoTypeWithListProperty.PROPERTY, jokerList));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierTwoTypeWithListProperty, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObject with {@link Property} when all actual list members are primitive types and are equal to
     * expected list members with a {@link TierTwoTypeWithListProperty}.
     */
    @Test
    public void testAssertObjectChangedPropertyWithListAllPropertiesEqual() {
        // setup
        final List<String> actualList = new ArrayList<String>();
        actualList.add(TEST);

        final List<String> expectedList = new ArrayList<String>();
        expectedList.add(TEST);

        final TierTwoTypeWithListProperty tierTwoTypeWithListProperty = new TierTwoTypeWithListProperty(actualList);

        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierTwoTypeWithListProperty.PROPERTY, expectedList));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierTwoTypeWithListProperty, properties);

        // assert
        assertTrue(ok);
    }

    /**
     * Test for assertObject with {@link Property} when actual list members are primitive types and are not equal to
     * expected list members with a {@link TierTwoTypeWithListProperty}.
     */
    @Test
    public void testAssertObjectChangedPropertyWithListAllPropertiesNotEqual() {
        // setup
        final List<String> actualList = new ArrayList<String>();
        actualList.add(TEST);

        final List<String> expectedList = new ArrayList<String>();
        expectedList.add(TEST + TEST);

        final TierTwoTypeWithListProperty tierTwoTypeWithListProperty = new TierTwoTypeWithListProperty(actualList);

        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierTwoTypeWithListProperty.PROPERTY, expectedList));

        // method
        final boolean ok = assertObjectWithProperties(new FabutReportBuilder(), tierTwoTypeWithListProperty, properties);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObjects for two {@link TierTwoType} objects with equal values.
     */
    @Test
    public void testAssertObjectsTierTwoObjectsWithEqualValues() {
        // setup
        final TierTwoType actual = new TierTwoType(new TierOneType(TEST));
        final TierTwoType expected = new TierTwoType(new TierOneType(TEST));

        // method
        assertObjects(new FabutReportBuilder(), expected, actual, new LinkedList<ISingleProperty>());
    }

    /**
     * Test for assertObjects for two {@link TierTwoType} objects with equal values.
     */
    @Test
    public void testAssertObjectsTierTwoObjectsWithNotEqualValues() {
        // setup
        final TierTwoType actual = new TierTwoType(new TierOneType(TEST));
        final TierTwoType expected = new TierTwoType(new TierOneType(TEST + TEST));

        // method
        final boolean ok = assertObjects(new FabutReportBuilder(), expected, actual, new LinkedList<ISingleProperty>());

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObjects for two {@link List}s of {@link TierOneType} with equal values.
     */
    @Test
    public void testAssertObjectsListOfTierOneObjectsWithEqualValues() {
        // setup
        final List<TierOneType> actual = new ArrayList<TierOneType>();
        actual.add(new TierOneType(TEST));
        actual.add(new TierOneType(TEST + TEST));
        actual.add(new TierOneType(TEST + TEST + TEST));

        final List<TierOneType> expected = new ArrayList<TierOneType>();
        expected.add(new TierOneType(TEST));
        expected.add(new TierOneType(TEST + TEST));
        expected.add(new TierOneType(TEST + TEST + TEST));

        // method
        assertObjects(new FabutReportBuilder(), expected, actual, new LinkedList<ISingleProperty>());
    }

    /**
     * Test for assertObjects for two {@link List}s of {@link TierOneType} with unequal values.
     */
    @Test
    public void testAssertObjectsListOfTierOneObjectsWithNotEqualValues() {
        // setup
        final List<TierOneType> actual = new ArrayList<TierOneType>();
        actual.add(new TierOneType(TEST));
        actual.add(new TierOneType(TEST + TEST));
        actual.add(new TierOneType(TEST + TEST + TEST));

        final List<TierOneType> expected = new ArrayList<TierOneType>();
        expected.add(new TierOneType(TEST + TEST));
        expected.add(new TierOneType(TEST + TEST + TEST));
        expected.add(new TierOneType(TEST + TEST + TEST + TEST));

        // method
        final boolean ok = assertObjects(new FabutReportBuilder(), expected, actual, new LinkedList<ISingleProperty>());

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObjects with two {@link TierTwoTypeWithPrimitiveProperty} with {@link IgnoredProperty}.
     */
    @Test
    public void testAssertObjectsTierTwoTypeWithPrimitivePropertyWithIgnoreProperty() {
        // setup
        final TierTwoTypeWithPrimitiveProperty actual = new TierTwoTypeWithPrimitiveProperty(new TierOneType(TEST),
                TEST);
        final TierTwoTypeWithPrimitiveProperty expected = new TierTwoTypeWithPrimitiveProperty(new TierOneType(TEST
                + TEST), TEST);

        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.ignored(TierTwoType.PROPERTY + DOT + TierOneType.PROPERTY));

        // method
        assertObjects(new FabutReportBuilder(), expected, actual, properties);
    }

    /**
     * Test for assertObjects with expected and actual same instance.
     */
    @Test
    public void testAssertObjectsSameInstances() {
        // setup
        final TierOneType tierOneType = new TierOneType();

        // method
        assertObjects(new FabutReportBuilder(), tierOneType, tierOneType, new LinkedList<ISingleProperty>());
    }

    /**
     * Test for assertObjects with two {@link TierSixType} when complex object depth is six.
     */
    @Test
    public void testAssertObjectsTierSixTypeDepthSix() {
        // setup
        final TierSixType actual = new TierSixType(new TierFiveType(new TierFourType(new TierThreeType(new TierTwoType(
                new TierOneType(TEST))))));
        final TierSixType expected = new TierSixType(new TierFiveType(new TierFourType(new TierThreeType(
                new TierTwoType(new TierOneType(TEST + TEST))))));

        // method
        final boolean ok = assertObjects(new FabutReportBuilder(), expected, actual, new LinkedList<ISingleProperty>());

        // assert
        assertFalse(ok);
    }

    /**
     * Test for assertObjects with {@link TierOneType} when varargs of actual is called.
     */
    @Test
    public void testAssertObjectsVarargsExpected() {
        // setup
        final List<TierOneType> expected = new ArrayList<TierOneType>();
        expected.add(new TierOneType(TEST));
        final TierOneType actual = new TierOneType(TEST);

        // method
        assertObjects(new FabutReportBuilder(), expected, actual, new LinkedList<ISingleProperty>());

    }

    /**
     * Test for assertObjects with {@link TierOneType} when varargs of properties is called.
     */
    @Test
    public void testAssertObjectVarargsProperties() {
        // setup
        final TierOneType actual = new TierOneType(TEST);

        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, TEST));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), actual, properties);
    }

    /**
     * Test for disassembleObject of {@link FabutObjectAssert} when actual is pointing to itself.
     */
    @Test
    public void testDisassembleObjectTrivialGraphEqual() {
        // setup
        final A actual = new A(null);
        final A expected = new A(null);
        final NodesList nodesList = new NodesList();

        // method
        final boolean assertValue = assertSubfields(new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.COMPLEX_TYPE), new ArrayList<ISingleProperty>(), nodesList);

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for disassembleObject of {@link FabutObjectAssert} when actual and expected object nodes are contained in
     * nodes list.
     */
    @Test
    public void testDisassembleObjectNodePairInList() {
        // setup
        final Object actual = new Object();
        final Object expected = new Object();
        final NodesList nodesList = new NodesList();
        nodesList.addPair(expected, actual);

        // method
        final boolean assertValue = assertSubfields(new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.PRIMITIVE_TYPE), new ArrayList<ISingleProperty>(), nodesList);

        // assert
        assertTrue(assertValue);
    }

    @Test
    public void testDisassembleObjectCyclicGraphEqual() {
        // setup
        final NodesList nodesList = new NodesList();

        final A actual = new A(null);
        actual.setB(new B(new C(actual)));

        final A expected = new A(null);
        expected.setB(new B(new C(expected)));

        // method
        final boolean assertValue = assertSubfields(new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.COMPLEX_TYPE), new ArrayList<ISingleProperty>(), nodesList);

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for disassembleObject of {@link FabutObjectAssert} when getting reference to field via {@link Method}'s
     * method invoke and it trows exception.
     */
    @Test
    public void testDisassembleObject() {
        // method
        final boolean t = assertSubfields(new FabutReportBuilder(), new AssertPair(new TierOneType(TEST),
                new UnknownType(), AssertableType.COMPLEX_TYPE), new LinkedList<ISingleProperty>(), new NodesList());

        // assert
        assertFalse(t);
    }

    /**
     * Test for assertChangedProperty of {@link FabutObjectAssert} when actual and expected values are nulls.
     */
    @Test
    public void testAssertChangedPropertyBothNulls() {
        // method
        final boolean assertValue = assertPair(EMPTY_STRING, new FabutReportBuilder(), new AssertPair(null, null,
                AssertableType.PRIMITIVE_TYPE), new ArrayList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertChangedProperty of {@link FabutObjectAssert} when actual is instance of complex class.
     */
    @Test
    public void testAssertChangedPropertyComplexType() {
        // setup
        final TierOneType actual = new TierOneType(TEST);
        final TierOneType expected = new TierOneType(TEST);

        // method
        final boolean assertValue = assertPair(EMPTY_STRING, new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.COMPLEX_TYPE, false), new ArrayList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertChangedProperty of {@link FabutObjectAssert} when actual is ignored type.
     */
    @Test
    public void testAssertChangedPropertyIgnoredType() {
        // setup
        final IgnoredType actual = new IgnoredType();
        final IgnoredType expected = new IgnoredType();

        // method
        final boolean assertValue = assertPair(EMPTY_STRING, new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.IGNORED_TYPE, true), new ArrayList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertChangedProperty of {@link FabutObjectAssert} when actual is type unknown to test util and its
     * considered to be primitive.Actual is not equal to expected.
     */
    @Test
    public void testAssertChangedPropertyPrimitiveTypeTrue() {
        // setup
        final String actual = TEST;
        final String expected = TEST;

        // method
        final boolean assertValue = assertPair(EMPTY_STRING, new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.PRIMITIVE_TYPE, true), new ArrayList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertChangedProperty of {@link FabutObjectAssert} when actual is type unknown to test util and its
     * considered to be primitive. Actual is not equal to expected.
     */
    @Test
    public void testAssertChangedPropertyPrimitiveTypeFalse() {
        // setup
        final String actual = TEST;
        final String expected = TEST + TEST;

        // method
        final boolean assertValue = assertPair(EMPTY_STRING, new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.PRIMITIVE_TYPE, true), new ArrayList<ISingleProperty>(), new NodesList());

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for assertChangedProperty of {@link FabutObjectAssert} when actual list and expected list are equal.
     */
    @Test
    public void testAssertChangedPropertyListTypeEqual() {
        // setup
        final List<String> actual = new ArrayList<String>();
        actual.add(TEST);
        actual.add(TEST);
        final List<String> expected = new ArrayList<String>();
        expected.add(TEST);
        expected.add(TEST);

        // method
        final boolean assertValue = assertPair(EMPTY_STRING, new FabutReportBuilder(), new AssertPair(expected, actual,
                AssertableType.LIST_TYPE, true), new LinkedList<ISingleProperty>(), new NodesList());

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertProperties of {@link FabutObjectAssert} when expected property is {@link NotNullProperty} and
     * actual is not null.
     */
    @Test
    public void testAssertPropertiesNotNullPropertyTrue() {
        // method
        final boolean assertValue = assertProperty(EMPTY_STRING, new FabutReportBuilder(),
                PropertyFactory.notNull(TierOneType.PROPERTY), new TierOneType(TEST), "",
                new ArrayList<ISingleProperty>(), null, true);

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertProperties of {@link FabutObjectAssert} when expected property is {@link NotNullProperty} and
     * actual is null.
     */
    @Test
    public void testAssertPropertiesNotNullPropertyFalse() {
        // method
        final boolean assertValue = assertProperty(EMPTY_STRING, new FabutReportBuilder(),
                PropertyFactory.notNull(TierOneType.PROPERTY), null, "", new ArrayList<ISingleProperty>(), null, true);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for assertProperties of {@link FabutObjectAssert} when expected property is {@link NullProperty} and actual
     * is null.
     */
    @Test
    public void testAssertPropertiesNullPropertyTrue() {
        // method
        final boolean assertValue = assertProperty(EMPTY_STRING, new FabutReportBuilder(),
                PropertyFactory.nulll(TierOneType.PROPERTY), null, "", new ArrayList<ISingleProperty>(), null, true);

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertProperties of {@link FabutObjectAssert} when expected property is {@link NullProperty} and actual
     * is not null.
     */
    @Test
    public void testAssertPropertiesNullPropertyFalse() {
        // method
        final boolean assertValue = assertProperty(EMPTY_STRING, new FabutReportBuilder(),
                PropertyFactory.nulll(TierOneType.PROPERTY), new TierOneType(TEST), "",
                new ArrayList<ISingleProperty>(), null, true);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for assertProperties of {@link FabutObjectAssert} when expected property is {@link IgnoredProperty}.
     */
    @Test
    public void testAssertPropertiesIgnoreProperty() {
        // method
        final boolean assertValue = assertProperty(EMPTY_STRING, new FabutReportBuilder(),
                PropertyFactory.ignored(TierOneType.PROPERTY), new TierOneType(TEST), "",
                new ArrayList<ISingleProperty>(), null, true);

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertList of {@link FabutObjectAssert} when actual list has different size than expected list.
     */
    @Test
    public void testAssertListNotEqualSize() {
        // setup
        final List<String> actual = new ArrayList<String>();
        actual.add(TEST);
        actual.add(TEST);
        final List<String> expected = new ArrayList<String>();
        expected.add(TEST);

        // method
        final boolean assertValue = assertList(EMPTY_STRING, new FabutReportBuilder(), expected, actual,
                new ArrayList<ISingleProperty>(), new NodesList(), false);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for assertList of {@link FabutObjectAssert} when actual list is equal to expected list.
     */
    @Test
    public void testAssertListEqual() {
        // setup
        final List<String> actual = new ArrayList<String>();
        actual.add(TEST);
        actual.add(TEST);
        final List<String> expected = new ArrayList<String>();
        expected.add(TEST);
        expected.add(TEST);

        // method
        final boolean assertValue = assertList(EMPTY_STRING, new FabutReportBuilder(), expected, actual,
                new ArrayList<ISingleProperty>(), new NodesList(), false);

        // assert
        assertTrue(assertValue);
    }

    /**
     * Test for assertList of {@link FabutObjectAssert} when actual list is equal to expected list.
     */
    @Test
    public void testAssertListNotEqual() {
        // setup
        final List<String> actual = new ArrayList<String>();
        actual.add(TEST);
        actual.add(TEST + TEST);
        final List<String> expected = new ArrayList<String>();
        expected.add(TEST);
        expected.add(TEST);

        // method
        final boolean assertValue = assertList(EMPTY_STRING, new FabutReportBuilder(), expected, actual,
                new ArrayList<ISingleProperty>(), new NodesList(), false);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for preAssertObjectWithProperties of {@link FabutObjectAssert} when object properties match specified
     * properties.
     */
    @Test
    public void testPreAssertObjectWithPropertiesEqual() {
        // setup
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, "ninja"));

        // method
        assertObjectWithProperties(new FabutReportBuilder(), new TierOneType("ninja"), properties);

    }

    /**
     * Test for preAssertObjectWithProperties of {@link FabutObjectAssert} when object properties don't match specified
     * properties.
     */
    @Test
    public void testPreAssertObjectWithPropertiesNotEqual() {
        // setup
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();
        properties.add(PropertyFactory.value(TierOneType.PROPERTY, TEST));

        // method
        final boolean assertValue = assertObjectWithProperties(new FabutReportBuilder(), new TierOneType(TEST + TEST),
                properties);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for preAssertObjectWithProperties of {@link FabutObjectAssert} when there is no property for field and field
     * value is null.
     */
    @Test
    public void testPreAssertObjectWithPropertiesMethodReturnsNull() {
        // setup
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        // method
        final boolean assertValue = assertObjectWithProperties(new FabutReportBuilder(), new TierOneType(null),
                properties);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for preAssertObjectWithProperties of {@link FabutObjectAssert} when specified property's path doesn't match
     * object's field path.
     */
    @Test
    public void testPreAssertObjectWithPropertiesBadProperties() {
        // setup
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();
        properties.add(PropertyFactory.value(TEST, TEST));

        // method
        final boolean assertValue = assertObjectWithProperties(new FabutReportBuilder(), new TierOneType(TEST),
                properties);

        // assertTrue
        assertFalse(assertValue);
    }

    /**
     * Test for removeParentQualificationForProperties of {@link FabutObjectAssert}.
     */
    @Test
    public void testRemoveParentQualificationForProperty() {
        // setup
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        properties.add(PropertyFactory.notNull("parent.id"));
        properties.add(PropertyFactory.notNull("parent.name"));
        properties.add(PropertyFactory.notNull("parent.lastname"));

        // method
        final List<ISingleProperty> unqualifiedProperties = removeParentQualification("parent", properties);

        // assert
        assertEquals("id", unqualifiedProperties.get(0).getPath());
        assertEquals("name", unqualifiedProperties.get(1).getPath());
        assertEquals("lastname", unqualifiedProperties.get(2).getPath());
    }

    /**
     * Test for generateNewProperty of {@link FabutObjectAssert} when property for that field already exists.
     */
    @Test
    public void testGeneratePropertyFromListOfExcluded() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();
        properties.add(PropertyFactory.notNull(TierOneType.PROPERTY));
        final int numProperties = properties.size();

        // method
        final ISingleProperty property = obtainProperty(tierOneType, TierOneType.PROPERTY, properties);

        // assert
        assertEquals(TierOneType.PROPERTY, property.getPath());
        assertEquals(numProperties - 1, properties.size());
    }

    /**
     * Test for generateNewProperty of {@link FabutObjectAssert} when specified field needs to be wrapped inside new
     * ChangedProperty.
     */
    @Test
    public void testGeneratePropertyCreateNew() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();
        properties.add(PropertyFactory.notNull(TEST));
        final int numProperties = properties.size();

        // method
        final Property property = (Property) obtainProperty(tierOneType, TierOneType.PROPERTY, properties);

        // assert
        assertEquals(TierOneType.PROPERTY, property.getPath());
        assertEquals(tierOneType, property.getValue());
        assertEquals(numProperties, properties.size());

    }

    /**
     * Test for popProperty of {@link FabutObjectAssert} when specified property's path matches specified path.
     */
    @Test
    public void testPopPropertyEqualPath() {
        // setup
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.nulll(TEST));

        // method
        final ISingleProperty property = getPropertyFromList(TEST, properties);

        // assert
        assertEquals(TEST, property.getPath());
        assertEquals(0, properties.size());
    }

    /**
     * Test for popProperty of {@link FabutObjectAssert} when there are no properties in the list.
     */
    @Test
    public void testPopPropertyNoProperties() {
        // setup
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();

        // method
        final ISingleProperty property = getPropertyFromList(TEST, properties);

        // assert
        assertEquals(null, property);
    }

    /**
     * Test for popProperty of {@link FabutObjectAssert} when specified property's path doesn't match specified path.
     */
    @Test
    public void testPopPropertyNotEqualPath() {
        // setup
        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
        properties.add(PropertyFactory.nulll(TEST));

        // method
        final ISingleProperty property = getPropertyFromList(TEST + TEST, properties);

        // assert
        assertEquals(null, property);
    }

    /**
     * Test for checkForNull of {@link FabutObjectAssert} with two null objects.
     */
    @Test
    public void testCheckForNullsTrueNull() {
        // method
        final ReferenceCheckType assertValue = checkByReference(new FabutReportBuilder(), new AssertPair(null, null,
                AssertableType.COMPLEX_TYPE), TEST);

        // assert
        assertEquals(ReferenceCheckType.EQUAL_REFERENCE, assertValue);
    }

    /**
     * Test for checkForNulls of {@link FabutObjectAssert} with two same references.
     */
    @Test
    public void testCheckForNullsTrueNotNull() {
        // setup
        final TierOneType expected = new TierOneType();

        // method
        final ReferenceCheckType assertValue = checkByReference(new FabutReportBuilder(), new AssertPair(expected,
                expected, AssertableType.COMPLEX_TYPE), TEST);

        // assert
        assertEquals(ReferenceCheckType.EQUAL_REFERENCE, assertValue);
    }

    /**
     * Test for checkForNulls of {@link FabutObjectAssert} with one object null and one not null, both cases.
     */
    @Test
    public void testCheckForNullsFalse() {
        // method
        final ReferenceCheckType assertValue1 = checkByReference(new FabutReportBuilder(), new AssertPair(new Object(),
                null, AssertableType.COMPLEX_TYPE), TEST);
        final ReferenceCheckType assertValue2 = checkByReference(new FabutReportBuilder(), new AssertPair(null,
                new Object(), AssertableType.COMPLEX_TYPE), TEST);

        // assert
        assertEquals(ReferenceCheckType.EXCLUSIVE_NULL, assertValue1);
        assertEquals(ReferenceCheckType.EXCLUSIVE_NULL, assertValue2);
    }

    /**
     * Test for checkForNulls of {@link FabutObjectAssert} with both objects not null.
     */
    @Test
    public void testCheckForNullsNull() {
        // method
        final ReferenceCheckType assertValue = checkByReference(new FabutReportBuilder(), new AssertPair(new Object(),
                new Object(), AssertableType.COMPLEX_TYPE), TEST);

        // assert
        assertEquals(ReferenceCheckType.NOT_NULL_PAIR, assertValue);
    }

    /**
     * Test for extractProperties when parameters are of types {@link ISingleProperty} and {@link IMultiProperties}.
     */
    @Test
    public void testExtractPropertiesMixed() {
        // method
        final List<ISingleProperty> properties = extractProperties(
                PropertyFactory.value(EntityTierOneType.PROPERTY, ""),
                PropertyFactory.notNull(EntityTierOneType.ID, EntityTierOneType.PROPERTY));

        // assert
        assertEquals(3, properties.size());
        assertEquals(EntityTierOneType.PROPERTY, properties.get(0).getPath());
        assertEquals(EntityTierOneType.ID, properties.get(1).getPath());
        assertEquals(EntityTierOneType.PROPERTY, properties.get(2).getPath());
    }

    /**
     * Test for extractProperties when all passed parameters are of type {@link ISingleProperty}.
     */
    @Test
    public void testExtractPropertiesAllISingleProperty() {
        // setup
        final IProperty[] propArray = new ISingleProperty[] {PropertyFactory.value(EntityTierOneType.PROPERTY, ""),
                PropertyFactory.value(EntityTierOneType.ID, 0)};

        // method
        final List<ISingleProperty> properties = extractProperties(propArray);

        // assert
        assertEquals(propArray.length, properties.size());
        for (int i = 0; i < propArray.length; i++) {
            assertEquals(((ISingleProperty) propArray[i]).getPath(), properties.get(i).getPath());
        }
    }

    /**
     * Test for extractProperties when all passed parameters are of type {@link IMultiProperties}.
     */
    @Test
    public void testExtractPropertiesAllIMultiProperty() {
        // setup
        final IMultiProperties notNullMultiProp = PropertyFactory.notNull(EntityTierOneType.PROPERTY,
                EntityTierOneType.ID);
        final IMultiProperties ignoredMultiProp = PropertyFactory.ignored(EntityTierOneType.PROPERTY,
                EntityTierOneType.ID);
        final IProperty[] multiPropArray = new IMultiProperties[] {notNullMultiProp, ignoredMultiProp};

        // method
        final List<ISingleProperty> properties = extractProperties(multiPropArray);

        // assert
        assertEquals(notNullMultiProp.getProperties().size() + ignoredMultiProp.getProperties().size(),
                properties.size());
        assertEquals(notNullMultiProp.getProperties().get(0).getPath(), properties.get(0).getPath());
        assertEquals(notNullMultiProp.getProperties().get(1).getPath(), properties.get(1).getPath());
        assertEquals(ignoredMultiProp.getProperties().get(0).getPath(), properties.get(2).getPath());
        assertEquals(ignoredMultiProp.getProperties().get(1).getPath(), properties.get(3).getPath());
    }

    /**
     * Test for assertObjects with parameters of type {@link IMultiProperties}.
     */
    @Test
    public void testAssertObjectsMultiProperty() {
        // setup
        final TierTwoTypeWithPrimitiveProperty actual = new TierTwoTypeWithPrimitiveProperty(new TierOneType(TEST),
                TEST);
        final TierTwoTypeWithPrimitiveProperty expected = new TierTwoTypeWithPrimitiveProperty(new TierOneType(TEST
                + TEST), TEST + TEST);

        // method
        assertObjects(new FabutReportBuilder(), expected, actual, extractProperties(PropertyFactory.ignored(
                TierTwoType.PROPERTY + DOT + TierOneType.PROPERTY, TierTwoTypeWithPrimitiveProperty.PROPERTY2)));
    }

    /**
     * Test for {@link FabutObjectAssert#takeSnapshot(Object...)}.
     */
    @Test
    public void testTakeSnapshot() {
        // setup
        final TierOneType tierOneType = new TierOneType(TEST);

        // method
        takeSnapshot(tierOneType);

        // assert
        final List<SnapshotPair> parameterSnapshot = getParameterSnapshot();
        assertEquals(parameterSnapshot.size(), 1);

        final List<ISingleProperty> properties = new LinkedList<ISingleProperty>();

        assertObjects(new FabutReportBuilder(), new TierOneType(TEST), parameterSnapshot.get(0).getExpected(),
                properties);
        assertObjects(new FabutReportBuilder(), new TierOneType(TEST), parameterSnapshot.get(0).getActual(), properties);
    }

}
