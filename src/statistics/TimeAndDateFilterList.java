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
        List<Point> filteredList = new ArrayList<>(list);
        Point p = null;
        outer: for(Iterator<Point> iterator = filteredList.iterator(); iterator.hasNext(); p = iterator.next()) {
            if(p == null) continue;
            LocalDateTime pDate = LocalDateTime.ofEpochSecond((long)p.x, 0, ZoneOffset.UTC); //Works for seconds up to 2^53 - which is way longer than necessary! (ca. year 30.000)
            
            if(pDate.toLocalDate().isBefore(dateInterval.startDate) || pDate.toLocalDate().isAfter(dateInterval.endDate)) {
                iterator.remove();
                continue;
            }
            
            if(pDate.toLocalTime().isBefore(timeInterval.startTime) || pDate.toLocalTime().isAfter(timeInterval.endTime)) {
                iterator.remove();
                continue;
            }
            
            for(DateRange dr : excludedDates) {
                if(!(pDate.toLocalDate().isBefore(dr.startDate) || pDate.toLocalDate().isAfter(dr.endDate))) {
                    iterator.remove();
                    continue outer;
                }
            }
            for(DayOfWeek dw : excludedDays) {
                if(pDate.getDayOfWeek() == dw) {
                    iterator.remove();
                    continue outer;
                }
            }
        }
        
        return filteredList;
    }
    
}
