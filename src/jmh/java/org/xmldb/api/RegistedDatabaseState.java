package org.xmldb.api;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.xmldb.api.base.XMLDBException;

@State(Scope.Benchmark)
public class RegistedDatabaseState extends DatabaseState{
    TestCollection collection;

    @Override
    @Setup(Level.Trial)
    public void up() throws XMLDBException {
        super.up();
        collection = database.addCollection("testdatabase:testcollection");
    }
}