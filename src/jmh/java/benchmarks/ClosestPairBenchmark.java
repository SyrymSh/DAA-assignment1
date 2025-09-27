package jmh.java.benchmarks;

import algorithms.ClosestPair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import util.ArrayGenerator;
import util.Point;

import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for Closest Pair algorithm
 * Compares divide-and-conquer vs brute force approaches
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(1) // Reduce forks due to longer execution time
@State(Scope.Benchmark)
public class ClosestPairBenchmark {

    @Param({"100", "500", "1000", "2000", "5000"})
    private int pointsCount;

    private Point[] randomPoints;
    private Point[] clusteredPoints;
    private Point[] gridPoints;

    @Setup
    public void setup() {
        randomPoints = ArrayGenerator.generateRandomPoints(pointsCount, 0, 1000, 0, 1000);
        clusteredPoints = generateClusteredPoints(pointsCount);
        gridPoints = generateGridPoints(pointsCount);
    }

    private Point[] generateClusteredPoints(int count) {
        Point[] points = new Point[count];
        // Create 5 clusters
        Point[] centers = {
                new Point(100, 100), new Point(400, 400),
                new Point(700, 700), new Point(200, 600), new Point(600, 200)
        };

        for (int i = 0; i < count; i++) {
            Point center = centers[i % centers.length];
            double x = center.x + (Math.random() - 0.5) * 50;
            double y = center.y + (Math.random() - 0.5) * 50;
            points[i] = new Point(x, y);
        }
        return points;
    }

    private Point[] generateGridPoints(int count) {
        Point[] points = new Point[count];
        int gridSize = (int) Math.ceil(Math.sqrt(count));
        double spacing = 1000.0 / gridSize;

        for (int i = 0; i < count; i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            double x = col * spacing + Math.random() * spacing * 0.1;
            double y = row * spacing + Math.random() * spacing * 0.1;
            points[i] = new Point(x, y);
        }
        return points;
    }

    @Benchmark
    public void closestPairDivideConquerRandom(Blackhole blackhole) {
        Point[] pointsCopy = randomPoints.clone();
        ClosestPair closestPair = new ClosestPair(new util.AlgorithmMetrics());
        ClosestPair.Result result = closestPair.findClosestPair(pointsCopy);
        blackhole.consume(result);
    }

    @Benchmark
    public void closestPairDivideConquerClustered(Blackhole blackhole) {
        Point[] pointsCopy = clusteredPoints.clone();
        ClosestPair closestPair = new ClosestPair(new util.AlgorithmMetrics());
        ClosestPair.Result result = closestPair.findClosestPair(pointsCopy);
        blackhole.consume(result);
    }

    @Benchmark
    public void closestPairDivideConquerGrid(Blackhole blackhole) {
        Point[] pointsCopy = gridPoints.clone();
        ClosestPair closestPair = new ClosestPair(new util.AlgorithmMetrics());
        ClosestPair.Result result = closestPair.findClosestPair(pointsCopy);
        blackhole.consume(result);
    }

    @Benchmark
    public void closestPairBruteForceRandom(Blackhole blackhole) {
        Point[] pointsCopy = randomPoints.clone();
        ClosestPair closestPair = new ClosestPair(new util.AlgorithmMetrics());
        ClosestPair.Result result = closestPair.bruteForce(pointsCopy);
        blackhole.consume(result);
    }

    /**
     * Benchmark to verify correctness for small n
     */
    @Benchmark
    public void closestPairCorrectnessCheck(Blackhole blackhole) {
        Point[] pointsCopy = randomPoints.clone();
        ClosestPair closestPair = new ClosestPair(new util.AlgorithmMetrics());
        ClosestPair.Result dqResult = closestPair.findClosestPair(pointsCopy);

        // Verify with brute force for small n (as per assignment requirements)
        if (pointsCount <= 2000) {
            ClosestPair.Result bruteResult = closestPair.bruteForce(pointsCopy);
            if (Math.abs(dqResult.distance - bruteResult.distance) > 1e-10) {
                throw new RuntimeException("Correctness check failed!");
            }
        }

        blackhole.consume(dqResult);
    }
}