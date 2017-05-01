package datavisualizer;

import datainput.DataInput;
import datainput.NymarkenDataInput;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import statistics.GoodOldWay;
import statistics.Point;
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
        /*try {
            return GoodOldWay.readValuesFromFile();
        } catch (IOException ex) {
            Logger.getLogger(DataController.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }*/
        DataInput input = new NymarkenDataInput("nyedata.txt");
        return input.readDataPointList();
    }
    
    public List<Point> getRawData() {
        return rawData;
    }

    public void setRawData(List<Point> rawData) {
        this.rawData = rawData;
    }
    
    public List<Point> getComputedData() {
        System.out.println("Raw List: " + rawData.toString());
        
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
        
        
        List<Point> computedData = filter.getFilteredList(rawData);
        
        //Average, median, min, max, moving average...
        
        System.out.println("Filtered list: " + computedData.toString());
        
        return computedData;
    }
    
}
