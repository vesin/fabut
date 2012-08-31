package eu.execom.testutil.util;

import java.util.LinkedList;
import java.util.List;

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

}
