package statistics;

import java.util.List;

/**
 * @author Nicklas Boserup
 */
public class Extremes {
    
    public enum Coordinate {
        X, Y
    }
    public enum Extreme {
        MINIMUM,
        MAXIMUM
    }
    
    public static Point extremePoint(List<Point> points, Extreme ex, Coordinate coordinate) {
        double value = ex == Extreme.MAXIMUM ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Point point = null;
        
        for(Point p : points) {
            if(ex == Extreme.MAXIMUM ? (coordinate == Coordinate.X ? p.x > value : p.y > value) :
                    (coordinate == Coordinate.X ? p.x < value : p.y < value)) {
                value = coordinate == Coordinate.X ? p.x : p.y;
                point = p;
            }
        }
        return point;
    }
    
    public static Point extremePointX(List<Point> points, Extreme ex) {
        long value = ex == Extreme.MAXIMUM ? Long.MIN_VALUE : Long.MAX_VALUE;
        Point point = null;
        
        for(Point p : points) {
            if(ex == Extreme.MAXIMUM ? p.x > value : p.x < value) {
                value = p.x;
                point = p;
            }
        }
        return point;
    }
    
    public static Point extremePointY(List<Point> points, Extreme ex) {
        double value = ex == Extreme.MAXIMUM ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Point point = null;
        
        for(Point p : points) {
            if(ex == Extreme.MAXIMUM ? p.y > value : p.y < value) {
                value = p.y;
                point = p;
            }
        }
        return point;
    }
    
}
