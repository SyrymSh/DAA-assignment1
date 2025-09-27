package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import util.AlgorithmMetrics;
import util.Point;
import util.ArrayGenerator;
import java.util.Arrays;

public class ClosestPairTest {
    private AlgorithmMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new AlgorithmMetrics();
    }

    @Test
    void testBasicClosestPair() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Simple test case with known closest pair
        Point[] points = {
                new Point(0, 0),
                new Point(1, 1),
                new Point(3, 3),
                new Point(5, 5)
        };

        ClosestPairResult result = closestPair.findClosestPair(points);

        assertEquals(0.0, points[0].distanceTo(points[0]), 1e-10);
        assertTrue(result.distance > 0);
        assertEquals(Math.sqrt(2), result.distance, 1e-10); // Distance between (0,0) and (1,1)
    }

    @Test
    void testClosestPairComparedToBruteForce() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Test multiple random point sets
        for (int size : new int[]{10, 20, 50, 100}) {
            Point[] points = ArrayGenerator.generateRandomPoints(size, 0, 100, 0, 100);

            ClosestPairResult dqResult = closestPair.findClosestPair(points);
            ClosestPairResult bruteResult = closestPair.bruteForce(points);

            assertEquals(bruteResult.distance, dqResult.distance, 1e-10,
                    "Divide-and-conquer failed for size " + size);
        }
    }

    @Test
    void testClosestPairWithKnownAnswer() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Create two points that should be the closest pair
        Point closest1 = new Point(1.0, 1.0);
        Point closest2 = new Point(1.1, 1.1);
        double expectedDistance = closest1.distanceTo(closest2);

        Point[] points = ArrayGenerator.generatePointsWithKnownClosest(100, closest1, closest2);

        ClosestPairResult result = closestPair.findClosestPair(points);

        assertEquals(expectedDistance, result.distance, 1e-10);
    }

    @Test
    void testClosestPairHorizontalLine() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // All points on a horizontal line - closest pairs are adjacent points
        Point[] points = ArrayGenerator.generateHorizontalLinePoints(10, 5.0);

        ClosestPairResult result = closestPair.findClosestPair(points);

        assertEquals(1.0, result.distance, 1e-10); // Distance between adjacent points
    }

    @Test
    void testClosestPairVerticalLine() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // All points on a vertical line
        Point[] points = new Point[10];
        for (int i = 0; i < 10; i++) {
            points[i] = new Point(5.0, i * 1.0);
        }

        ClosestPairResult result = closestPair.findClosestPair(points);

        assertEquals(1.0, result.distance, 1e-10); // Distance between adjacent points
    }

    @Test
    void testClosestPairDuplicatePoints() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Include duplicate points - distance should be zero
        Point[] points = {
                new Point(1, 1),
                new Point(1, 1), // Duplicate
                new Point(2, 2),
                new Point(3, 3)
        };

        ClosestPairResult result = closestPair.findClosestPair(points);

        assertEquals(0.0, result.distance, 1e-10); // Distance between duplicate points
    }

    @Test
    void testClosestPairLargeDataset() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Test with larger dataset to verify O(n log n) behavior
        Point[] points = ArrayGenerator.generateRandomPoints(1000, 0, 1000, 0, 1000);

        ClosestPairResult dqResult = closestPair.findClosestPair(points);
        ClosestPairResult bruteResult = closestPair.bruteForce(points);

        assertEquals(bruteResult.distance, dqResult.distance, 1e-10);
    }

    @Test
    void testClosestPairSmallArrays() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Test edge cases with small arrays
        Point[] twoPoints = {
                new Point(0, 0),
                new Point(1, 1)
        };

        ClosestPairResult result2 = closestPair.findClosestPair(twoPoints);
        assertEquals(Math.sqrt(2), result2.distance, 1e-10);

        Point[] threePoints = {
                new Point(0, 0),
                new Point(1, 1),
                new Point(0.5, 0.5)
        };

        ClosestPairResult result3 = closestPair.findClosestPair(threePoints);
        assertEquals(Math.sqrt(0.5), result3.distance, 1e-10); // Distance to (0.5,0.5)
    }

    @Test
    void testClosestPairInvalidInput() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Test null array
        assertThrows(IllegalArgumentException.class, () -> {
            closestPair.findClosestPair(null);
        });

        // Test single point
        Point[] singlePoint = { new Point(1, 1) };
        assertThrows(IllegalArgumentException.class, () -> {
            closestPair.findClosestPair(singlePoint);
        });

        // Test empty array
        Point[] emptyArray = {};
        assertThrows(IllegalArgumentException.class, () -> {
            closestPair.findClosestPair(emptyArray);
        });
    }

    @Test
    void testClosestPairPerformance() {
        ClosestPair closestPair = new ClosestPair(metrics);

        // Test that divide-and-conquer is faster than brute force for large n
        Point[] points = ArrayGenerator.generateRandomPoints(100, 0, 100, 0, 100);

        long startTime = System.nanoTime();
        ClosestPairResult dqResult = closestPair.findClosestPair(points);
        long dqTime = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        ClosestPairResult bruteResult = closestPair.bruteForce(points);
        long bruteTime = System.nanoTime() - startTime;

        // Divide-and-conquer should be faster for n=100
        assertTrue(dqTime < bruteTime,
                "Divide-and-conquer should be faster than brute force");

        assertEquals(bruteResult.distance, dqResult.distance, 1e-10);
    }
}