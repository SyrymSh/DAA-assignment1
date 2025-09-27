package algorithms;

import util.AlgorithmMetrics;
import util.Point;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Closest Pair of Points algorithm using divide-and-conquer
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class ClosestPair {
    private AlgorithmMetrics metrics;

    public ClosestPair(AlgorithmMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Public static result class
     */
    public static class Result {
        public final Point point1;
        public final Point point2;
        public final double distance;

        public Result(Point point1, Point point2, double distance) {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance;
        }

        @Override
        public String toString() {
            if (point1 == null || point2 == null) {
                return "No pair found";
            }
            return String.format("Closest pair: %s and %s, distance: %.6f", point1, point2, distance);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Result that = (Result) obj;
            return Math.abs(this.distance - that.distance) < 1e-10;
        }
    }

    /**
     * Finds the closest pair of points in the given array
     */
    public Result findClosestPair(Point[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("At least two points required");
        }

        metrics.startTimer();

        // Base case: use brute force for small arrays
        if (points.length <= 3) {
            Result result = bruteForceClosestPair(points);
            metrics.stopTimer();
            return result;
        }

        // Step 1: Sort points by x-coordinate
        Point[] pointsByX = points.clone();
        Arrays.sort(pointsByX);
        metrics.recordComparison();

        // Step 2: Recursively find closest pair
        Result result = findClosestPairRecursive(pointsByX);
        metrics.stopTimer();

        return result;
    }

    private Result findClosestPairRecursive(Point[] pointsByX) {
        metrics.enterRecursion();

        int n = pointsByX.length;

        if (n <= 3) {
            Result result = bruteForceClosestPair(pointsByX);
            metrics.exitRecursion();
            return result;
        }

        int mid = n / 2;
        Point midPoint = pointsByX[mid];

        Point[] leftPoints = Arrays.copyOfRange(pointsByX, 0, mid);
        Point[] rightPoints = Arrays.copyOfRange(pointsByX, mid, n);

        Result leftResult = findClosestPairRecursive(leftPoints);
        Result rightResult = findClosestPairRecursive(rightPoints);

        Result minResult = (leftResult.distance <= rightResult.distance) ? leftResult : rightResult;
        double minDistance = minResult.distance;

        Result stripResult = findClosestInStrip(pointsByX, midPoint, minDistance);

        Result finalResult;
        if (stripResult.distance < minDistance) {
            finalResult = stripResult;
        } else {
            finalResult = minResult;
        }

        metrics.exitRecursion();
        return finalResult;
    }

    private Result findClosestInStrip(Point[] pointsByX, Point midPoint, double minDistance) {
        Point[] strip = new Point[pointsByX.length];
        int stripSize = 0;

        for (Point point : pointsByX) {
            if (Math.abs(point.x - midPoint.x) < minDistance) {
                strip[stripSize++] = point;
            }
        }

        if (stripSize < 2) {
            return new Result(null, null, Double.POSITIVE_INFINITY);
        }

        Arrays.sort(strip, 0, stripSize, Comparator.comparingDouble(p -> p.y));
        metrics.recordComparison();

        double closestDistance = minDistance;
        Point closest1 = null;
        Point closest2 = null;

        for (int i = 0; i < stripSize; i++) {
            for (int j = i + 1; j < stripSize && (strip[j].y - strip[i].y) < closestDistance; j++) {
                double distance = strip[i].distanceTo(strip[j]);
                metrics.recordComparison();

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest1 = strip[i];
                    closest2 = strip[j];
                }

                if (j - i >= 7) break;
            }
        }

        if (closest1 == null) {
            return new Result(null, null, Double.POSITIVE_INFINITY);
        }

        return new Result(closest1, closest2, closestDistance);
    }

    private Result bruteForceClosestPair(Point[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("At least two points required");
        }

        Point closest1 = points[0];
        Point closest2 = points[1];
        double minDistance = closest1.distanceTo(closest2);
        metrics.recordComparison();

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double distance = points[i].distanceTo(points[j]);
                metrics.recordComparison();

                if (distance < minDistance) {
                    minDistance = distance;
                    closest1 = points[i];
                    closest2 = points[j];
                }
            }
        }

        return new Result(closest1, closest2, minDistance);
    }

    /**
     * Alternative brute force implementation for verification
     */
    public Result bruteForce(Point[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("At least two points required");
        }

        metrics.startTimer();
        Result result = bruteForceClosestPair(points);
        metrics.stopTimer();
        return result;
    }
}