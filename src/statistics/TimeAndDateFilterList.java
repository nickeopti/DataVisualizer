package statistics;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nicklas Boserup
 */
public class TimeAndDateFilterList {
    
    public static class DateRange {
        public LocalDate startDate;
        public LocalDate endDate;
        
        public DateRange(LocalDate start, LocalDate end) {
            startDate = start;
            endDate = end;
        }
    }
    
    public static class TimeRange {
        public LocalTime startTime;
        public LocalTime endTime;
        
        public TimeRange(LocalTime start, LocalTime end) {
            startTime = start;
            endTime = end;
        }
    }
    
    public DateRange dateInterval;
    public TimeRange timeInterval;
    public List<DateRange> excludedDates = new ArrayList<>();
    public List<DayOfWeek> excludedDays = new ArrayList<>();
    
    public List<Point> getFilteredList(List<Point> list) {
        List<Point> filteredList = new ArrayList<>();
        for(Point p : list)
            filteredList.add(new Point(p.x, p.y));
        
        Point p;
        outer: for(int i = 0; i < filteredList.size(); i++) {
            p = filteredList.get(i);
            if(p == null) continue;
            LocalDateTime pDate = LocalDateTime.ofEpochSecond(p.x, 0, ZoneOffset.UTC);
            
            if(pDate.toLocalDate().isBefore(dateInterval.startDate) || pDate.toLocalDate().isAfter(dateInterval.endDate)) {
                filteredList.remove(i);
                --i;
                System.out.println("Remove point based on date interval. Epoc " + pDate.toEpochSecond(ZoneOffset.UTC));
                continue;
            }
            
            if(pDate.toLocalTime().isBefore(timeInterval.startTime) || pDate.toLocalTime().isAfter(timeInterval.endTime)) {
                filteredList.remove(i);
                --i;
                continue;
            }
            
            for(DateRange dr : excludedDates) {
                if(!(pDate.toLocalDate().isBefore(dr.startDate) || pDate.toLocalDate().isAfter(dr.endDate))) {
                    filteredList.remove(i);
                    --i;
                    continue outer;
                }
            }
            for(DayOfWeek dw : excludedDays) {
                if(pDate.getDayOfWeek() == dw) {
                    filteredList.remove(i);
                    --i;
                    continue outer;
                }
            }
        }
        
        return filteredList;
    }
    
}
