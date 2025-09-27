package algorithms;

import util.AlgorithmMetrics;

public class MergeSort {
    private AlgorithmMetrics metrics;

    public MergeSort(AlgorithmMetrics metrics) {
        this.metrics = metrics;
    }

    public void sort(int[] arr) {
        metrics.startTimer();
        int[] buffer = new int[arr.length];
        mergeSort(arr, 0, arr.length - 1, buffer);
        metrics.stopTimer();
    }

    private void mergeSort(int[] arr, int left, int right, int[] buffer) {
        metrics.enterRecursion();

        if (right - left <= 15) { // Cutoff for small arrays
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
        System.arraycopy(arr, left, buffer, left, right - left + 1);

        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            metrics.recordComparison();
            if (buffer[i] <= buffer[j]) {
                arr[k++] = buffer[i++];
            } else {
                arr[k++] = buffer[j++];
            }
            metrics.recordSwap();
        }

        while (i <= mid) {
            arr[k++] = buffer[i++];
            metrics.recordSwap();
        }
    }

    private void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= left) {
                metrics.recordComparison();
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    metrics.recordSwap();
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
            metrics.recordSwap();
        }
    }
}