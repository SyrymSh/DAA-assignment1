package algorithms;

import util.AlgorithmMetrics;
import util.ArrayUtils;

public class QuickSort {
    private AlgorithmMetrics metrics;
    private static final int INSERTION_CUTOFF = 15;

    public QuickSort(AlgorithmMetrics metrics) {
        this.metrics = metrics;
    }

    public void sort(int[] arr) {
        // Use guard to check trivial cases
        if (ArrayUtils.isTrivialArray(arr)) return;

        metrics.startTimer();
        quickSort(arr, 0, arr.length - 1);
        metrics.stopTimer();

        // Verify result
        if (!ArrayUtils.isSorted(arr)) {
            System.err.println("QuickSort: Array not sorted correctly!");
        }
    }

    private void quickSort(int[] arr, int low, int high) {
        metrics.enterRecursion();

        // Use iteration for larger partition to limit stack depth
        while (high - low > INSERTION_CUTOFF) {
            // Use utility method for randomized partition
            int pivotIndex = ArrayUtils.partitionRandomized(arr, low, high, metrics);

            // Recurse on smaller partition, iterate on larger
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(arr, low, pivotIndex - 1);
                low = pivotIndex + 1;
            } else {
                quickSort(arr, pivotIndex + 1, high);
                high = pivotIndex - 1;
            }
        }

        // Insertion sort for small arrays
        if (high > low) {
            insertionSort(arr, low, high);
        }

        metrics.exitRecursion();
    }

    private void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= low) {
                metrics.recordComparison();
                if (arr[j] > key) {
                    // Use utility swap method
                    ArrayUtils.swap(arr, j, j + 1, metrics);
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
        }
    }
}