package util;

import algorithms.MergeSort;
import algorithms.QuickSort;
import algorithms.DeterministicSelect;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

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
                metrics -> new MergeSort(metrics), AlgorithmType.SORT);
        results.add(mergeResult);

        // Benchmark QuickSort
        AlgorithmResult quickResult = benchmarkAlgorithm("QuickSort", originalArray,
                metrics -> new QuickSort(metrics), AlgorithmType.SORT);
        results.add(quickResult);

        // Benchmark DeterministicSelect (find median)
        AlgorithmResult selectResult = benchmarkAlgorithm("DeterministicSelect", originalArray,
                metrics -> new DeterministicSelect(metrics), AlgorithmType.SELECT);
        results.add(selectResult);

        return results;
    }

    @FunctionalInterface
    private interface AlgorithmFactory {
        Object create(AlgorithmMetrics metrics);
    }

    private enum AlgorithmType {
        SORT, SELECT
    }

    private static AlgorithmResult benchmarkAlgorithm(String algorithmName,
                                                      int[] originalArray,
                                                      AlgorithmFactory factory,
                                                      AlgorithmType type) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        // Run multiple times for stability
        for (int run = 0; run < MEASURED_RUNS; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            int[] testArray = originalArray.clone();

            Object algorithm = factory.create(metrics);

            if (type == AlgorithmType.SORT) {
                // Handle sorting algorithms
                if (algorithm instanceof MergeSort) {
                    ((MergeSort) algorithm).sort(testArray);
                } else if (algorithm instanceof QuickSort) {
                    ((QuickSort) algorithm).sort(testArray);
                }

                // Verify sorting is correct
                if (!ArrayUtils.isSorted(testArray)) {
                    System.err.println("❌ " + algorithmName + " produced incorrect result!");
                }
            } else if (type == AlgorithmType.SELECT) {
                // Handle selection algorithm
                DeterministicSelect selector = (DeterministicSelect) algorithm;
                int k = testArray.length / 2; // Find median for consistent comparison
                int result = selector.select(testArray, k);

                // Verify result against Arrays.sort()
                int[] sortedCopy = testArray.clone();
                Arrays.sort(sortedCopy);
                int expected = sortedCopy[k];

                if (result != expected) {
                    System.err.println("❌ " + algorithmName + " produced incorrect result!");
                    System.err.println("Expected: " + expected + ", Got: " + result);
                }
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

        // Warmup all algorithms
        for (int i = 0; i < WARMUP_RUNS; i++) {
            // Warmup sorting algorithms
            new MergeSort(metrics).sort(warmupArray.clone());
            new QuickSort(metrics).sort(warmupArray.clone());

            // Warmup selection algorithm (find median)
            DeterministicSelect selector = new DeterministicSelect(metrics);
            int k = warmupArray.length / 2;
            selector.select(warmupArray.clone(), k);

            metrics.reset();
        }
    }

    private static void printSizeComparison(List<AlgorithmResult> results) {
        if (results.size() >= 3) {
            AlgorithmResult merge = results.get(0);
            AlgorithmResult quick = results.get(1);
            AlgorithmResult select = results.get(2);

            System.out.printf("  %s: %,9d ns | %s: %,9d ns | %s: %,9d ns%n",
                    merge.algorithmName, merge.timeNs,
                    quick.algorithmName, quick.timeNs,
                    select.algorithmName, select.timeNs);

            // Show relative performance
            double quickVsMerge = (double) quick.timeNs / merge.timeNs;
            double selectVsMerge = (double) select.timeNs / merge.timeNs;
            System.out.printf("  Relative to MergeSort: QuickSort=%.2fx, Select=%.2fx%n",
                    quickVsMerge, selectVsMerge);
        }
    }

    private static void printSummary(List<AlgorithmResult> results) {
        System.out.println("\n=== SUMMARY ===");
        System.out.printf("%-10s %-12s %-12s %-12s %-10s%n",
                "Size", "MergeSort", "QuickSort", "Select", "Q/M Ratio");
        System.out.println("----------------------------------------------------");

        for (int i = 0; i < results.size(); i += 3) {
            if (i + 2 < results.size()) {
                AlgorithmResult merge = results.get(i);
                AlgorithmResult quick = results.get(i + 1);
                AlgorithmResult select = results.get(i + 2);

                double quickRatio = (double) quick.timeNs / merge.timeNs;
                double selectRatio = (double) select.timeNs / merge.timeNs;

                System.out.printf("%-10d %-12d %-12d %-12d %-10.2fx%n",
                        merge.n, merge.timeNs, quick.timeNs, select.timeNs, quickRatio);
            }
        }

        // Print additional analysis
        printComplexityAnalysis(results);
    }

    private static void printComplexityAnalysis(List<AlgorithmResult> results) {
        System.out.println("\n=== COMPLEXITY ANALYSIS ===");
        System.out.println("Expected complexities:");
        System.out.println("- MergeSort: O(n log n)");
        System.out.println("- QuickSort: O(n log n) average, O(n²) worst-case");
        System.out.println("- DeterministicSelect: O(n) guaranteed");
        System.out.println("\nObserved trends:");

        // Analyze the last few sizes to see growth patterns
        int analysisSizes = Math.min(3, SIZES.length);
        for (int i = SIZES.length - analysisSizes; i < SIZES.length; i++) {
            int sizeIndex = i * 3;
            if (sizeIndex + 2 < results.size()) {
                AlgorithmResult merge = results.get(sizeIndex);
                AlgorithmResult quick = results.get(sizeIndex + 1);
                AlgorithmResult select = results.get(sizeIndex + 2);

                System.out.printf("\nSize %d:", merge.n);
                System.out.printf(" MergeSort=%,dns, QuickSort=%,dns, Select=%,dns",
                        merge.timeNs, quick.timeNs, select.timeNs);
            }
        }
    }
}