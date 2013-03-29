package eu.execom.fabut;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;

import eu.execom.fabut.model.A;
import eu.execom.fabut.model.B;
import eu.execom.fabut.model.C;
import eu.execom.fabut.model.DoubleLink;
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

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class AbstractFabutObjectAssertTest extends Assert implements IFabutTest {

    private FabutObjectAssert fabutObjectAssert;

    /**
     * Default constructor.
     */
    public AbstractFabutObjectAssertTest() {
        super();
    }

    @Override
    @Before
    public void fabutBeforeTest() {
        fabutObjectAssert = new FabutObjectAssert(this);
    }

    @Override
    public void fabutAfterTest() {
    }

    @Override
    public List<Class<?>> getComplexTypes() {
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
        return complexTypes;
    }

    @Override
    public List<Class<?>> getIgnoredTypes() {
        final List<Class<?>> ignoredTypes = new LinkedList<Class<?>>();
        ignoredTypes.add(IgnoredType.class);
        return ignoredTypes;
    }

    @Override
    public void customAssertEquals(final Object expected, final Object actual) {
        Assert.assertEquals(expected, actual);

    }

    public FabutObjectAssert getFabutObjectAssert() {
        return fabutObjectAssert;
    }

}
