package jmh.java.benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Main class to run specific JMH benchmarks
 */
public class JMHRunner {

    public static void main(String[] args) throws RunnerException {
        // Configure which benchmarks to run
        Options opt = new OptionsBuilder()
                .include(SelectVsSortBenchmark.class.getSimpleName())
                .include(SortingAlgorithmsBenchmark.class.getSimpleName())
                .include(ClosestPairBenchmark.class.getSimpleName())
                .exclude(".*BruteForce.*") // Exclude slow brute force benchmarks by default
                .warmupIterations(2)
                .measurementIterations(3)
                .forks(1)
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }

    /**
     * Run only selection vs sort benchmarks
     */
    public static void runSelectVsSort() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SelectVsSortBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(5)
                .forks(2)
                .build();

        new Runner(opt).run();
    }

    /**
     * Run only sorting algorithm benchmarks
     */
    public static void runSortingBenchmarks() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SortingAlgorithmsBenchmark.class.getSimpleName())
                .exclude(".*DepthAnalysis.*") // Exclude depth analysis if needed
                .build();

        new Runner(opt).run();
    }
}