package jmh.java.benchmarks;

import algorithms.DeterministicSelect;
import algorithms.MergeSort;
import algorithms.QuickSort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import util.ArrayGenerator;
import util.Point;
import algorithms.ClosestPair;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark comparing DeterministicSelect vs Sorting-based selection
 * Measures the practical difference between O(n) and O(n log n) approaches
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SelectVsSortBenchmark {

    @Param({"100", "1000", "10000", "50000", "100000"})
    private int arraySize;

    private int[] randomArray;
    private int k; // Target position for selection

    @Setup
    public void setup() {
        randomArray = ArrayGenerator.generateRandom(arraySize);
        k = arraySize / 2; // Always select median for fair comparison
    }

    /**
     * Benchmark: Deterministic Select (Median-of-Medians)
     * Theoretical: O(n)
     */
    @Benchmark
    public void deterministicSelect(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        // Create a new instance each time to avoid state contamination
        DeterministicSelect select = new DeterministicSelect(new util.AlgorithmMetrics());
        int result = select.select(arrayCopy, k);
        blackhole.consume(result); // Prevent dead code elimination
    }

    /**
     * Benchmark: Sort then select using MergeSort
     * Theoretical: O(n log n)
     */
    @Benchmark
    public void sortThenSelectMergeSort(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        new MergeSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        int result = arrayCopy[k];
        blackhole.consume(result);
    }

    /**
     * Benchmark: Sort then select using QuickSort
     * Theoretical: O(n log n) average
     */
    @Benchmark
    public void sortThenSelectQuickSort(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        new QuickSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        int result = arrayCopy[k];
        blackhole.consume(result);
    }

    /**
     * Benchmark: Arrays.sort() then select
     * Baseline: Highly optimized dual-pivot QuickSort
     */
    @Benchmark
    public void arraysSortThenSelect(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        Arrays.sort(arrayCopy);
        int result = arrayCopy[k];
        blackhole.consume(result);
    }

    /**
     * Benchmark: Partial sort using Arrays.sort(range)
     * More realistic: Only sort what's necessary
     */
    @Benchmark
    public void partialSortThenSelect(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();

        // Only sort the portion that contains the k-th element
        // This simulates a more intelligent sorting approach
        if (k < arrayCopy.length - k - 1) {
            Arrays.sort(arrayCopy, 0, k + 1);
            // Ensure the k-th element is the largest in the sorted portion
            for (int i = k + 1; i < arrayCopy.length; i++) {
                if (arrayCopy[i] < arrayCopy[k]) {
                    int temp = arrayCopy[i];
                    arrayCopy[i] = arrayCopy[k];
                    arrayCopy[k] = temp;
                    // Re-sort if needed (simplified approach)
                    Arrays.sort(arrayCopy, 0, k + 1);
                }
            }
        } else {
            Arrays.sort(arrayCopy, k, arrayCopy.length);
            // Similar adjustment for the other side
            for (int i = 0; i < k; i++) {
                if (arrayCopy[i] > arrayCopy[k]) {
                    int temp = arrayCopy[i];
                    arrayCopy[i] = arrayCopy[k];
                    arrayCopy[k] = temp;
                    Arrays.sort(arrayCopy, k, arrayCopy.length);
                }
            }
        }

        int result = arrayCopy[k];
        blackhole.consume(result);
    }
}