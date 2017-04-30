package datavisualizer;

import com.sun.javafx.scene.control.skin.SliderSkin;
import datainput.DataInput;
import datainput.NymarkenDataInput;
import datainput.TabSeperatedTextFile;
import datainput.TextFileInput;
import graphing.Graph;
import graphing.Plotter;
import graphing.ZoomScrollBar;
import graphing.ZoomScrollBar;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import statistics.GoodOldWay;
import statistics.Point;
import statistics.SortPointList;
import statistics.StatisticalValues;
import statistics.TimeAndDateFilterList;
import statistics.TimeAndDateFilterList.TimeRange;
import threading.ThreadPoolSingleton;

/**
 * @author Nicklas Boserup
 */
public class DataVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        //setupDataForGraph(g);

        
        MainUI mainUI = new MainUI();
        Scene scene = new Scene(mainUI);
        
        primaryStage.setTitle("Visual Data Analyzer");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(500);
        primaryStage.setOnCloseRequest((e) -> ThreadPoolSingleton.getExecutor().shutdown());
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private void setupDataForGraph(Graph g) {
        DataInput input = new NymarkenDataInput("nyedata.txt");
        List<Point> data = input.readDataPointList();
        
        TimeAndDateFilterList tdfl = new TimeAndDateFilterList();
        tdfl.excludedDays.add(DayOfWeek.SATURDAY);
        tdfl.excludedDays.add(DayOfWeek.SUNDAY);
        tdfl.excludedTimes.add(new TimeAndDateFilterList.TimeRange(LocalTime.of(0, 0), LocalTime.of(6, 0)));
        tdfl.excludedTimes.add(new TimeAndDateFilterList.TimeRange(LocalTime.of(15, 0), LocalTime.of(23, 59)));
        
        
        
        List<Point>[] q = StatisticalValues.listPerMinute(tdfl.getFilteredList(data));
        List<Point> fil = new ArrayList<>();
        for(int i = 1; i < q.length; i++) {
            fil.add(new Point(i, StatisticalValues.median(q[i])));
        }
        g.getPlots().get(0).dataPoints.setAll(fil);
    }
    
}
