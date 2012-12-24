package eu.execom.testutil.property;

import java.util.List;

/**
 * Class that contains a collection of single properties.
 */
public class MultiProperty implements IMultiProperty {

    private final List<ISingleProperty> properties;

    public MultiProperty(final List<ISingleProperty> properies) {
        this.properties = properies;
    }

    @Override
    public List<ISingleProperty> getProperties() {
        return properties;
    }

}
