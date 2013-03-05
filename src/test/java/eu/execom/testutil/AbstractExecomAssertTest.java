package eu.execom.testutil;

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class AbstractExecomAssertTest extends FabutObjectAssert {

    /**
     * Default constructor.
     */
    public AbstractExecomAssertTest() {
        super();
    }

    // @Override
    // protected void initIgnoredTypes(final List<Class<?>> ignoredTypes) {
    // ignoredTypes.add(IgnoredType.class);
    // }

    // @Override
    // protected void initComplexTypes(final List<Class<?>> complexTypes) {
    //
    // complexTypes.add(A.class);
    // complexTypes.add(B.class);
    // complexTypes.add(C.class);
    // complexTypes.add(TierOneType.class);
    // complexTypes.add(TierTwoType.class);
    // complexTypes.add(TierThreeType.class);
    // complexTypes.add(TierFourType.class);
    // complexTypes.add(TierFiveType.class);
    // complexTypes.add(TierSixType.class);
    // complexTypes.add(NoGetMethodsType.class);
    // complexTypes.add(IgnoredMethodsType.class);
    // complexTypes.add(TierTwoTypeWithIgnoreProperty.class);
    // complexTypes.add(TierTwoTypeWithListProperty.class);
    // complexTypes.add(TierTwoTypeWithPrimitiveProperty.class);
    //
    // complexTypes.add(DoubleLink.class);
    // complexTypes.add(Start.class);
    //
    // }

    // @Override
    // protected void initEntityList(final List<Class<?>> entityTypes) {
    // entityTypes.add(EntityTierTwoType.class);
    // entityTypes.add(EntityTierOneType.class);
    // }

    @Override
    protected void customAssertEquals(final Object actual, final Object expected) {
        assertEquals(expected, actual);
    }

    @Override
    public void afterAssertEntity(final Object object, final boolean asProperty) {
    }

}
