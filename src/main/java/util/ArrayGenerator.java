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

    public static Point[] generateRandomPoints(int count, double minX, double maxX, double minY, double maxY) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = Point.random(minX, maxX, minY, maxY);
        }
        return points;
    }

    /**
     * Generate points with known closest pair for testing
     */
    public static Point[] generatePointsWithKnownClosest(int count, Point closest1, Point closest2) {
        Point[] points = new Point[count];
        points[0] = closest1;
        points[1] = closest2;

        for (int i = 2; i < count; i++) {
            double x = closest1.x + 2.0 + Math.random() * 10.0;
            double y = closest1.y + 2.0 + Math.random() * 10.0;
            points[i] = new Point(x, y);
        }

        // Shuffle the array
        for (int i = count - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Point temp = points[i];
            points[i] = points[j];
            points[j] = temp;
        }

        return points;
    }

    /**
     * Generate adversarial case for closest pair (all points in a horizontal line)
     */
    public static Point[] generateHorizontalLinePoints(int count, double y) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new Point(i * 1.0, y);
        }
        return points;
    }
}