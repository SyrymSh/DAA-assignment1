package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import util.AlgorithmMetrics;
import java.util.Arrays;

public class DeterministicSelectTest {
    private AlgorithmMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new AlgorithmMetrics();
    }

    @Test
    void testSelectBasic() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {5, 2, 8, 1, 9, 3};

        // Test various k values
        assertEquals(1, selector.select(arr.clone(), 0)); // smallest
        assertEquals(3, selector.select(arr.clone(), 2)); // median-ish
        assertEquals(9, selector.select(arr.clone(), 5)); // largest
    }

    @Test
    void testSelectComparedToArraysSort() {
        DeterministicSelect selector = new DeterministicSelect(metrics);

        // Test multiple random arrays
        for (int size : new int[]{10, 50, 100, 500}) {
            int[] arr = generateRandomArray(size);
            int k = size / 2; // Test median

            int expected = getKthUsingSort(arr, k);
            int actual = selector.select(arr.clone(), k);

            assertEquals(expected, actual,
                    "Select failed for size " + size + ", k=" + k);
        }
    }

    @Test
    void testSelectWithDuplicates() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};

        assertEquals(1, selector.select(arr.clone(), 0));
        assertEquals(1, selector.select(arr.clone(), 1));
        assertEquals(2, selector.select(arr.clone(), 2));
        assertEquals(3, selector.select(arr.clone(), 3));
        assertEquals(3, selector.select(arr.clone(), 4));
    }

    @Test
    void testSelectAlreadySorted() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int k = 0; k < arr.length; k++) {
            assertEquals(arr[k], selector.select(arr.clone(), k),
                    "Failed for k=" + k);
        }
    }

    @Test
    void testSelectReverseSorted() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        for (int k = 0; k < arr.length; k++) {
            assertEquals(k + 1, selector.select(arr.clone(), k),
                    "Failed for k=" + k);
        }
    }

    @Test
    void testSelectSingleElement() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {42};

        assertEquals(42, selector.select(arr, 0));
    }

    @Test
    void testSelectEmptyArray() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {};

        assertThrows(IllegalArgumentException.class, () -> {
            selector.select(arr, 0);
        });
    }

    @Test
    void testSelectInvalidK() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = {1, 2, 3};

        assertThrows(IllegalArgumentException.class, () -> {
            selector.select(arr, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            selector.select(arr, 3);
        });
    }

    @Test
    void testSelectLargeArray() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = generateRandomArray(1000);
        int k = 499; // Test near median

        int expected = getKthUsingSort(arr, k);
        int actual = selector.select(arr.clone(), k);

        assertEquals(expected, actual);
    }

    @Test
    void testSelectVsSimpleVersion() {
        DeterministicSelect selector = new DeterministicSelect(metrics);
        int[] arr = generateRandomArray(100);

        for (int k = 0; k < arr.length; k++) {
            int expected = selector.selectSimple(arr.clone(), k);
            int actual = selector.select(arr.clone(), k);

            assertEquals(expected, actual, "MoM vs Simple mismatch for k=" + k);
        }
    }

    // Helper methods
    private int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = (int) (Math.random() * size * 10);
        }
        return arr;
    }

    private int getKthUsingSort(int[] arr, int k) {
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        return sorted[k];
    }
}