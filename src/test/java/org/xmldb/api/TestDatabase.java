package org.xmldb.api;

import java.util.HashMap;
import java.util.Map;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public class TestDatabase extends ConfigurableImpl implements Database {
    private static final String[] DETAULT_NAMES = new String[] {
            "testdatabase" };

    private final String[] names;
    private final Map<String, TestCollection> collections;

    public TestDatabase(String... names) {
        if (names.length == 0) {
            this.names = DETAULT_NAMES;
        } else {
            this.names = names;
        }
        collections= new HashMap<>();
    }

    @Override
    public final String getName() throws XMLDBException {
        return names[0];
    }

    @Override
    public final String[] getNames() throws XMLDBException {
        return names;
    }

    public TestCollection addCollection(String collectionName) {
        return collections.computeIfAbsent(collectionName, TestCollection::new);
    }

    @Override
    public Collection getCollection(String uri, String username,
            String password) throws XMLDBException {
        return collections.get(uri);
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
