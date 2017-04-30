package statistics;

import java.util.List;

/**
 * @author Nicklas Boserup
 */
public class SortPointList {
    
    public static List<Point> getSortedList(List<Point> list, boolean sortedX) {
        if(sortedX)
            list.sort((Point p1, Point p2) -> p1.x < p2.x ? -1 : (p1.x == p2.x ? 0 : 1));
        else
            list.sort((Point p1, Point p2) -> p1.y < p2.y ? -1 : (p1.y == p2.y ? 0 : 1));
        return list;
    }
    
}
