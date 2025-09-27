package algorithms;

import util.AlgorithmMetrics;
import util.ArrayUtils;
import java.util.Arrays;

/**
 * Deterministic Select using Median-of-Medians (group by 5)
 * Guaranteed O(n) time complexity for order statistics
 */
public class DeterministicSelect {
    private AlgorithmMetrics metrics;
    private static final int GROUP_SIZE = 5;

    public DeterministicSelect(AlgorithmMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Find the k-th smallest element in the array (0-indexed)
     * @param arr The input array
     * @param k The index of the desired order statistic (0 <= k < arr.length)
     * @return The k-th smallest element
     */
    public int select(int[] arr, int k) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        if (k < 0 || k >= arr.length) {
            throw new IllegalArgumentException("k must be between 0 and " + (arr.length - 1));
        }

        metrics.startTimer();
        int result = select(arr, 0, arr.length - 1, k);
        metrics.stopTimer();

        return result;
    }

    /**
     * Recursive select implementation with Median-of-Medians pivot selection
     */
    private int select(int[] arr, int left, int right, int k) {
        metrics.enterRecursion();

        // Base case: small array - use insertion sort
        if (right - left + 1 <= GROUP_SIZE) {
            insertionSort(arr, left, right);
            int result = arr[left + k];
            metrics.exitRecursion();
            return result;
        }

        // Step 1: Divide into groups of 5 and find their medians
        int numGroups = (right - left + 1 + GROUP_SIZE - 1) / GROUP_SIZE;
        int[] medians = new int[numGroups];

        for (int i = 0; i < numGroups; i++) {
            int groupLeft = left + i * GROUP_SIZE;
            int groupRight = Math.min(groupLeft + GROUP_SIZE - 1, right);

            // Find median of this group using insertion sort
            insertionSort(arr, groupLeft, groupRight);
            int medianIndex = groupLeft + (groupRight - groupLeft) / 2;
            medians[i] = arr[medianIndex];
        }

        // Step 2: Recursively find median of medians
        int mom = select(medians, 0, medians.length - 1, medians.length / 2);

        // Step 3: Partition around median of medians
        int pivotIndex = partitionAroundPivot(arr, left, right, mom);

        // Step 4: Recurse on the appropriate partition
        int relativeK = pivotIndex - left;

        if (k == relativeK) {
            // Found the k-th element
            metrics.exitRecursion();
            return arr[pivotIndex];
        } else if (k < relativeK) {
            // k-th element is in left partition
            int result = select(arr, left, pivotIndex - 1, k);
            metrics.exitRecursion();
            return result;
        } else {
            // k-th element is in right partition
            int result = select(arr, pivotIndex + 1, right, k - relativeK - 1);
            metrics.exitRecursion();
            return result;
        }
    }

    /**
     * Partition the array around a pivot value (not index)
     * Returns the final position of the pivot
     */
    private int partitionAroundPivot(int[] arr, int left, int right, int pivotValue) {
        // First, find the pivot value and move it to the end
        int pivotIndex = -1;
        for (int i = left; i <= right; i++) {
            if (arr[i] == pivotValue) {
                pivotIndex = i;
                break;
            }
        }

        if (pivotIndex == -1) {
            // This shouldn't happen with correct MoM implementation
            throw new IllegalStateException("Pivot value not found in array");
        }

        // Move pivot to end
        ArrayUtils.swap(arr, pivotIndex, right, metrics);

        // Now partition using Lomuto scheme
        int i = left - 1;
        for (int j = left; j < right; j++) {
            metrics.recordComparison();
            if (arr[j] <= pivotValue) {
                i++;
                ArrayUtils.swap(arr, i, j, metrics);
            }
        }

        // Move pivot to its final position
        ArrayUtils.swap(arr, i + 1, right, metrics);
        return i + 1;
    }

    /**
     * Insertion sort for small arrays
     */
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
            if (j + 1 != i) {
                metrics.recordSwap();
            }
        }
    }

    /**
     * Alternative simpler version for comparison with Arrays.sort
     */
    public int selectSimple(int[] arr, int k) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        if (k < 0 || k >= arr.length) {
            throw new IllegalArgumentException("k must be between 0 and " + (arr.length - 1));
        }

        metrics.startTimer();
        int result = selectSimple(arr, 0, arr.length - 1, k);
        metrics.stopTimer();

        return result;
    }

    private int selectSimple(int[] arr, int left, int right, int k) {
        metrics.enterRecursion();

        if (left == right) {
            metrics.exitRecursion();
            return arr[left];
        }

        // Use randomized partition for simpler implementation
        int pivotIndex = randomizedPartition(arr, left, right);
        int relativeK = pivotIndex - left;

        if (k == relativeK) {
            metrics.exitRecursion();
            return arr[pivotIndex];
        } else if (k < relativeK) {
            int result = selectSimple(arr, left, pivotIndex - 1, k);
            metrics.exitRecursion();
            return result;
        } else {
            int result = selectSimple(arr, pivotIndex + 1, right, k - relativeK - 1);
            metrics.exitRecursion();
            return result;
        }
    }

    private int randomizedPartition(int[] arr, int left, int right) {
        int randomIndex = left + (int) (Math.random() * (right - left + 1));
        ArrayUtils.swap(arr, randomIndex, right, metrics);

        return partition(arr, left, right);
    }

    private int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left - 1;

        for (int j = left; j < right; j++) {
            metrics.recordComparison();
            if (arr[j] <= pivot) {
                i++;
                ArrayUtils.swap(arr, i, j, metrics);
            }
        }

        ArrayUtils.swap(arr, i + 1, right, metrics);
        return i + 1;
    }
}