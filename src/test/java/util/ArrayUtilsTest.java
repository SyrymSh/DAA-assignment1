package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilsTest {

    @Test
    void testSwap() {
        int[] arr = {1, 2, 3, 4, 5};
        ArrayUtils.swap(arr, 0, 4);
        assertArrayEquals(new int[]{5, 2, 3, 4, 1}, arr);
    }

    @Test
    void testIsSorted() {
        assertTrue(ArrayUtils.isSorted(new int[]{1, 2, 3, 4, 5}));
        assertFalse(ArrayUtils.isSorted(new int[]{5, 4, 3, 2, 1}));
    }

    @Test
    void testPartitionLomuto() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        int pivotIndex = ArrayUtils.partitionLomuto(arr, 0, arr.length - 1, metrics);

        // Check all elements before pivot are <= pivot value
        int pivotValue = arr[pivotIndex];
        for (int i = 0; i < pivotIndex; i++) {
            assertTrue(arr[i] <= pivotValue);
        }
        // Check all elements after pivot are >= pivot value
        for (int i = pivotIndex + 1; i < arr.length; i++) {
            assertTrue(arr[i] >= pivotValue);
        }
    }
}