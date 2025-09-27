package util;

import java.util.Random;

/**
 * Utility class containing common array operations used by sorting algorithms
 * Includes partition, swap, shuffle, and guard methods
 */
public class ArrayUtils {
    private static final Random random = new Random();

    private ArrayUtils() {
        // Utility class - prevent instantiation
    }

    // ==================== SWAP OPERATIONS ====================

    /**
     * Swap two elements in an array and record the swap in metrics
     */
    public static void swap(int[] arr, int i, int j, AlgorithmMetrics metrics) {
        if (i == j) return; // No need to swap same element

        if (metrics != null) {
            metrics.recordSwap();
        }

        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * Swap two elements in an array (without metrics)
     */
    public static void swap(int[] arr, int i, int j) {
        if (i == j) return;

        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ==================== SHUFFLE OPERATIONS ====================

    /**
     * Fisher-Yates shuffle for the entire array
     */
    public static void shuffle(int[] arr, AlgorithmMetrics metrics) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swap(arr, i, j, metrics);
        }
    }

    /**
     * Shuffle a portion of the array
     */
    public static void shuffle(int[] arr, int low, int high, AlgorithmMetrics metrics) {
        for (int i = high; i > low; i--) {
            int j = low + random.nextInt(i - low + 1);
            swap(arr, i, j, metrics);
        }
    }

    // ==================== PARTITION OPERATIONS ====================

    /**
     * Lomuto partition scheme - returns pivot index
     */
    public static int partitionLomuto(int[] arr, int low, int high, AlgorithmMetrics metrics) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (metrics != null) {
                metrics.recordComparison();
            }

            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j, metrics);
            }
        }

        swap(arr, i + 1, high, metrics);
        return i + 1;
    }

    /**
     * Hoare partition scheme - generally more efficient
     */
    public static int partitionHoare(int[] arr, int low, int high, AlgorithmMetrics metrics) {
        int pivot = arr[low + (high - low) / 2]; // Middle element as pivot
        int i = low - 1;
        int j = high + 1;

        while (true) {
            // Find element on left that should be on right
            do {
                i++;
                if (metrics != null) {
                    metrics.recordComparison();
                }
            } while (arr[i] < pivot);

            // Find element on right that should be on left
            do {
                j--;
                if (metrics != null) {
                    metrics.recordComparison();
                }
            } while (arr[j] > pivot);

            // If pointers cross, partition is complete
            if (i >= j) {
                return j;
            }

            swap(arr, i, j, metrics);
        }
    }

    /**
     * Randomized partition - avoids worst-case performance
     */
    public static int partitionRandomized(int[] arr, int low, int high, AlgorithmMetrics metrics) {
        // Choose random pivot and move to end
        int randomIndex = low + random.nextInt(high - low + 1);
        swap(arr, randomIndex, high, metrics);

        return partitionLomuto(arr, low, high, metrics);
    }

    // ==================== GUARD/VALIDATION OPERATIONS ====================

    /**
     * Check if array is sorted in ascending order
     */
    public static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a subarray is sorted
     */
    public static boolean isSorted(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate array indices are within bounds
     */
    public static void validateIndices(int[] arr, int low, int high) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (low < 0 || low >= arr.length) {
            throw new IllegalArgumentException("Low index out of bounds: " + low);
        }
        if (high < 0 || high >= arr.length) {
            throw new IllegalArgumentException("High index out of bounds: " + high);
        }
        if (low > high) {
            throw new IllegalArgumentException("Low index cannot be greater than high index");
        }
    }

    /**
     * Check if array is too small to need sorting
     */
    public static boolean isTrivialArray(int[] arr) {
        return arr == null || arr.length <= 1;
    }

    /**
     * Get median of three values (helper for pivot selection)
     */
    public static int medianOfThree(int a, int b, int c) {
        if ((a > b) != (a > c)) {
            return a;
        } else if ((b > a) != (b > c)) {
            return b;
        } else {
            return c;
        }
    }

    // ==================== ARRAY OPERATIONS ====================

    /**
     * Copy a range from source to destination array
     */
    public static void copyRange(int[] src, int srcPos, int[] dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    /**
     * Create a copy of a subarray
     */
    public static int[] copyOfRange(int[] arr, int low, int high) {
        int length = high - low + 1;
        int[] copy = new int[length];
        System.arraycopy(arr, low, copy, 0, length);
        return copy;
    }
}