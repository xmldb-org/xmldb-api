package org.xmldb.api;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.xmldb.api.base.XMLDBException;

@State(Scope.Benchmark)
public class DatabaseState {
  TestDatabase database;

  @Setup(Level.Trial)
  public void up() throws XMLDBException {
    database = new TestDatabase();
    DatabaseManager.registerDatabase(database);
  }

  @TearDown(Level.Trial)
  public void down() throws XMLDBException {
    DatabaseManager.deregisterDatabase(database);
  }
}
