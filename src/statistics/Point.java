package statistics;

/**
 * @author Nicklas Boserup
 */
public class Point {
    
    public long x;
    public double y;
    
    public Point(long x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Point() {
        this(0, 0);
    }
    
    @Override
    public String toString() {
        return "(" + x + ",\t" + y + ")";
    }
    
}
