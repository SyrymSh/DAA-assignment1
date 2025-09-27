package util;

import java.util.Random;
import java.util.Arrays;

public class ArrayGenerator {
    private static final Random random = new Random();

    public static int[] generateRandom(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size * 10); // Larger range for more diversity
        }
        return arr;
    }

    public static int[] generateSorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        return arr;
    }

    public static int[] generateReverseSorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = size - i - 1;
        }
        return arr;
    }

    public static int[] generateDuplicates(int size, int uniqueValues) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(uniqueValues);
        }
        return arr;
    }

    // For testing adversarial cases for QuickSort
    public static int[] generateAdversarialQuickSort(int size) {
        int[] arr = new int[size];
        // Create array that's already sorted (worst case for naive QuickSort)
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        return arr;
    }
}