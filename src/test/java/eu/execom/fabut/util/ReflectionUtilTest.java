package eu.execom.fabut.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.graph.NodesList;
import eu.execom.fabut.model.A;
import eu.execom.fabut.model.B;
import eu.execom.fabut.model.BooleanFieldType;
import eu.execom.fabut.model.C;
import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.model.EntityTierTwoType;
import eu.execom.fabut.model.NoGetMethodsType;
import eu.execom.fabut.model.TierOneType;
import eu.execom.fabut.model.TierTwoType;
import eu.execom.fabut.model.UnknownType;

/**
 * Tests for {@link ReflectionUtil}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
@SuppressWarnings("unchecked")
public class ReflectionUtilTest extends Assert {

    private static final String TEST = "test";
    private static final String PROPERTY = "property";
    private static final String GET_PROPERTY = "getProperty";
    private static final String IS_PROPERTY = "isProperty";
    private static final String IS_FIELD = "isField";
    private static final String IS_NOT_BOOLEAN_PROPERTY = "isNotBooleanProperty";

    Map<AssertableType, List<Class<?>>> types;

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when method does not starts with "get" prefix and there is
     * matching field in the class.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Before
    public void setup() {
        types = new EnumMap<AssertableType, List<Class<?>>>(AssertableType.class);
        final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
        complexTypes.add(TierOneType.class);
        complexTypes.add(TierTwoType.class);
        complexTypes.add(A.class);
        complexTypes.add(B.class);
        complexTypes.add(C.class);
        types.put(AssertableType.COMPLEX_TYPE, complexTypes);

        final List<Class<?>> entityTypes = new LinkedList<Class<?>>();
        complexTypes.add(EntityTierOneType.class);
        complexTypes.add(EntityTierTwoType.class);
        types.put(AssertableType.ENTITY_TYPE, entityTypes);
    }

    @Test
    public void testIsGetMethodNoGetPrefix() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = NoGetMethodsType.class.getMethod("property");
        // method
        final boolean isGetMethod = ReflectionUtil.isGetMethod(new NoGetMethodsType(TEST).getClass(), method);
        // assert
        assertFalse(isGetMethod);
    }

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when specified method is has no matching field in the class.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testIsGetMethodFakeGetMethod() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = NoGetMethodsType.class.getMethod("getString");
        // method
        final boolean isGetMethod = ReflectionUtil.isGetMethod(new NoGetMethodsType(TEST).getClass(), method);
        // assert
        assertFalse(isGetMethod);
    }

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when method is real get method.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testIsGetMethodGetPrefix() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = TierOneType.class.getMethod(GET_PROPERTY);
        // method
        final boolean isGetMetod = ReflectionUtil.isGetMethod(new TierOneType(TEST).getClass(), method);
        // assert
        assertTrue(isGetMetod);
    }

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when method has no "get" or "is" prefix and has matching field in
     * class.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testIsGetMethodNoPrefix() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = NoGetMethodsType.class.getMethod(PROPERTY);
        // method
        final boolean isGetMethod = ReflectionUtil.isGetMethod(new NoGetMethodsType(TEST).getClass(), method);
        // assert
        assertFalse(isGetMethod);
    }

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when get methods for primitive boolean type has no matching
     * boolean field in class.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testIsGetMethodFakeIsMethod() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = NoGetMethodsType.class.getMethod(IS_NOT_BOOLEAN_PROPERTY);

        // method
        final boolean isGetMethod = ReflectionUtil.isGetMethod(new NoGetMethodsType(TEST).getClass(), method);

        // assert
        assertFalse(isGetMethod);
    }

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when there is no field for specified method so it throws
     * {@link NoSuchFieldException}.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testIsGetMethodFakeMethodNoField() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = NoGetMethodsType.class.getMethod(IS_FIELD);

        // method
        final boolean assertValue = ReflectionUtil.isGetMethod(new NoGetMethodsType(TEST).getClass(), method);

        // assert
        assertFalse(assertValue);
    }

    /**
     * Test for isGetMethod of {@link ReflectionUtil} when get methods is of boolean primitive type.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testIsGetMethodIsMethod() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = BooleanFieldType.class.getMethod(IS_PROPERTY);

        // method
        final boolean isGetMethod = ReflectionUtil.isGetMethod(new BooleanFieldType(true).getClass(), method);

        // assert
        assertTrue(isGetMethod);
    }

    /**
     * Test for getFieldName of {@link ReflectionUtil} when method starts with "get" prefix and there is matching field
     * in class.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testGetFieldNameGetPrefix() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = TierOneType.class.getMethod(GET_PROPERTY);
        // method
        final String fieldName = ReflectionUtil.getFieldName(method);
        // assert
        assertEquals(PROPERTY, fieldName);
    }

    /**
     * Test for getFieldName of {@link ReflectionUtil} when get method is of boolean primitive type.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testGetFieldNameIsPrefix() throws SecurityException, NoSuchMethodException {
        // setup
        final Method method = BooleanFieldType.class.getMethod(IS_PROPERTY);
        // method
        final String fieldName = ReflectionUtil.getFieldName(method);
        // assert
        assertEquals(PROPERTY, fieldName);
    }

    /**
     * Test for findFieldInInheritance of {@link ReflectionUtil} when null is specified as a class.
     */
    @Test
    public void testFindFieldInInheritanceNullClass() {
        // method
        final Field field = ReflectionUtil.findField(null, TEST);

        // assert
        assertNull(field);
    }

    /**
     * Test for findFieldInInheritance of {@link ReflectionUtil} when subclass and name of field from subclass is
     * specified.
     */
    @Test
    public void testFindFieldInInheritanceSubclass() {
        // method
        final Field field = ReflectionUtil.findField(EntityTierOneType.class, EntityTierOneType.PROPERTY);

        // assert
        assertNotNull(field);
        assertEquals(EntityTierOneType.PROPERTY, field.getName());
    }

    /**
     * Test for findFieldInInheritance of {@link ReflectionUtil} when subclass and name of field from superclass is
     * specified.
     */
    @Test
    public void testFindFieldInInheritanceSuperclass() {
        // method
        final Field field = ReflectionUtil.findField(EntityTierOneType.class, EntityTierOneType.ID);

        // assert
        assertNotNull(field);
        assertEquals(EntityTierOneType.ID, field.getName());
    }

    /**
     * Test for isListType of {@link ReflectionUtil} when specified object is instance of {@link Collection} interface.
     */
    @Test
    public void testIsListTypeTrue() {
        // assert
        assertTrue(ReflectionUtil.isListType(new ArrayList<String>()));
    }

    /**
     * Test for isListType of {@link ReflectionUtil} when specified object is not instance of {@link Collection}
     * interface.
     */
    @Test
    public void testIsListTypeFalse() {
        // assert
        assertFalse(ReflectionUtil.isListType(TEST));
    }

    // TODO fix these tests

    // /**
    // * Test foe isEntityType of {@link ReflectionUtil} when specified class is entity type.
    // */
    // @Test
    // public void testIsEntityTypeTrue() {
    // // setup
    // final List<Class<?>> entityTypes = new ArrayList<Class<?>>();
    // entityTypes.add(EntityTierOneType.class);
    //
    // // assert
    // assertTrue(ReflectionUtil.isEntityType(new EntityTierOneType().getClass(), entityTypes));
    // }
    //
    // /**
    // * Test foe isEntityType of {@link ReflectionUtil} when specified class is not entity type.
    // */
    // @Test
    // public void testIsEntityTypeFalse() {
    // // assert
    // assertFalse(ReflectionUtil.isEntityType(new TierOneType().getClass(), new ArrayList<Class<?>>()));
    // }
    //
    // /**
    // * Test for isComplexType of {@link ReflectionUtil} when specified class is complex type.
    // */
    // @Test
    // public void testIsComplexTypeTrue() {
    // // setup
    // final List<Class<?>> complexTypes = new ArrayList<Class<?>>();
    // complexTypes.add(TierOneType.class);
    //
    // // assert
    // assertTrue(ReflectionUtil.isComplexType(new TierOneType().getClass(), complexTypes));
    // }
    //
    // /**
    // * Test for isComplexType of {@link ReflectionUtil} when specified class is not complex type.
    // */
    // @Test
    // public void testIsComplexTypeFalse() {
    // // assert
    // assertFalse(ReflectionUtil.isComplexType(new Object().getClass(), new ArrayList<Class<?>>()));
    // }
    //
    // /**
    // * Test for isIgnoredType of {@link ReflectionUtil} when specified class is ignored type.
    // */
    // @Test
    // public void testIsIgnoredTypeTrue() {
    // // setup
    // final List<Class<?>> ignoredTypes = new ArrayList<Class<?>>();
    // ignoredTypes.add(IgnoredType.class);
    //
    // // assert
    // assertTrue(ReflectionUtil.isIgnoredType(new IgnoredType().getClass(), ignoredTypes));
    // }
    //
    // /**
    // * Test for isIgnoredType of {@link ReflectionUtil} when specified class is not ignored type.
    // */
    // @Test
    // public void testIsIgnoredTypeFalse() {
    // // assert
    // assertFalse(ReflectionUtil.isIgnoredType(new Object().getClass(), new ArrayList<Class<?>>()));
    //
    // }
    //
    // /**
    // * Test for isIgnoredType of {@link ReflectionUtil} when expected and actual are null.
    // */
    // @Test
    // public void testIsIgnoredTypeExpectedActualNull() {
    // // assert
    // assertFalse(ReflectionUtil.isIgnoredType(null, null, new ArrayList<Class<?>>()));
    // }
    //
    // /**
    // * Test of isIgnoredType of {@link ReflectionUtil} when expected is not null and is ignored type.
    // */
    // @Test
    // public void testIsIgnoredTypeExpectedNotNull() {
    // // setup
    // final List<Class<?>> ignoredTypes = new ArrayList<Class<?>>();
    // ignoredTypes.add(IgnoredType.class);
    //
    // // assert
    // assertTrue(ReflectionUtil.isIgnoredType(new IgnoredType().getClass(), ignoredTypes));
    //
    // }
    //
    // /**
    // * Test of isIgnoredType of {@link ReflectionUtil} when actual is not null and is ignored type.
    // */
    // @Test
    // public void testIsIgnoredTypeActualNotNull() {
    // // setup
    // final List<Class<?>> ignoredTypes = new ArrayList<Class<?>>();
    // ignoredTypes.add(IgnoredType.class);
    //
    // // assert
    // assertTrue(ReflectionUtil.isIgnoredType(null, new IgnoredType(), ignoredTypes));
    // }

    /**
     * Test for getIdValue of {@link ReflectionUtil} when specified type is entity.
     */
    @Test
    public void testGetIdValueEntity() {
        // setup
        final EntityTierOneType entityTierOneType = new EntityTierOneType(TEST, 1);

        // method
        final Integer id = (Integer) ReflectionUtil.getIdValue(entityTierOneType);

        // assert
        assertEquals(1, id.intValue());
    }

    /**
     * Test for getIdValue of {@link ReflectionUtil} when specified type is unknown type.
     */
    @Test
    public void testGetIdValueUknownType() {
        // setup
        final UnknownType unknownType = new UnknownType();
        // method
        final Integer id = (Integer) ReflectionUtil.getIdValue(unknownType);

        // assert
        assertNull(id);
    }

    // TODO fix these tests

    // /**
    // * Test for getObjectGetMethods when has one real get method.
    // */
    // @Test
    // public void testGetObjectGetMethodsTierOneType() {
    // // method
    // final List<Method> methods = ReflectionUtil.getObjectGetMethods(new TierOneType(TEST),
    // new ArrayList<Class<?>>(), new ArrayList<Class<?>>());
    // final Method method = methods.get(0);
    // // assert
    // assertEquals(1, methods.size());
    // assertEquals("getProperty", method.getName());
    // }
    //
    // /**
    // * Test for getObjectGetMethods when class has no real get methods.
    // */
    // @Test
    // public void testGetObjectGetMethodsNoGetMethodsType() {
    // // method
    // final List<Method> methods = ReflectionUtil.getObjectGetMethods(new NoGetMethodsType(TEST),
    // new ArrayList<Class<?>>(), new ArrayList<Class<?>>());
    // // assert
    // assertEquals(0, methods.size());
    // }
    //
    // /**
    // * Test for getObjectGetMethods if it impose ordering of get methods for complex or entity types come last in
    // list.
    // *
    // * @throws Exception
    // */
    // @Test
    // public void testGetObjectGetMethodsCheckOrdering() throws Exception {
    // // setup
    // final Method getSubProperty = EntityTierTwoType.class.getMethod("getSubProperty");
    // final List<Class<?>> entityTypes = new ArrayList<Class<?>>();
    // entityTypes.add(EntityTierTwoType.class);
    // entityTypes.add(EntityTierOneType.class);
    //
    // // method
    // final List<Method> methods = ReflectionUtil.getObjectGetMethods(new EntityTierTwoType(TEST, 10,
    // new EntityTierOneType(TEST, 5)), new ArrayList<Class<?>>(), entityTypes);
    //
    // // assert
    // assertEquals(getSubProperty.getName(), methods.get(2).getName());
    // }

    @Test
    public void testGetObjectGetMethodNamed() throws Exception {
        // setup
        final String methodName = "getProperty";
        final TierOneType tierOneType = new TierOneType(PROPERTY);
        final Method expectedGetMethod = tierOneType.getClass().getMethod(methodName);

        // method
        final Method actualGetMethod = ReflectionUtil.getGetMethod(methodName, tierOneType);

        // assert
        assertEquals(expectedGetMethod.getName(), actualGetMethod.getName());
        assertEquals(expectedGetMethod.getParameterTypes().length, actualGetMethod.getParameterTypes().length);
    }

    @Test(expected = NoSuchMethodException.class)
    public void testGetObjectGetMethodNamedNoGetMethod() throws Exception {
        // setup
        final String methodName = "getProperty";
        final NoGetMethodsType noGetMethodsType = new NoGetMethodsType();

        // method
        ReflectionUtil.getGetMethod(methodName, noGetMethodsType);
    }

    /**
     * Test for createEmptyCopyOf of {@link FabutRepositoryAssert} when specified object has default constructor.
     */
    @Test
    public void testCreateEmptyCopyOfHasDefaultConstructor() {
        // method
        final TierOneType assertObject = ReflectionUtil.createEmptyCopyOf(new TierOneType());

        // assert
        assertNotNull(assertObject);
        assertEquals(TierOneType.class, assertObject.getClass());
    }

    /**
     * Test for createEmptyCopyOf of {@link FabutRepositoryAssert} when specified object has no default constructor.
     */
    @Test(expected = AssertionFailedError.class)
    public void testCreateEmptyCopyOfNoDefaultConstructor() {
        // method
        final NoGetMethodsType assertObject = ReflectionUtil.createEmptyCopyOf(new NoGetMethodsType(TEST));

        // assert
        assertNull(assertObject);
    }

    /**
     * Test for copyList of {@link FabutRepositoryAssert} if it copies list.
     */
    @Test
    public void testCopyList() {
        // setup
        final List<String> list = new LinkedList<String>();
        list.add(TEST);

        // method
        final List<String> assertList = ReflectionUtil.copyList(list);

        assertNotNull(assertList);
        assertEquals(1, assertList.size());
        assertEquals(TEST, assertList.get(0));
    }

    /**
     * Test for createCopy of {@link FabutRepositoryAssert} when specified object is null.
     */
    @Test
    public void testCreateCopyNull() {
        // method
        final Object object = ReflectionUtil.createCopy(null, new EnumMap<AssertableType, List<Class<?>>>(
                AssertableType.class));

        // assert
        assertNull(object);
    }

    /**
     * Test for createCopy of {@link FabutRepositoryAssert} when specified object is list.
     */
    @Test
    public void testCreateCopyList() {
        // setup
        final List<String> list = new LinkedList<String>();
        list.add(TEST);

        // method
        final List<String> assertList = (List<String>) ReflectionUtil.createCopy(list,
                new EnumMap<AssertableType, List<Class<?>>>(AssertableType.class));

        assertNotNull(assertList);
        assertEquals(1, assertList.size());
        assertEquals(TEST, assertList.get(0));
    }

    /**
     * Test for ivokeSetMethod of {@link FabutRepositoryAssert} when specified object for copying has set method with
     * specified name and can be invoked.
     */
    @Test
    public void testIvokeSetMethodSuccess() {
        // setup
        final Method method = ReflectionUtil.getGetMethods(new TierOneType(), types).get(0);
        final Class<?> classObject = TierOneType.class;
        final String propertyName = PROPERTY;
        final TierOneType copy = new TierOneType();
        final Object copiedProperty = PROPERTY;

        // method
        ReflectionUtil.invokeSetMethod(method, classObject, propertyName, copy, copiedProperty);

        // assert
        assertEquals(PROPERTY, copy.getProperty());
    }

    /**
     * Test for ivokeSetMethod of {@link FabutRepositoryAssert} when specified object for copying has set method with
     * specified name and can't be invoked as object has no set methods.
     */
    @Test
    public void testIvokeSetMethodNull() {
        // setup
        final Method method = ReflectionUtil.getGetMethods(new TierTwoType(new TierOneType()), types).get(0);
        final Class<?> classObject = TierTwoType.class;
        final String propertyName = PROPERTY;
        final TierTwoType copy = new TierTwoType(new TierOneType());
        final Object copiedProperty = PROPERTY;

        // method
        final boolean ok = ReflectionUtil.invokeSetMethod(method, classObject, propertyName, copy, copiedProperty);

        // assert
        assertFalse(ok);
    }

    /**
     * Test for copyProperty of {@link FabutRepositoryAssert} when specified object for copying is null;
     */
    @Test
    public void testCopyPropertyNull() {
        // method
        final Object copy = ReflectionUtil.copyProperty(null, null, types);

        // assert
        assertNull(copy);
    }

    /**
     * Test for copyProperty of {@link FabutRepositoryAssert} when specified object is complex object with property of
     * complex type.
     */
    @Test
    public void testCopyPropertyComplexType() {
        // method
        final EntityTierTwoType copy = (EntityTierTwoType) ReflectionUtil.copyProperty(new EntityTierTwoType(TEST, 1,
                new EntityTierOneType(PROPERTY, 2)), new NodesList(), types);

        // assert
        assertNotNull(copy);
        assertEquals(TEST, copy.getProperty());
        assertEquals(1, copy.getId());
        assertNotNull(copy.getSubProperty());
        assertEquals(PROPERTY, copy.getSubProperty().getProperty());
        assertEquals(new Integer(2), copy.getSubProperty().getId());
    }

    /**
     * Test for copyProperty of {@link FabutRepositoryAssert} when specified object is {@link List}.
     */
    @Test
    public void testCopyPropertyList() {
        // setup
        final List<String> list = new ArrayList<String>();
        list.add(TEST);

        // method
        final List<String> copy = (List<String>) ReflectionUtil.copyProperty(list, null, types);

        // assert
        assertNotNull(copy);
        assertEquals(1, copy.size());
        assertEquals(TEST, copy.get(0));

    }

    /**
     * Test for copyProperty of {@link FabutRepositoryAssert} when specified object is of unknown type.
     */
    @Test
    public void testCopyPropertyUnkownType() {
        // setup
        final UnknownType unknownType = new UnknownType();

        // method
        final UnknownType copy = (UnknownType) ReflectionUtil.copyProperty(unknownType, null, types);

        // assert
        assertEquals(unknownType, copy);

    }

    /**
     * Test for getPropertyForCopying of {@link FabutRepositoryAssert} when specified method can be invoked.
     */
    @Test
    public void testGetPropertyForCopyingCanInvoke() {
        // setup
        final Method method = ReflectionUtil.getGetMethods(new EntityTierOneType(), types).get(1);

        // method
        final String property = (String) ReflectionUtil.getPropertyForCopying(new EntityTierOneType(TEST, 1), method);

        // assert
        assertEquals(TEST, property);

    }

    /**
     * Test for getPropertyForCopying of {@link FabutRepositoryAssert} when specified method can not be invoked.
     */
    @Test(expected = AssertionFailedError.class)
    public void testGetPropertyForCopyingCantInvoke() {
        // setup
        final Method method = ReflectionUtil.getGetMethods(new EntityTierOneType(), types).get(1);

        // method
        final String property = (String) ReflectionUtil.getPropertyForCopying(null, method);

        // assert
        assertNull(property);
    }

    /**
     * Test for createCopy if it properly handles cyclic object references.
     */
    @Test
    public void testCreateCopyCyclic() {
        // setup
        final A a = new A();
        a.setProperty(PROPERTY);
        a.setB(new B(new C(a)));

        // method
        final A aCopy = (A) ReflectionUtil.createCopy(a, types);

        // assert
        assertEquals(aCopy, aCopy.getB().getC().getA());
        assertEquals(a.getProperty(), aCopy.getProperty());
        assertEquals(a.getB().getC().getA().getProperty(), aCopy.getB().getC().getA().getProperty());
    }

}
