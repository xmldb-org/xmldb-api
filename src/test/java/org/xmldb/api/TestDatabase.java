package org.xmldb.api;

import java.util.HashMap;
import java.util.Map;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public final class TestDatabase implements Database {
    private static final String[] DETAULT_NAMES = new String[] { "testdatabase" };

    private final String[] names;
    private final Map<String,String> properties;

    public TestDatabase(String... names) {
        if (names.length == 0) {
            this.names = DETAULT_NAMES;
        } else {
            this.names = names;
        }
        properties = new HashMap<>();
    }

    @Override
    public String getName() throws XMLDBException {
        return names[0];
    }

    @Override
    public String[] getNames() throws XMLDBException {
        return names;
    }

    @Override
    public String getProperty(String name) throws XMLDBException {
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value) throws XMLDBException {
        properties.put(name, value);
    }

    @Override
    public Collection getCollection(String uri, String username,
            String password) throws XMLDBException {
        return null;
    }

    @Override
    public boolean acceptsURI(String uri) throws XMLDBException {
        return false;
    }

    @Override
    public String getConformanceLevel() throws XMLDBException {
        return "0";
    }
}
