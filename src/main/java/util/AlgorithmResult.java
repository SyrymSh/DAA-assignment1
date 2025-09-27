package util;

public class AlgorithmResult {
    public int n;
    public String algorithmName;
    public long timeNs;
    public long comparisons;
    public long swaps;
    public int maxDepth;

    // Optional: Add constructor for convenience
    public AlgorithmResult(int n, String algorithmName, long timeNs,
                           long comparisons, long swaps, int maxDepth) {
        this.n = n;
        this.algorithmName = algorithmName;
        this.timeNs = timeNs;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.maxDepth = maxDepth;
    }

    // Default constructor
    public AlgorithmResult() {}
}