package org.xmldb.api;

import java.util.HashMap;
import java.util.Map;

import org.xmldb.api.base.Configurable;
import org.xmldb.api.base.XMLDBException;

public class ConfigurableImpl implements Configurable {
    private final Map<String, String> properties;

    public ConfigurableImpl() {
        properties = new HashMap<>();
    }

    @Override
    public final String getProperty(String name) throws XMLDBException {
        return properties.get(name);
    }

    @Override
    public final void setProperty(String name, String value)
            throws XMLDBException {
        properties.put(name, value);
    }
}
