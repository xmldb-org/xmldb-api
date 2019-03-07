package org.xmldb.api;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;
import org.xmldb.api.base.XMLDBException;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class DatabaseManagerBenchmark {

  @Benchmark
  public void registerDatabase(RegistedDatabaseState state) throws XMLDBException {
    DatabaseManager.registerDatabase(state.database);
  }

  @Benchmark
  public void getCollection(RegistedDatabaseState state, Blackhole bh) throws XMLDBException {
    bh.consume(DatabaseManager.getCollection("xmldb:testdatabase:testcollection"));
  }

  @Benchmark
  public void deregisterDatabase(RegistedDatabaseState state) throws XMLDBException {
    DatabaseManager.deregisterDatabase(state.database);
  }
}
