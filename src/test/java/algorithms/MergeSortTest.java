package algorithms;
import util.AlgorithmMetrics;
import static org.junit.jupiter.api.Assertions.*;

public class MergeSortTest {

    @Test
    public void testSortRandomArray() {
        // SETUP
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        MergeSort sorter = new MergeSort(metrics);
        int[] arr = {5, 2, 8, 1, 9};
        int[] expected = {1, 2, 5, 8, 9};
        // EXECUTE
        sorter.sort(arr);
        // VERIFY
        assertArrayEquals(expected, arr);
        }
    @Test
    public void testSortEmptyArray() {
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        MergeSort sorter = new MergeSort(metrics);
        int[] arr = {};

        sorter.sort(arr); // Should not crash

        assertEquals(0, arr.length);
        }

    @Test
    public void testSortAlreadySorted() {
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        MergeSort sorter = new MergeSort(metrics);
        int[] arr = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
    }
}

