package util;

/**
 * Main class that delegates to CLI or BenchmarkRunner
 * Provides backward compatibility
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            // Use CLI if arguments provided
            CLI.main(args);
        } else {
            // Use default benchmark runner for backward compatibility
            System.out.println("Running default benchmark...");
            System.out.println("For CLI options, use: java util.CLI --help");
            System.out.println();
            BenchmarkRunner.main(args);
        }
    }
}