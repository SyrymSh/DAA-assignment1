import algorithms.MergeSort;
import util.AlgorithmMetrics;
import util.AlgorithmResult;
import util.CSVWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<AlgorithmResult> results = new ArrayList<>();

        for (int n = 1000; n <= 100000; n *= 2) {
            int[] arr = generateRandomArray(n);

            AlgorithmMetrics metrics = new AlgorithmMetrics();
            MergeSort sorter = new MergeSort(metrics);

            sorter.sort(arr.clone());

            AlgorithmResult result = new AlgorithmResult(
                    n, "MergeSort",
                    metrics.getElapsedTime(),
                    metrics.getComparisons(),
                    metrics.getSwaps(),
                    metrics.getMaxDepth()
            );

            results.add(result);
            metrics.reset();
        }

        CSVWriter.writeMetrics("metrics.csv", results);
        System.out.println("Metrics saved to metrics.csv");
    }

    private static int[] generateRandomArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = (int) (Math.random() * n);
        }
        return arr;
    }
}