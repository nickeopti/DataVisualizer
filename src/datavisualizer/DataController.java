package datavisualizer;

import datainput.DataInput;
import datainput.NymarkenDataInput;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import statistics.Extremes;
import statistics.Point;
import statistics.SortPointList;
import statistics.StatisticalValues;
import statistics.TimeAndDateFilterList;

/**
 * @author Nicklas Boserup
 */
public class DataController {
    
    private List<Point> rawData;
    private final MainUI ui;
    private final TimeAndDateFilterList filter;
    
    public DataController(MainUI mainUI) {
        ui = mainUI;
        rawData = readDataInput();
        filter = new TimeAndDateFilterList();
    }
    
    private List<Point> readDataInput() {
        DataInput input = new NymarkenDataInput("data-5.csv");
        return input.readDataPointList();
    }
    
    public List<Point> getRawData() {
        return rawData;
    }

    public void setRawData(List<Point> rawData) {
        this.rawData = rawData;
    }
    
    public List<Point> getComputedData() {
        filter.dateInterval = ui.dateRange;
        filter.timeInterval = ui.timeRange;
        
        filter.excludedDays.clear();
        if(!ui.mon.isSelected())
            filter.excludedDays.add(DayOfWeek.MONDAY);
        if(!ui.tue.isSelected())
            filter.excludedDays.add(DayOfWeek.TUESDAY);
        if(!ui.wed.isSelected())
            filter.excludedDays.add(DayOfWeek.WEDNESDAY);
        if(!ui.thu.isSelected())
            filter.excludedDays.add(DayOfWeek.THURSDAY);
        if(!ui.fri.isSelected())
            filter.excludedDays.add(DayOfWeek.FRIDAY);
        if(!ui.sat.isSelected())
            filter.excludedDays.add(DayOfWeek.SATURDAY);
        if(!ui.sun.isSelected())
            filter.excludedDays.add(DayOfWeek.SUNDAY);
        
        filter.excludedDates.clear();
        filter.excludedDates.addAll(ui.excludedDates);
        
        
        List<Point> filteredData = filter.getFilteredList(rawData);
        
        List<Point>[] minuteData = StatisticalValues.listPerMinute(filteredData);
        List<Point> computedMinuteData = new ArrayList<>();
        for(List<Point> l : minuteData) {
            if(!l.isEmpty()) {
                switch(ui.statSel.getSelectionModel().getSelectedIndex()) {
                    case 0: //Average
                        computedMinuteData.add(new Point(l.get(0).x , StatisticalValues.average(l)));
                        break;
                    case 1: //Median
                        computedMinuteData.add(new Point(l.get(0).x , StatisticalValues.median(l)));
                        break;
                    case 2: //Maximum
                        computedMinuteData.add(Extremes.extremePointY(l, Extremes.Extreme.MAXIMUM));
                        break;
                    case 3: //Minimum
                        computedMinuteData.add(Extremes.extremePointY(l, Extremes.Extreme.MINIMUM));
                        break;
                }
            }
        }
        
        System.out.println("maks: " + Extremes.extremePointY(computedMinuteData, Extremes.Extreme.MAXIMUM).y);
        
        //Moving average?
        
        List<Point> sortedList = SortPointList.getSortedList(computedMinuteData, true);
        
        return sortedList;
    }
    
}
