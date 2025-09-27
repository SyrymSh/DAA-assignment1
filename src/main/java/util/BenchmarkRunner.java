package util;

import algorithms.MergeSort;
import algorithms.QuickSort;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {
    private static final int[] SIZES = {100, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 3;

    public static void main(String[] args) {
        System.out.println("=== Algorithm Benchmark Runner ===");
        System.out.println("Sizes: " + java.util.Arrays.toString(SIZES));

        List<AlgorithmResult> allResults = new ArrayList<>();

        // Warmup JVM
        System.out.println("\n🔧 Warming up JVM...");
        warmupAlgorithms();

        // Benchmark each size
        for (int size : SIZES) {
            System.out.println("\n--- Benchmarking size: " + size + " ---");

            List<AlgorithmResult> sizeResults = benchmarkSize(size);
            allResults.addAll(sizeResults);

            // Print quick comparison for this size
            printSizeComparison(sizeResults);
        }

        // Save all results to unified folder
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(new java.util.Date());
        String filename = "benchmark_comparison_" + timestamp + ".csv";

        boolean success = CSVWriter.writeMetrics(filename, allResults);

        if (success) {
            System.out.println("\n🎉 Benchmark completed successfully!");
            System.out.println("📊 Results saved to: test-results/" + filename);

            // Print summary
            printSummary(allResults);
        } else {
            System.err.println("❌ Benchmark failed to save results");
        }
    }

    private static List<AlgorithmResult> benchmarkSize(int size) {
        List<AlgorithmResult> results = new ArrayList<>();
        int[] originalArray = ArrayGenerator.generateRandom(size);

        // Benchmark MergeSort
        AlgorithmResult mergeResult = benchmarkAlgorithm("MergeSort", originalArray,
                metrics -> new MergeSort(metrics));
        results.add(mergeResult);

        // Benchmark QuickSort
        AlgorithmResult quickResult = benchmarkAlgorithm("QuickSort", originalArray,
                metrics -> new QuickSort(metrics));
        results.add(quickResult);

        return results;
    }

    @FunctionalInterface
    private interface AlgorithmFactory {
        Object create(AlgorithmMetrics metrics);
    }

    private static AlgorithmResult benchmarkAlgorithm(String algorithmName,
                                                      int[] originalArray,
                                                      AlgorithmFactory factory) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        // Run multiple times for stability
        for (int run = 0; run < MEASURED_RUNS; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();

            Object sorter = factory.create(metrics);
            int[] testArray = originalArray.clone();

            // Cast to appropriate type and sort
            if (sorter instanceof MergeSort) {
                ((MergeSort) sorter).sort(testArray);
            } else if (sorter instanceof QuickSort) {
                ((QuickSort) sorter).sort(testArray);
            }

            // Verify sorting is correct
            if (!isSorted(testArray)) {
                System.err.println("❌ " + algorithmName + " produced incorrect result!");
            }

            // Accumulate metrics
            totalTime += metrics.getElapsedTime();
            totalComparisons += metrics.getComparisons();
            totalSwaps += metrics.getSwaps();
            maxDepth = Math.max(maxDepth, metrics.getMaxDepth());
        }

        // Create average result
        AlgorithmResult result = new AlgorithmResult();
        result.n = originalArray.length;
        result.algorithmName = algorithmName;
        result.timeNs = totalTime / MEASURED_RUNS;
        result.comparisons = totalComparisons / MEASURED_RUNS;
        result.swaps = totalSwaps / MEASURED_RUNS;
        result.maxDepth = maxDepth;

        return result;
    }

    private static void warmupAlgorithms() {
        int[] warmupArray = ArrayGenerator.generateRandom(1000);
        AlgorithmMetrics metrics = new AlgorithmMetrics();

        // Warmup both algorithms
        for (int i = 0; i < WARMUP_RUNS; i++) {
            new MergeSort(metrics).sort(warmupArray.clone());
            new QuickSort(metrics).sort(warmupArray.clone());
            metrics.reset();
        }
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    private static void printSizeComparison(List<AlgorithmResult> results) {
        AlgorithmResult merge = results.get(0);
        AlgorithmResult quick = results.get(1);

        double timeRatio = (double) quick.timeNs / merge.timeNs;
        System.out.printf("  %s: %,d ns | %s: %,d ns | Ratio: %.2fx%n",
                merge.algorithmName, merge.timeNs,
                quick.algorithmName, quick.timeNs,
                timeRatio);
    }

    private static void printSummary(List<AlgorithmResult> results) {
        System.out.println("\n=== SUMMARY ===");
        System.out.printf("%-10s %-12s %-12s %-10s%n",
                "Size", "MergeSort", "QuickSort", "Ratio");
        System.out.println("----------------------------------------");

        for (int i = 0; i < results.size(); i += 2) {
            AlgorithmResult merge = results.get(i);
            AlgorithmResult quick = results.get(i + 1);
            double ratio = (double) quick.timeNs / merge.timeNs;

            System.out.printf("%-10d %-12d %-12d %-10.2fx%n",
                    merge.n, merge.timeNs, quick.timeNs, ratio);
        }
    }
}