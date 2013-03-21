package eu.execom.fabut.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.execom.fabut.IFabutTest;
import eu.execom.fabut.IRepositoryFabutTest;
import eu.execom.fabut.enums.AssertType;
import eu.execom.fabut.enums.AssertableType;
import eu.execom.fabut.pair.AssertPair;
import eu.execom.fabut.property.Property;

/**
 * Util class for conversions needed by testutil.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public final class ConversionUtil {

    /**
     * Private Conversion util constructor.
     */
    private ConversionUtil() {
        super();
    }

    /**
     * Creates a {@link LinkedList} from specified varargs.
     * 
     * @param excludes
     *            varargs parameters
     * @return list of objects of class X
     * 
     * @param <X>
     *            generic type
     */
    public static <X> List<X> createListFromArray(final X... excludes) {
        final List<X> list = new LinkedList<X>();
        for (final X object : excludes) {
            list.add(object);
        }
        return list;
    }

    // TODO comments, tests
    public static AssertPair createAssertPair(final Object expected, final Object actual,
            final Map<AssertableType, List<Class<?>>> types) {
        final AssertableType objectType = ReflectionUtil.getObjectType(expected, actual, types);
        return new AssertPair(expected, actual, objectType);
    }

    // TODO comments, tests
    public static AssertPair createAssertPair(final Object expected, final Object actual,
            final Map<AssertableType, List<Class<?>>> types, final boolean isProperty) {
        final AssertPair assertPair = createAssertPair(expected, actual, types);
        assertPair.setProperty(isProperty);
        return assertPair;
    }

    // TODO comments, tests
    public static AssertPair createAssertPair(final Property expected, final Object actual,
            final Map<AssertableType, List<Class<?>>> types, final boolean isProperty) {
        final AssertPair assertPair = createAssertPair(expected.getValue(), actual, types);
        assertPair.setProperty(isProperty);
        return assertPair;
    }

    public static AssertType getAssertType(final Object testInstance) {
        if (testInstance instanceof IRepositoryFabutTest) {
            return AssertType.REPOSITORY_ASSERT;
        } else if (testInstance instanceof IFabutTest) {
            return AssertType.OBJECT_ASSERT;
        } else {
            return AssertType.UNSUPPORTED_ASSERT;
        }
    }

}
