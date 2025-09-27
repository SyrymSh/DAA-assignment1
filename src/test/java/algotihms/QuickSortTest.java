package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import util.AlgorithmMetrics;

public class QuickSortTest {
    private AlgorithmMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new AlgorithmMetrics();
    }

    @Test
    void testSortRandomArray() {
        QuickSort sorter = new QuickSort(metrics);
        int[] arr = {5, 2, 8, 1, 9, 3};
        int[] expected = {1, 2, 3, 5, 8, 9};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(metrics.getComparisons() > 0);
        assertTrue(metrics.getSwaps() > 0);
    }

    @Test
    void testSortAlreadySorted() {
        QuickSort sorter = new QuickSort(metrics);
        int[] arr = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
    }

    @Test
    void testSortReverseSorted() {
        QuickSort sorter = new QuickSort(metrics);
        int[] arr = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
    }

    @Test
    void testSortEmptyArray() {
        QuickSort sorter = new QuickSort(metrics);
        int[] arr = {};

        sorter.sort(arr); // Should not throw exception

        assertEquals(0, arr.length);
    }

    @Test
    void testSortSingleElement() {
        QuickSort sorter = new QuickSort(metrics);
        int[] arr = {42};
        int[] expected = {42};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
    }

    @Test
    void testSortWithDuplicates() {
        QuickSort sorter = new QuickSort(metrics);
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        int[] expected = {1, 1, 2, 3, 3, 4, 5, 5, 6, 9};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
    }
}