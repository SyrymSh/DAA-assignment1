package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {
    public static void writeMetrics(String filename, List<AlgorithmResult> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("n,algorithm,time_ns,comparisons,swaps,max_depth");

            for (AlgorithmResult result : results) {
                writer.printf("%d,%s,%d,%d,%d,%d%n",
                        result.n,
                        result.algorithmName,
                        result.timeNs,
                        result.comparisons,
                        result.swaps,
                        result.maxDepth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}