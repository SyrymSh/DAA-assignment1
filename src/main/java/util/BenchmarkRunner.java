package util;

import algorithms.MergeSort;
import algorithms.QuickSort;
import algorithms.DeterministicSelect;
import algorithms.ClosestPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class BenchmarkRunner {
    private static final int[] SIZES = {100, 500, 1000, 2000, 4000, 8000, 16000, 32000};
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

            printSizeComparison(sizeResults);
        }

        // Save results
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(new java.util.Date());
        String filename = "benchmark_comparison_" + timestamp + ".csv";

        boolean success = CSVWriter.writeMetrics(filename, allResults);

        if (success) {
            System.out.println("\n🎉 Benchmark completed successfully!");
            System.out.println("📊 Results saved to: test-results/" + filename);
            printSummary(allResults);
        } else {
            System.err.println("❌ Benchmark failed to save results");
        }
    }

    private static List<AlgorithmResult> benchmarkSize(int size) {
        List<AlgorithmResult> results = new ArrayList<>();

        // For sorting/select algorithms
        int[] originalArray = ArrayGenerator.generateRandom(size);

        // Benchmark algorithms
        results.add(benchmarkSortAlgorithm("MergeSort", originalArray));
        results.add(benchmarkSortAlgorithm("QuickSort", originalArray));
        results.add(benchmarkSelectAlgorithm("DeterministicSelect", originalArray));

        // Benchmark ClosestPair with points
        Point[] points = ArrayGenerator.generateRandomPoints(size, 0, size * 10, 0, size * 10);
        results.add(benchmarkClosestPairAlgorithm("ClosestPair", points));

        return results;
    }

    private static AlgorithmResult benchmarkSortAlgorithm(String algorithmName, int[] originalArray) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        for (int run = 0; run < MEASURED_RUNS; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            int[] testArray = originalArray.clone();

            if ("MergeSort".equals(algorithmName)) {
                new MergeSort(metrics).sort(testArray);
            } else {
                new QuickSort(metrics).sort(testArray);
            }

            if (!ArrayUtils.isSorted(testArray)) {
                System.err.println("❌ " + algorithmName + " produced incorrect result!");
            }

            totalTime += metrics.getElapsedTime();
            totalComparisons += metrics.getComparisons();
            totalSwaps += metrics.getSwaps();
            maxDepth = Math.max(maxDepth, metrics.getMaxDepth());
        }

        return createResult(originalArray.length, algorithmName, totalTime, totalComparisons, totalSwaps, maxDepth);
    }

    private static AlgorithmResult benchmarkSelectAlgorithm(String algorithmName, int[] originalArray) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        for (int run = 0; run < MEASURED_RUNS; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            int[] testArray = originalArray.clone();

            int k = testArray.length / 2;
            int result = new DeterministicSelect(metrics).select(testArray, k);

            // Verify against Arrays.sort
            int[] sortedCopy = testArray.clone();
            Arrays.sort(sortedCopy);
            if (result != sortedCopy[k]) {
                System.err.println("❌ " + algorithmName + " produced incorrect result!");
            }

            totalTime += metrics.getElapsedTime();
            totalComparisons += metrics.getComparisons();
            totalSwaps += metrics.getSwaps();
            maxDepth = Math.max(maxDepth, metrics.getMaxDepth());
        }

        return createResult(originalArray.length, algorithmName, totalTime, totalComparisons, totalSwaps, maxDepth);
    }

    private static AlgorithmResult benchmarkClosestPairAlgorithm(String algorithmName, Point[] points) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        for (int run = 0; run < MEASURED_RUNS; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            Point[] testPoints = points.clone();

            ClosestPair closestPair = new ClosestPair(metrics);
            ClosestPair.Result result = closestPair.findClosestPair(testPoints); // Fixed: Use ClosestPair.Result

            // Verify against brute force for small n
            if (testPoints.length <= 2000) {
                ClosestPair.Result bruteResult = closestPair.bruteForce(testPoints); // Fixed: Use ClosestPair.Result
                if (Math.abs(result.distance - bruteResult.distance) > 1e-10) {
                    System.err.println("❌ " + algorithmName + " produced incorrect result!");
                }
            }

            totalTime += metrics.getElapsedTime();
            totalComparisons += metrics.getComparisons();
            totalSwaps += metrics.getSwaps();
            maxDepth = Math.max(maxDepth, metrics.getMaxDepth());
        }

        return createResult(points.length, algorithmName, totalTime, totalComparisons, totalSwaps, maxDepth);
    }

    private static AlgorithmResult createResult(int n, String algorithmName, long totalTime,
                                                long totalComparisons, long totalSwaps, int maxDepth) {
        AlgorithmResult result = new AlgorithmResult();
        result.n = n;
        result.algorithmName = algorithmName;
        result.timeNs = totalTime / MEASURED_RUNS;
        result.comparisons = totalComparisons / MEASURED_RUNS;
        result.swaps = totalSwaps / MEASURED_RUNS;
        result.maxDepth = maxDepth;
        return result;
    }

    private static void warmupAlgorithms() {
        // Warmup sorting/select algorithms
        int[] warmupArray = ArrayGenerator.generateRandom(1000);
        AlgorithmMetrics metrics = new AlgorithmMetrics();

        for (int i = 0; i < WARMUP_RUNS; i++) {
            new MergeSort(metrics).sort(warmupArray.clone());
            new QuickSort(metrics).sort(warmupArray.clone());
            new DeterministicSelect(metrics).select(warmupArray.clone(), warmupArray.length / 2);
            metrics.reset();
        }

        // Warmup closest pair
        Point[] warmupPoints = ArrayGenerator.generateRandomPoints(100, 0, 1000, 0, 1000);
        for (int i = 0; i < WARMUP_RUNS; i++) {
            AlgorithmMetrics cpMetrics = new AlgorithmMetrics();
            new ClosestPair(cpMetrics).findClosestPair(warmupPoints.clone());
        }
    }

    private static void printSizeComparison(List<AlgorithmResult> results) {
        if (results.size() >= 4) {
            System.out.printf("  %s: %,9d ns%n", results.get(0).algorithmName, results.get(0).timeNs);
            System.out.printf("  %s: %,9d ns%n", results.get(1).algorithmName, results.get(1).timeNs);
            System.out.printf("  %s: %,9d ns%n", results.get(2).algorithmName, results.get(2).timeNs);
            System.out.printf("  %s: %,9d ns%n", results.get(3).algorithmName, results.get(3).timeNs);
        }
    }

    private static void printSummary(List<AlgorithmResult> results) {
        System.out.println("\n=== SUMMARY ===");
        System.out.printf("%-10s %-12s %-12s %-12s %-12s%n",
                "Size", "MergeSort", "QuickSort", "Select", "ClosestPair");
        System.out.println("------------------------------------------------------------");

        for (int i = 0; i < results.size(); i += 4) {
            if (i + 3 < results.size()) {
                AlgorithmResult merge = results.get(i);
                AlgorithmResult quick = results.get(i + 1);
                AlgorithmResult select = results.get(i + 2);
                AlgorithmResult closest = results.get(i + 3);

                System.out.printf("%-10d %-12d %-12d %-12d %-12d%n",
                        merge.n, merge.timeNs, quick.timeNs, select.timeNs, closest.timeNs);
            }
        }
    }
}