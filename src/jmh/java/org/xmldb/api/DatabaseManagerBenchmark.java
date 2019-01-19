package org.xmldb.api;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.xmldb.api.base.XMLDBException;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class DatabaseManagerBenchmark {

    @Benchmark
    public void registerDatabase() throws XMLDBException {
        DatabaseManager.registerDatabase(new TestDatabase());
    }

}
