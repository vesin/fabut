package eu.execom.testutil.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.execom.testutil.AssertPair;
import eu.execom.testutil.enums.ObjectType;

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
     *            - varargs parameters
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
            final Map<ObjectType, List<Class<?>>> types) {
        final ObjectType objectType = ReflectionUtil.getObjectType(expected, actual, types);
        return new AssertPair(expected, actual, objectType);
    }

}
