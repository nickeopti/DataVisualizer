package statistics;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicklas Boserup
 */
public class StatisticalValues {
    
    public static double sum(List<Point> list) {
        double sum = 0;
        for(Point p : list)
            sum += p.y;
        return sum;
    }
    
    public static double average(List<Point> list) {
        return sum(list)/list.size();
    }
    
    public static double median(List<Point> list) {
        List<Point> sortedList = SortPointList.getSortedList(new ArrayList<Point>(list), false);
        int size = sortedList.size();
        if(size < 1) return 0;
        return size % 2 == 0 ? sortedList.get(size/2-1).y : sortedList.get(size/2).y;
    }
    
    public static List<Point>[] listPerMinute(List<Point> list) {
        List<Point>[] rawValues = new ArrayList[60*24];
        for(int i = 0; i < 24*60; i++) {
                rawValues[i] = new ArrayList();
        }
        
        for(Point p : list) {
            LocalTime currentTime = LocalDateTime.ofEpochSecond(p.x, 0, ZoneOffset.UTC).toLocalTime();
            rawValues[currentTime.getHour()*60 + currentTime.getMinute()].add(new Point((currentTime.getHour()*60 + currentTime.getMinute()), p.y));
        }
        
        return rawValues;
    }
    
    public static List<Double>[] listPerWeekDay(List<Point> list) {
        List<Double>[] rawValues = new ArrayList[7];
        for(int i = 0; i < 7; i++) {
                rawValues[i] = new ArrayList();
        }
        
        for(Point p : list) {
            LocalDateTime currentTime = LocalDateTime.ofEpochSecond(p.x, 0, ZoneOffset.UTC);
            rawValues[currentTime.getDayOfWeek().getValue()-1].add(p.y);
        }
        
        return rawValues;
    }
    
    public static List<Point> filterNaNs(List<Point> list) {
        Point p = null;
        for(int i = 0; i < list.size(); i++) {
            if(p == null) continue;
            if( p.y == Double.NaN) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }
    
}
