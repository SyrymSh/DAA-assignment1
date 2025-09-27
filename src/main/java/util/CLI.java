package util;

import algorithms.MergeSort;
import algorithms.QuickSort;
import algorithms.DeterministicSelect;
import algorithms.ClosestPair;
import java.util.*;
import java.io.File;

/**
 * Command Line Interface for algorithm benchmarking
 * Supports running specific algorithms, custom sizes, and output formats
 */
public class CLI {
    private static final Map<String, String> ALGORITHMS = Map.of(
            "mergesort", "MergeSort algorithm (O(n log n))",
            "quicksort", "QuickSort algorithm (O(n log n) average)",
            "select", "Deterministic Select algorithm (O(n))",
            "closest", "Closest Pair algorithm (O(n log n))",
            "all", "All algorithms (default)"
    );

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
            printHelp();
            return;
        }

        try {
            Config config = parseArgs(args);
            runBenchmark(config);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Use --help for usage information");
            System.exit(1);
        }
    }

    /**
     * Benchmark configuration
     */
    private static class Config {
        Set<String> algorithms = new HashSet<>(Arrays.asList("all"));
        List<Integer> sizes = new ArrayList<>();
        String outputFormat = "csv";
        String outputFile = null;
        int warmupRuns = 2;
        int measuredRuns = 3;
        boolean verbose = false;
    }

    /**
     * Parse command line arguments
     */
    private static Config parseArgs(String[] args) {
        Config config = new Config();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--algorithms":
                case "-a":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing algorithm list after " + args[i]);
                    }
                    config.algorithms = parseAlgorithms(args[++i]);
                    break;

                case "--sizes":
                case "-s":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing size list after " + args[i]);
                    }
                    config.sizes = parseSizes(args[++i]);
                    break;

                case "--output":
                case "-o":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing output file after " + args[i]);
                    }
                    config.outputFile = args[++i];
                    break;

                case "--format":
                case "-f":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing format after " + args[i]);
                    }
                    config.outputFormat = args[++i].toLowerCase();
                    break;

                case "--warmup":
                case "-w":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing warmup count after " + args[i]);
                    }
                    config.warmupRuns = Integer.parseInt(args[++i]);
                    break;

                case "--runs":
                case "-r":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing run count after " + args[i]);
                    }
                    config.measuredRuns = Integer.parseInt(args[++i]);
                    break;

                case "--verbose":
                case "-v":
                    config.verbose = true;
                    break;

                default:
                    throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }

        // Set default sizes if not specified
        if (config.sizes.isEmpty()) {
            config.sizes = Arrays.asList(100, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000);
        }

        return config;
    }

    private static Set<String> parseAlgorithms(String algoStr) {
        Set<String> algorithms = new HashSet<>();
        for (String algo : algoStr.split(",")) {
            String trimmed = algo.trim().toLowerCase();
            if (ALGORITHMS.containsKey(trimmed) || trimmed.equals("all")) {
                algorithms.add(trimmed);
            } else {
                throw new IllegalArgumentException("Unknown algorithm: " + algo);
            }
        }
        return algorithms;
    }

    private static List<Integer> parseSizes(String sizeStr) {
        List<Integer> sizes = new ArrayList<>();
        for (String size : sizeStr.split(",")) {
            try {
                sizes.add(Integer.parseInt(size.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid size: " + size);
            }
        }
        Collections.sort(sizes);
        return sizes;
    }

    /**
     * Run benchmark based on configuration
     */
    private static void runBenchmark(Config config) {
        if (config.verbose) {
            System.out.println("=== Algorithm Benchmark Configuration ===");
            System.out.println("Algorithms: " + String.join(", ", config.algorithms));
            System.out.println("Sizes: " + config.sizes);
            System.out.println("Warmup runs: " + config.warmupRuns);
            System.out.println("Measured runs: " + config.measuredRuns);
            System.out.println("Output format: " + config.outputFormat);
            if (config.outputFile != null) {
                System.out.println("Output file: " + config.outputFile);
            }
            System.out.println();
        }

        List<AlgorithmResult> allResults = new ArrayList<>();

        // Warmup JVM
        if (config.verbose) {
            System.out.println("🔧 Warming up JVM...");
        }
        warmupAlgorithms(config);

        // Benchmark each size
        for (int size : config.sizes) {
            if (config.verbose) {
                System.out.println("\n--- Benchmarking size: " + size + " ---");
            }

            List<AlgorithmResult> sizeResults = benchmarkSize(size, config);
            allResults.addAll(sizeResults);

            if (config.verbose) {
                printSizeComparison(sizeResults);
            }
        }

        // Output results
        outputResults(allResults, config);

        if (config.verbose) {
            System.out.println("\n🎉 Benchmark completed successfully!");
        }
    }

    private static List<AlgorithmResult> benchmarkSize(int size, Config config) {
        List<AlgorithmResult> results = new ArrayList<>();

        boolean runAll = config.algorithms.contains("all");

        if (runAll || config.algorithms.contains("mergesort")) {
            results.add(benchmarkSortAlgorithm("MergeSort", size, config));
        }

        if (runAll || config.algorithms.contains("quicksort")) {
            results.add(benchmarkSortAlgorithm("QuickSort", size, config));
        }

        if (runAll || config.algorithms.contains("select")) {
            results.add(benchmarkSelectAlgorithm("DeterministicSelect", size, config));
        }

        if (runAll || config.algorithms.contains("closest")) {
            results.add(benchmarkClosestPairAlgorithm("ClosestPair", size, config));
        }

        return results;
    }

    private static AlgorithmResult benchmarkSortAlgorithm(String algorithmName, int size, Config config) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        for (int run = 0; run < config.measuredRuns; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            int[] testArray = ArrayGenerator.generateRandom(size);

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

        return createResult(size, algorithmName, totalTime, totalComparisons, totalSwaps, maxDepth, config.measuredRuns);
    }

    private static AlgorithmResult benchmarkSelectAlgorithm(String algorithmName, int size, Config config) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        for (int run = 0; run < config.measuredRuns; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            int[] testArray = ArrayGenerator.generateRandom(size);

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

        return createResult(size, algorithmName, totalTime, totalComparisons, totalSwaps, maxDepth, config.measuredRuns);
    }

    private static AlgorithmResult benchmarkClosestPairAlgorithm(String algorithmName, int size, Config config) {
        long totalTime = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        int maxDepth = 0;

        for (int run = 0; run < config.measuredRuns; run++) {
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            Point[] testPoints = ArrayGenerator.generateRandomPoints(size, 0, size * 10, 0, size * 10);

            ClosestPair closestPair = new ClosestPair(metrics);
            ClosestPair.Result result = closestPair.findClosestPair(testPoints);

            // Verify against brute force for small n
            if (size <= 2000) {
                ClosestPair.Result bruteResult = closestPair.bruteForce(testPoints);
                if (Math.abs(result.distance - bruteResult.distance) > 1e-10) {
                    System.err.println("❌ " + algorithmName + " produced incorrect result!");
                }
            }

            totalTime += metrics.getElapsedTime();
            totalComparisons += metrics.getComparisons();
            totalSwaps += metrics.getSwaps();
            maxDepth = Math.max(maxDepth, metrics.getMaxDepth());
        }

        return createResult(size, algorithmName, totalTime, totalComparisons, totalSwaps, maxDepth, config.measuredRuns);
    }

    private static AlgorithmResult createResult(int n, String algorithmName, long totalTime,
                                                long totalComparisons, long totalSwaps, int maxDepth, int runs) {
        AlgorithmResult result = new AlgorithmResult();
        result.n = n;
        result.algorithmName = algorithmName;
        result.timeNs = totalTime / runs;
        result.comparisons = totalComparisons / runs;
        result.swaps = totalSwaps / runs;
        result.maxDepth = maxDepth;
        return result;
    }

    private static void warmupAlgorithms(Config config) {
        // Warmup sorting/select algorithms
        int[] warmupArray = ArrayGenerator.generateRandom(1000);
        AlgorithmMetrics metrics = new AlgorithmMetrics();

        for (int i = 0; i < config.warmupRuns; i++) {
            new MergeSort(metrics).sort(warmupArray.clone());
            new QuickSort(metrics).sort(warmupArray.clone());
            new DeterministicSelect(metrics).select(warmupArray.clone(), warmupArray.length / 2);
            metrics.reset();
        }

        // Warmup closest pair
        Point[] warmupPoints = ArrayGenerator.generateRandomPoints(100, 0, 1000, 0, 1000);
        for (int i = 0; i < config.warmupRuns; i++) {
            AlgorithmMetrics cpMetrics = new AlgorithmMetrics();
            new ClosestPair(cpMetrics).findClosestPair(warmupPoints.clone());
        }
    }

    private static void outputResults(List<AlgorithmResult> results, Config config) {
        String filename = config.outputFile;
        if (filename == null) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
            filename = "benchmark_" + timestamp + "." + config.outputFormat;
        }

        switch (config.outputFormat.toLowerCase()) {
            case "csv":
                outputCSV(results, filename, config);
                break;
            case "json":
                outputJSON(results, filename, config);
                break;
            case "table":
                outputTable(results, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + config.outputFormat);
        }
    }

    private static void outputCSV(List<AlgorithmResult> results, String filename, Config config) {
        boolean success = CSVWriter.writeMetrics(filename, results);
        if (success && config.verbose) {
            System.out.println("📊 Results saved to: " + filename);
        }
    }

    private static void outputJSON(List<AlgorithmResult> results, String filename, Config config) {
        // Simple JSON output implementation
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename));
            writer.println("{");
            writer.println("  \"benchmarkResults\": [");

            for (int i = 0; i < results.size(); i++) {
                AlgorithmResult r = results.get(i);
                writer.printf("    {\"n\": %d, \"algorithm\": \"%s\", \"time_ns\": %d, \"comparisons\": %d, \"swaps\": %d, \"max_depth\": %d}",
                        r.n, r.algorithmName, r.timeNs, r.comparisons, r.swaps, r.maxDepth);
                if (i < results.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }

            writer.println("  ]");
            writer.println("}");
            writer.close();

            if (config.verbose) {
                System.out.println("📊 Results saved to: " + filename);
            }
        } catch (java.io.IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }

    private static void outputTable(List<AlgorithmResult> results, Config config) {
        System.out.println("\n=== BENCHMARK RESULTS ===");
        System.out.printf("%-10s %-15s %-12s %-12s %-12s %-10s%n",
                "Size", "Algorithm", "Time (ns)", "Comparisons", "Swaps", "Max Depth");
        System.out.println("--------------------------------------------------------------------------------");

        for (AlgorithmResult r : results) {
            System.out.printf("%-10d %-15s %-,12d %-,12d %-,12d %-10d%n",
                    r.n, r.algorithmName, r.timeNs, r.comparisons, r.swaps, r.maxDepth);
        }
    }

    private static void printSizeComparison(List<AlgorithmResult> results) {
        if (!results.isEmpty()) {
            for (AlgorithmResult result : results) {
                System.out.printf("  %s (n=%d): %,9d ns%n", result.algorithmName, result.n, result.timeNs);
            }
        }
    }

    private static void printHelp() {
        System.out.println("Algorithm Benchmark CLI");
        System.out.println("Usage: java util.CLI [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -a, --algorithms ALGOS    Comma-separated algorithms to run");
        System.out.println("                            Available: " + String.join(", ", ALGORITHMS.keySet()));
        System.out.println("  -s, --sizes SIZES         Comma-separated input sizes (default: 100,500,1000,2000,4000,8000,16000,32000,64000)");
        System.out.println("  -o, --output FILE         Output file (default: auto-generated)");
        System.out.println("  -f, --format FORMAT       Output format: csv, json, table (default: csv)");
        System.out.println("  -w, --warmup RUNS         Warmup runs (default: 2)");
        System.out.println("  -r, --runs RUNS           Measured runs per size (default: 3)");
        System.out.println("  -v, --verbose             Verbose output");
        System.out.println("  -h, --help                Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java util.CLI --algorithms mergesort,quicksort --sizes 1000,2000,4000");
        System.out.println("  java util.CLI -a all -s 500,1000,2000 -f table -v");
        System.out.println("  java util.CLI --algorithms select --output select_results.json --format json");
    }
}