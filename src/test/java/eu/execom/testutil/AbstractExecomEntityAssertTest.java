package eu.execom.testutil;

import java.util.ArrayList;
import java.util.List;

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

    private final List<Class<?>> entityTypes;
    private final List<Class<?>> complexTypes;
    private final List<Class<?>> ignoredTypes;

    public AbstractExecomEntityAssertTest() {
        super();

        entityTypes = new ArrayList<Class<?>>();
        entityTypes.add(EntityTierTwoType.class);
        entityTypes.add(EntityTierOneType.class);

        complexTypes = new ArrayList<Class<?>>();
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

        ignoredTypes = new ArrayList<Class<?>>();
        ignoredTypes.add(IgnoredType.class);

    }

    @Override
    protected <T> void customAssertEquals(final T actual, final T expected) {
        assertEquals(expected, actual);
    }

    @Override
    public List<Class<?>> getEntityTypes() {
        return entityTypes;
    }

    @Override
    public List<Class<?>> getComplexTypes() {

        return complexTypes;
    }

    @Override
    public List<Class<?>> getIgnoredTypes() {

        return ignoredTypes;
    }

    @Override
    protected <X> void afterAssertEntity(final X object, final boolean asProperty) {
    }

}
