package eu.execom.testutil.property;

import java.util.List;

/**
 * Interface that represents a collection of single properties.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public interface IMultiProperty extends IProperty {

    /**
     * Get contained properties.
     * 
     * @return collection of properties.
     */
    List<ISingleProperty> getProperties();

}