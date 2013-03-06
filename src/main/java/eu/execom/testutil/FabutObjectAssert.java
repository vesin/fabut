package eu.execom.testutil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;

import eu.execom.testutil.enums.CommentType;
import eu.execom.testutil.enums.NodeCheckType;
import eu.execom.testutil.enums.ObjectType;
import eu.execom.testutil.enums.ReferenceCheckType;
import eu.execom.testutil.graph.NodesList;
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
import eu.execom.testutil.report.AssertReportBuilder;
import eu.execom.testutil.util.ConversionUtil;
import eu.execom.testutil.util.ReflectionUtil;

/**
 * ExeCom test util class. Should be used for asserting two object or to assert single object. TODO think of better
 * comment.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings({"rawtypes"})
public class FabutObjectAssert extends Assert {

    protected static final String EMPTY_STRING = "";
    private static final String DOT = ".";
    private static final boolean ASSERTED = true;
    private static final boolean ASSERT_FAIL = false;

    protected Map<ObjectType, List<Class<?>>> types;

    private final List<SnapshotPair> parameterSnapshot;

    /** Instance of the JUnit test that is currently running. */
    private Object testInstance;

    /**
     * Instantiates a new abstract execom entity assert.
     */
    public FabutObjectAssert() {
        super();
        types = new EnumMap<ObjectType, List<Class<?>>>(ObjectType.class);
        parameterSnapshot = new ArrayList<SnapshotPair>();
    }

    /**
     * TODO comments, check for tests Assert objects.
     * 
     * @param report
     *            the report
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @param expectedChangedProperties
     *            the expected changed properties
     * @return true, if successful
     */
    public boolean assertObjects(final AssertReportBuilder report, final Object expected, final Object actual,
            final List<ISingleProperty> expectedChangedProperties) {

        final AssertPair assertPair = ConversionUtil.createAssertPair(expected, actual, types);
        final boolean assertResult = assertPair(EMPTY_STRING, report, assertPair, expectedChangedProperties,
                new NodesList());

        if (assertResult == ASSERTED) {
            afterAssertObject(actual, false);
        }

        return assertResult;

    }

    /**
     * TODO comments, check for tests
     * 
     * @param report
     * @param actual
     * @param properties
     * @return
     */
    public boolean assertObjectWithProperties(final AssertReportBuilder report, final Object actual,
            final List<ISingleProperty> properties) {

        if (actual == null) {
            report.addNullReferenceAssertComment();
            return false;
        }

        final List<Method> methods = ReflectionUtil.getGetMethods(actual, types);
        boolean result = true;
        for (final Method method : methods) {

            final String fieldName = ReflectionUtil.getFieldName(method);
            final ISingleProperty property = getPropertyFromList(fieldName, properties);

            if (property == null) {
                // there is no matching property for field
                report.addNoPropertyForFieldComment(fieldName, method, actual);
                result = false;
            } else {
                try {
                    result &= assertProperty(fieldName, report, property, method.invoke(actual), properties);
                } catch (final Exception e) {
                    report.reportUninvokableMethod(method, actual);
                    result = false;
                }
            }
        }

        if (result) {
            afterAssertObject(actual, false);
        }

        return result;
    }

    /**
     * TODO rewrite This functionality should be reworked and used after initial refactoring is done. Takes current
     * parameters snapshot and original parameters, and saves them.
     * 
     * @param parameters
     *            array of parameters
     */
    protected void takeSnapshot(final Object... parameters) {
        initParametersSnapshot();

        for (final Object object : parameters) {

            final SnapshotPair snapshotPair = new SnapshotPair(object, ReflectionUtil.createCopy(object, types));
            parameterSnapshot.add(snapshotPair);
        }
    }

    /**
     * Asserts two primitive types, if assert fails method must throw {@link AssertionError}.
     * 
     * @param expected
     *            expected object
     * @param actual
     *            actual object
     */
    protected void customAssertEquals(final Object expected, final Object actual) {
        // TODO this method should get custom implementation by calling it from test instance via reflection.
    }

    /**
     * Handles asserting object by category of its type. Logs assertion result in report and returns it.
     * 
     * @param propertyName
     *            name of current property
     * @param report
     *            assert report builder
     * @param pair
     *            the pair
     * @param properties
     *            list of excluded properties
     * @param nodesList
     *            list of object that had been asserted
     * @return <code>true</code> if actual object is asserted to expected object, <code>false</code> otherwise.
     */
    boolean assertPair(final String propertyName, final AssertReportBuilder report, final AssertPair pair,
            final List<ISingleProperty> properties, final NodesList nodesList) {

        final ReferenceCheckType referenceCheckType = checkByReference(report, pair, propertyName);
        if (referenceCheckType != ReferenceCheckType.COMPLEX_ASSERT) {
            return referenceCheckType.getAssertResult();
        }

        // check if any of the expected/actual object is recurring in nodes list
        final NodeCheckType nodeCheckType = nodesList.nodeCheck(pair);
        if (nodeCheckType != NodeCheckType.NEW_PAIR) {
            report.reportPointsTo(propertyName, pair.getActual(), nodeCheckType.getAssertValue());
            return nodeCheckType.getAssertValue();
        }
        nodesList.addPair(pair);

        switch (pair.getObjectType()) {
        case IGNORED_TYPE:
            report.reportIgnoredType(pair);
            return true;
        case COMPLEX_TYPE:
            return assertSubfields(report, pair, properties, nodesList);
        case ENTITY_TYPE:
            throw new IllegalStateException("Entities are NOT supported in this type of assert");
        case PRIMITIVE_TYPE:
            return assertPrimitives(report, propertyName, pair.getExpected(), pair.getActual());
        case LIST_TYPE:
            return assertList(propertyName, report, (List) pair.getExpected(), (List) pair.getActual(), properties,
                    nodesList, true);
        default:
            throw new IllegalStateException("Unknown assert type: " + pair.getObjectType());
        }
    }

    /**
     * Assert subfields.
     * 
     * TODO add TESTS!! and proper comment
     * 
     * @param report
     *            the report
     * @param pair
     *            the pair
     * @param properties
     *            the properties
     * @param nodesList
     *            the nodes list
     * @return true, if successful
     */
    boolean assertSubfields(final AssertReportBuilder report, final AssertPair pair,
            final List<ISingleProperty> properties, final NodesList nodesList) {

        report.increaseDepth();

        boolean t = true;
        final List<Method> getMethods = ReflectionUtil.getGetMethods(pair.getExpected(), types);

        for (final Method expectedMethod : getMethods) {
            try {

                final String fieldName = ReflectionUtil.getFieldName(expectedMethod);

                final ISingleProperty property = obtainProperty(expectedMethod.invoke(pair.getExpected()), fieldName,
                        properties);

                final Method actualMethod = ReflectionUtil.getGetMethod(expectedMethod.getName(), pair.getActual());

                t &= assertProperty(fieldName, report, property, actualMethod.invoke(pair.getActual()), fieldName,
                        properties, nodesList, true);

            } catch (final Exception e) {
                report.reportUninvokableMethod(expectedMethod, pair);
                t = false;
            }
        }

        report.decreaseDepth();
        return t;
    }

    /**
     * Handles asserting actual object by the specified expected property. Logs the result in the report and returns it.
     * 
     * @param propertyName
     *            name of the current property
     * @param report
     *            assert report builder
     * @param expected
     *            property containing expected information
     * @param actual
     *            actual object
     * @param fieldName
     *            name of the field in parent actual object
     * @param properties
     *            list of properties that exclude fields from expected object
     * @param nodesList
     *            list of object that had been asserted
     * @param isProperty
     *            is actual property, important for entities
     * @return - <code>true</code> if object is asserted with expected property, <code>false</code> otherwise.
     */
    boolean assertProperty(final String propertyName, final AssertReportBuilder report, final ISingleProperty expected,
            final Object actual, final String fieldName, final List<ISingleProperty> properties,
            final NodesList nodesList, final boolean isProperty) {

        removeParentQualification(fieldName, properties);

        // expected any not null value
        if (expected instanceof NotNullProperty) {
            final boolean ok = actual != null ? true : false;
            report.reportNotNullProperty(propertyName, ok);
            return ok;
        }

        // expected null value
        if (expected instanceof NullProperty) {
            final boolean ok = actual == null ? true : false;
            report.reportNullProperty(propertyName, ok);
            return ok;
        }

        // any value
        if (expected instanceof IgnoredProperty) {
            report.reportIgnoreProperty(propertyName);
            return true;
        }

        // assert by type
        if (expected instanceof Property) {

            final Object expectedValue = ((Property) expected).geValue();
            final AssertPair assertPair = ConversionUtil.createAssertPair(expectedValue, actual, types, isProperty);
            return assertPair(propertyName, report, assertPair, properties, nodesList);
        }

        throw new IllegalStateException();
    }

    boolean assertProperty(final String propertyName, final AssertReportBuilder report, final ISingleProperty expected,
            final Object actual, final List<ISingleProperty> properties) {

        return assertProperty(propertyName, report, expected, actual, EMPTY_STRING, properties, new NodesList(), true);

    }

    /**
     * Asserts two primitives using abstract method assertEqualsObjects, reports result and returns it. Primitives are
     * any class not marked as complex type, entity type or ignored type.
     * 
     * @param report
     *            assert report builder
     * @param propertyName
     *            name of the current property
     * @param expected
     *            expected object
     * @param actual
     *            actual object
     * @return - <code>true</code> if and only if objects are asserted, <code>false</code> if method assertEqualsObjects
     *         throws {@link AssertionError}.
     */
    boolean assertPrimitives(final AssertReportBuilder report, final String propertyName, final Object expected,
            final Object actual) {

        try {
            customAssertEquals(expected, actual);
            report.addComment(propertyName, expected, actual, CommentType.SUCCESS);
            return true;
        } catch (final AssertionError e) {
            report.addComment(propertyName, expected, actual, CommentType.FAIL);
            return false;
        }
    }

    /**
     * Handles list asserting. It traverses trough the list by list index start from 0 and going up to list size and
     * asserts every two elements on matching index. Lists cannot be asserted if their sizes are different.
     * 
     * @param propertyName
     *            name of current property
     * @param report
     *            assert report builder
     * @param expected
     *            expected list
     * @param actual
     *            actual list
     * @param properties
     *            list of excluded properties
     * @param nodesList
     *            list of object that had been asserted
     * @param isProperty
     *            is it parent object or its member
     * @return - <code>true</code> if every element from expected list with index <em>i</em> is asserted with element
     *         from actual list with index <em>i</em>, <code>false</code> otherwise.
     */
    boolean assertList(final String propertyName, final AssertReportBuilder report, final List expected,
            final List actual, final List<ISingleProperty> properties, final NodesList nodesList,
            final boolean isProperty) {

        // check sizes
        if (expected.size() != actual.size()) {
            report.addListDifferentSizeComment(propertyName, expected.size(), actual.size());
            return false;
        }

        report.increaseDepth();

        // assert every element by index
        boolean assertResult = true;
        for (int i = 0; i < actual.size(); i++) {

            report.reportAssertingListElement(propertyName, i);

            final ISingleProperty property = PropertyFactory.value(EMPTY_STRING, expected.get(i));
            assertResult &= assertProperty(EMPTY_STRING, report, property, actual.get(i), EMPTY_STRING, properties,
                    nodesList, false);

            // TODO use assert pair
            afterAssertObject(actual, isProperty);
        }

        report.decreaseDepth();
        return assertResult;
    }

    /**
     * Cuts off parent property name from start of property path.
     * 
     * @param parentPropertyName
     *            parent name
     * @param properties
     *            list of excluded properties
     * @return List of properties without specified parent property name
     */
    List<ISingleProperty> removeParentQualification(final String parentPropertyName,
            final List<ISingleProperty> properties) {

        final String parentPrefix = parentPropertyName + DOT;
        for (final ISingleProperty property : properties) {
            final String path = StringUtils.removeStart(property.getPath(), parentPrefix);
            property.setPath(path);
        }
        return properties;
    }

    /**
     * Obtains property by following rules: if there is {@link ISingleProperty} in the list of properties matching path
     * with fieldName, it removes it from the list and returns it. Otherwise, it makes new {@link Property} with
     * fieldName as path and value of field.
     * 
     * @param field
     *            expected value for {@link Property}
     * @param propertyPath
     *            path for property
     * @param properties
     *            list of excluded properties
     * @return generated property
     */
    ISingleProperty obtainProperty(final Object field, final String propertyPath, final List<ISingleProperty> properties) {
        final ISingleProperty property = getPropertyFromList(propertyPath, properties);
        if (property != null) {
            return property;
        }
        return PropertyFactory.value(propertyPath, field);
    }

    /**
     * Searches for property with the specified path in the list of properties, removes it from the list and returns it.
     * 
     * @param propertyPath
     *            property path
     * @param properties
     *            list of properties
     * @return {@link ISingleProperty} if there is property with same path as specified in list of properties,
     *         <code>null</code> otherwise
     */
    ISingleProperty getPropertyFromList(final String propertyPath, final List<ISingleProperty> properties) {

        final Iterator<ISingleProperty> iterator = properties.iterator();
        while (iterator.hasNext()) {
            final ISingleProperty property = iterator.next();
            if (property.getPath().equalsIgnoreCase(propertyPath)) {
                iterator.remove();
                return property;
            }
        }
        return null;
    }

    /**
     * For two specified objects checks references and returns appropriate value.
     * 
     * @param report
     *            builder
     * @param expected
     *            object
     * @param actual
     *            object
     * @param propertyName
     *            name of the property
     * @return {@link ReferenceCheckType#EQUAL_REFERENCE} is expected and actual have same reference, if and only if one
     *         of them is null return {@link ReferenceCheckType#EXCLUSIVE_NULL}
     */
    ReferenceCheckType checkByReference(final AssertReportBuilder report, final AssertPair pair,
            final String propertyName) {

        final Object expected = pair.getExpected();
        final Object actual = pair.getActual();

        if (expected == actual) {
            report.addComment(propertyName, expected, actual, CommentType.SUCCESS);
            return ReferenceCheckType.EQUAL_REFERENCE;
        }

        if (expected == null ^ actual == null) {
            report.addComment(propertyName, expected, actual, CommentType.FAIL);
            return ReferenceCheckType.EXCLUSIVE_NULL;
        }
        return ReferenceCheckType.COMPLEX_ASSERT;
    }

    /**
     * Check is object of entity type and if it is mark it as asserted entity, in other case do nothing.
     * 
     * @param object
     *            the object
     * @param isSubproperty
     *            is object subproperty
     */
    boolean afterAssertObject(final Object object, final boolean isSubproperty) {
        return false;
    }

    /**
     * Checks if is same instance.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @return <code>true</code> if expected is same instance as actual, <code>false</code> otherwise.
     */
    // TODO remove this method and instead use checkByReference
    boolean isSameInstance(final Object expected, final Object actual) {
        return expected == actual;
    }

    /**
     * Extract properties and merge them into an array.
     * 
     * @param properties
     *            array/arrays of properties
     */
    List<ISingleProperty> extractProperties(final IProperty... properties) {
        final ArrayList<ISingleProperty> list = new ArrayList<ISingleProperty>();

        for (final IProperty property : properties) {
            if (property instanceof ISingleProperty) {
                list.add((ISingleProperty) property);
            } else {
                list.addAll(((IMultiProperties) property).getProperties());
            }
        }

        return list;
    }

    /**
     * Asserts current parameters states with snapshot previously taken.
     */
    void assertSnapshot() {

        boolean ok = true;
        final AssertReportBuilder report = new AssertReportBuilder();
        for (final SnapshotPair snapshotPair : parameterSnapshot) {
            ok &= assertObjects(report, snapshotPair.getExpected(), snapshotPair.getActual(),
                    new LinkedList<ISingleProperty>());
        }

        initParametersSnapshot();

        if (!ok) {
            throw new AssertionError(report.getMessage());
        }
    }

    /**
     * Initialize parameters snapshot.
     */
    void initParametersSnapshot() {
        parameterSnapshot.clear();
    }

    public Map<ObjectType, List<Class<?>>> getTypes() {
        return types;
    }

    public void setTypes(final Map<ObjectType, List<Class<?>>> types) {
        this.types = types;
    }

    public List<Class<?>> getComplexTypes() {
        return types.get(ObjectType.COMPLEX_TYPE);
    }

    public List<Class<?>> getEntityTypes() {
        return types.get(ObjectType.ENTITY_TYPE);
    }

    public List<Class<?>> getIgnoredTypes() {
        return types.get(ObjectType.IGNORED_TYPE);
    }

    List<SnapshotPair> getParameterSnapshot() {
        return parameterSnapshot;
    }

    public Object getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(final Object testInstance) {
        this.testInstance = testInstance;
    }

    /**
     * Init list of complex types.
     * 
     * @param complexTypes
     *            list of complex types
     */
    public void setComplexTypes(final List<Class<?>> complexTypes) {
        types.put(ObjectType.COMPLEX_TYPE, complexTypes);
    }

    /**
     * Init list of ignored types.
     * 
     * @param ignoredTypes
     *            list of ignored types
     */
    public void setIgnoredTypes(final List<Class<?>> ignoredTypes) {
        types.put(ObjectType.IGNORED_TYPE, ignoredTypes);
    }

}
