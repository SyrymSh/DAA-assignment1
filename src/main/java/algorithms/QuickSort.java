package algorithms;

import util.AlgorithmMetrics;

public class QuickSort {
    private AlgorithmMetrics metrics;
    private static final int INSERTION_CUTOFF = 15;

    public QuickSort(AlgorithmMetrics metrics) {
        this.metrics = metrics;
    }

    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        metrics.startTimer();
        quickSort(arr, 0, arr.length - 1);
        metrics.stopTimer();
    }

    private void quickSort(int[] arr, int low, int high) {
        metrics.enterRecursion();

        while (high - low > INSERTION_CUTOFF) {
            int pivotIndex = partition(arr, low, high);

            if (pivotIndex - low < high - pivotIndex) {
                quickSort(arr, low, pivotIndex - 1);
                low = pivotIndex + 1;
            } else {
                quickSort(arr, pivotIndex + 1, high);
                high = pivotIndex - 1;
            }
        }

        if (high > low) {
            insertionSort(arr, low, high);
        }

        metrics.exitRecursion();
    }

    private int partition(int[] arr, int low, int high) {
        int pivotIndex = low + (int) (Math.random() * (high - low + 1));
        swap(arr, pivotIndex, high);

        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            metrics.recordComparison();
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= low) {
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

    private void swap(int[] arr, int i, int j) {
        if (i != j) {
            metrics.recordSwap();
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}