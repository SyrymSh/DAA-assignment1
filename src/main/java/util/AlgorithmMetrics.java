package util;

public class AlgorithmMetrics {
    private long comparisons = 0;
    private long swaps = 0;
    private int maxDepth = 0;
    private int currentDepth = 0;
    private long startTime;
    private long endTime;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public void recordComparison() { comparisons++; }
    public void recordSwap() { swaps++; }

    public void enterRecursion() {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }

    public void exitRecursion() {
        currentDepth--;
    }

    // Getters
    public long getElapsedTime() { return endTime - startTime; }
    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public int getMaxDepth() { return maxDepth; }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        maxDepth = 0;
        currentDepth = 0;
        startTime = 0;
        endTime = 0;
    }
}