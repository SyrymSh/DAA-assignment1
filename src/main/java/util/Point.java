package util;

/**
 * Immutable 2D point class for Closest Pair algorithm
 */
public class Point implements Comparable<Point> {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Euclidean distance between two points
     */
    public double distanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Compare by x-coordinate for sorting
     */
    @Override
    public int compareTo(Point other) {
        return Double.compare(this.x, other.x);
    }

    /**
     * Compare by y-coordinate
     */
    public int compareByY(Point other) {
        return Double.compare(this.y, other.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) * 31 + Double.hashCode(y);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

    /**
     * Create a random point within specified bounds
     */
    public static Point random(double minX, double maxX, double minY, double maxY) {
        double x = minX + Math.random() * (maxX - minX);
        double y = minY + Math.random() * (maxY - minY);
        return new Point(x, y);
    }
}