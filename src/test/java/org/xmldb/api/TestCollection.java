package org.xmldb.api;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

public class TestCollection extends ConfigurableImpl implements Collection {
    private static final String[] EMPTY = new String[0];

    private final String name;

    private boolean closed;

    public TestCollection(String name) {
        this.name = name;
    }

    @Override
    public final String getName() throws XMLDBException {
        return name;
    }

    @Override
    public Service[] getServices() throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public Service getService(String name, String version)
            throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public Collection getParentCollection() throws XMLDBException {
        return null;
    }

    @Override
    public int getChildCollectionCount() throws XMLDBException {
        return 0;
    }

    @Override
    public String[] listChildCollections() throws XMLDBException {
        return EMPTY;
    }

    @Override
    public Collection getChildCollection(String name) throws XMLDBException {
        return null;
    }

    @Override
    public int getResourceCount() throws XMLDBException {
        return 0;
    }

    @Override
    public String[] listResources() throws XMLDBException {
        return EMPTY;
    }

    @Override
    public Resource createResource(String id, String type)
            throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public void removeResource(Resource res) throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public void storeResource(Resource res) throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public Resource getResource(String id) throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public String createId() throws XMLDBException {
        throw new XMLDBException(ErrorCodes.NOT_IMPLEMENTED);
    }

    @Override
    public boolean isOpen() throws XMLDBException {
        return !closed;
    }

    @Override
    public void close() throws XMLDBException {
        closed = true;
    }
}
