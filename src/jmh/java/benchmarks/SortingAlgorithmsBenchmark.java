package jmh.java.benchmarks;

import algorithms.MergeSort;
import algorithms.QuickSort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import util.ArrayGenerator;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for comparing sorting algorithms under different conditions
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SortingAlgorithmsBenchmark {

    @Param({"100", "1000", "10000", "50000", "100000"})
    private int arraySize;

    private int[] randomArray;
    private int[] sortedArray;
    private int[] reverseSortedArray;
    private int[] duplicateArray;

    @Setup
    public void setup() {
        randomArray = ArrayGenerator.generateRandom(arraySize);
        sortedArray = ArrayGenerator.generateSorted(arraySize);
        reverseSortedArray = ArrayGenerator.generateReverseSorted(arraySize);
        duplicateArray = ArrayGenerator.generateDuplicates(arraySize, arraySize / 10);
    }

    // MergeSort Benchmarks
    @Benchmark
    public void mergeSortRandom(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        new MergeSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    @Benchmark
    public void mergeSortSorted(Blackhole blackhole) {
        int[] arrayCopy = sortedArray.clone();
        new MergeSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    @Benchmark
    public void mergeSortReverseSorted(Blackhole blackhole) {
        int[] arrayCopy = reverseSortedArray.clone();
        new MergeSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    // QuickSort Benchmarks
    @Benchmark
    public void quickSortRandom(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        new QuickSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    @Benchmark
    public void quickSortSorted(Blackhole blackhole) {
        int[] arrayCopy = sortedArray.clone();
        new QuickSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    @Benchmark
    public void quickSortReverseSorted(Blackhole blackhole) {
        int[] arrayCopy = reverseSortedArray.clone();
        new QuickSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    @Benchmark
    public void quickSortDuplicates(Blackhole blackhole) {
        int[] arrayCopy = duplicateArray.clone();
        new QuickSort(new util.AlgorithmMetrics()).sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    // Arrays.sort as baseline
    @Benchmark
    public void arraysSortRandom(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        Arrays.sort(arrayCopy);
        blackhole.consume(arrayCopy);
    }

    /**
     * Benchmark recursion depth by measuring with metrics
     */
    @Benchmark
    public void quickSortDepthAnalysis(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        util.AlgorithmMetrics metrics = new util.AlgorithmMetrics();
        new QuickSort(metrics).sort(arrayCopy);

        // Consume both the sorted array and depth metrics
        blackhole.consume(arrayCopy);
        blackhole.consume(metrics.getMaxDepth());
    }

    @Benchmark
    public void mergeSortDepthAnalysis(Blackhole blackhole) {
        int[] arrayCopy = randomArray.clone();
        util.AlgorithmMetrics metrics = new util.AlgorithmMetrics();
        new MergeSort(metrics).sort(arrayCopy);

        blackhole.consume(arrayCopy);
        blackhole.consume(metrics.getMaxDepth());
    }
}