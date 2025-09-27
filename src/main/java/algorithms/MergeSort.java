package algorithms;

import util.AlgorithmMetrics;
import util.ArrayUtils;

public class MergeSort {
    private AlgorithmMetrics metrics;
    private static final int INSERTION_CUTOFF = 15;

    public MergeSort(AlgorithmMetrics metrics) {
        this.metrics = metrics;
    }

    public void sort(int[] arr) {
        if (ArrayUtils.isTrivialArray(arr)) return;

        metrics.startTimer();
        int[] buffer = new int[arr.length];
        mergeSort(arr, 0, arr.length - 1, buffer);
        metrics.stopTimer();

        if (!ArrayUtils.isSorted(arr)) {
            System.err.println("MergeSort: Array not sorted correctly!");
        }
    }

    private void mergeSort(int[] arr, int left, int right, int[] buffer) {
        metrics.enterRecursion();

        // Use cutoff for small arrays
        if (right - left <= INSERTION_CUTOFF) {
            insertionSort(arr, left, right);
            metrics.exitRecursion();
            return;
        }

        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid, buffer);
        mergeSort(arr, mid + 1, right, buffer);
        merge(arr, left, mid, right, buffer);

        metrics.exitRecursion();
    }

    private void merge(int[] arr, int left, int mid, int right, int[] buffer) {
        // Use utility method for array copy
        ArrayUtils.copyRange(arr, left, buffer, left, right - left + 1);

        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            metrics.recordComparison();
            if (buffer[i] <= buffer[j]) {
                arr[k++] = buffer[i++];
            } else {
                arr[k++] = buffer[j++];
            }
        }

        while (i <= mid) {
            arr[k++] = buffer[i++];
        }
    }

    private void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= left) {
                metrics.recordComparison();
                if (arr[j] > key) {
                    ArrayUtils.swap(arr, j, j + 1, metrics);
                    j--;
                } else {
                    break;
                }
            }
        }
    }
}