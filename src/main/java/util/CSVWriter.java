package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSVWriter {
    private static final String RESULTS_FOLDER = "test-results";

    public static boolean writeMetrics(String filename, List<AlgorithmResult> results) {
        return writeMetrics(RESULTS_FOLDER, filename, results);
    }

    public static boolean writeMetrics(String folderPath, String filename, List<AlgorithmResult> results) {
        try {
            // Create results folder if it doesn't exist
            File folder = new File(folderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    System.err.println("Failed to create directory: " + folderPath);
                    return false;
                }
                System.out.println("Created results folder: " + folder.getAbsolutePath());
            }

            File outputFile = new File(folder, filename);

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                // Write header
                writer.println("n,algorithm,time_ns,comparisons,swaps,max_depth");

                // Write data
                for (AlgorithmResult result : results) {
                    writer.printf("%d,%s,%d,%d,%d,%d%n",
                            result.n,
                            result.algorithmName,
                            result.timeNs,
                            result.comparisons,
                            result.swaps,
                            result.maxDepth);
                }
            }

            System.out.println("✓ Results saved to: " + outputFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
            System.err.println("✗ Error writing CSV: " + e.getMessage());
            return false;
        }
    }

    // Method to create timestamped subfolder for organized results
    public static String createTimestampedFolder() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String folderPath = RESULTS_FOLDER + File.separator + timestamp;
        new File(folderPath).mkdirs();
        return folderPath;
    }
}