package eu.execom.testutil.property;

import java.util.List;

/**
 * Class that contains a collection of single properties.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class MultiProperty implements IMultiProperty {

    private final List<ISingleProperty> properties;

    public MultiProperty(final List<ISingleProperty> properies) {
        properties = properies;
    }

    @Override
    public List<ISingleProperty> getProperties() {
        return properties;
    }

}