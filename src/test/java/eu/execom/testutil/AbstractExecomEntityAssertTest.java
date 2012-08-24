package eu.execom.testutil;

import eu.execom.testutil.model.A;
import eu.execom.testutil.model.B;
import eu.execom.testutil.model.C;
import eu.execom.testutil.model.DoubleLink;
import eu.execom.testutil.model.EntityTierOneType;
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
import eu.execom.testutil.model.Type;

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class AbstractExecomEntityAssertTest extends AbstractExecomEntityAssert<Type> {

    @Override
    protected <T> void customAssertEquals(final T actual, final T expected) {
        assertEquals(expected, actual);
    }

    @Override
    public void initEntityTypes() {
        getEntityTypes().add(EntityTierTwoType.class);
        getEntityTypes().add(EntityTierOneType.class);
    }

    @Override
    public void initComplexTypes() {
        getComplexTypes().add(A.class);
        getComplexTypes().add(B.class);
        getComplexTypes().add(C.class);
        getComplexTypes().add(TierOneType.class);
        getComplexTypes().add(TierTwoType.class);
        getComplexTypes().add(TierThreeType.class);
        getComplexTypes().add(TierFourType.class);
        getComplexTypes().add(TierFiveType.class);
        getComplexTypes().add(TierSixType.class);
        getComplexTypes().add(NoGetMethodsType.class);
        getComplexTypes().add(IgnoredMethodsType.class);
        getComplexTypes().add(TierTwoTypeWithIgnoreProperty.class);
        getComplexTypes().add(TierTwoTypeWithListProperty.class);
        getComplexTypes().add(TierTwoTypeWithPrimitiveProperty.class);

        getComplexTypes().add(DoubleLink.class);
        getComplexTypes().add(Start.class);
    }

    @Override
    public void initIgnoredTypes() {
        getIgnoredTypes().add(IgnoredType.class);

    }

    @Override
    protected <X> void afterAssertEntity(final X object, final boolean asProperty) {
    }

}
