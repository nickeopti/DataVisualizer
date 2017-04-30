package datavisualizer;

import graphing.Graph;
import graphing.Plotter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import statistics.GoodOldWay;
import statistics.TimeAndDateFilterList;

/**
 * @author Nicklas Boserup
 */
public class MainUI extends BorderPane {
    
    private Graph graph;
    public ObservableList<TimeAndDateFilterList.DateRange> excludedDates;
    public ObservableList<TimeAndDateFilterList.TimeRange> excludedTimeIntervals;
    
    public MainUI() {
        super();
        graph = convenient();
        
        excludedDates = FXCollections.observableArrayList();
        excludedTimeIntervals = FXCollections.observableArrayList();
        
        setCenter(graph.pane);
        setTop(setupSelectionPanel());
    }
    
    private Graph convenient() {
        Graph g = new Graph();
        
        Plotter plot = new Plotter();
        g.getPlots().add(plot); //Serioulsy wierd behaviour... This needs to be BEFORE it is modified! I got no clue why, whatsoever.
        
        Stop[] stops = {new Stop(0, Color.GREEN), new Stop(0.7, Color.YELLOW), new Stop(1, Color.RED)};
        plot.plot.setStroke(new LinearGradient(1, 1, 1, 0, true, CycleMethod.NO_CYCLE, stops));
        
        try {
            plot.dataPoints.setAll(GoodOldWay.readValuesFromFile());
        } catch (IOException ex) {
            Logger.getLogger(DataVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return g;
    }
    
    public Graph getGraph() {
        return graph;
    }
    
    private HBox setupSelectionPanel() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(5, 10, 5, 10));
        
        /*** Weekdays toggle buttons ***/
        ToggleButton mon = new ToggleButton("Man");
        mon.setFocusTraversable(false);
        mon.setSelected(true);
        ToggleButton tue = new ToggleButton("Tir");
        tue.setFocusTraversable(false);
        tue.setSelected(true);
        ToggleButton wed = new ToggleButton("Ons");
        wed.setFocusTraversable(false);
        wed.setSelected(true);
        ToggleButton thu = new ToggleButton("Tor");
        thu.setFocusTraversable(false);
        thu.setSelected(true);
        ToggleButton fri = new ToggleButton("Fre");
        fri.setFocusTraversable(false);
        fri.setSelected(true);
        ToggleButton sat = new ToggleButton("Lør");
        sat.setFocusTraversable(false);
        sat.setSelected(false);
        ToggleButton son = new ToggleButton("Søn");
        son.setFocusTraversable(false);
        son.setSelected(false);
        ToggleButton all = new ToggleButton("I alt");
        all.setFocusTraversable(false);
        all.setSelected(false);
        ToggleButton combined = new ToggleButton("Kombineret");
        combined.setFocusTraversable(false);
        combined.setSelected(true);
        
        SegmentedButton weekDaysSegment = new SegmentedButton(mon, tue, wed, thu, fri, sat, son, all, combined);
        weekDaysSegment.setToggleGroup(null);
        weekDaysSegment.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
        
        box.getChildren().add(weekDaysSegment);
        
        /*** Excluded days control ***/
        VBox exclDatesList = new VBox();
        exclDatesList.setPadding(new Insets(5));
        
        Button addExclDate = new Button("Tilføj");
        VBox.setMargin(addExclDate, new Insets(5, 0, 0, 0));
        addExclDate.setOnAction(oa -> {
            DateRangeView drv = new DateRangeView();
            drv.close.setOnMouseClicked(me -> {
                exclDatesList.getChildren().remove(drv);
            });
            exclDatesList.getChildren().add(exclDatesList.getChildren().size()-1, drv);
        });
        exclDatesList.getChildren().add(addExclDate);
        exclDatesList.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            List<TimeAndDateFilterList.DateRange> list = new ArrayList<>();
            for(Node n : exclDatesList.getChildren()) {
                if(n != null && n instanceof DateRangeView) {
                    DateRangeView drv = (DateRangeView) n;
                    TimeAndDateFilterList.DateRange dr;
                    if(drv.startDate.isBefore(drv.endDate))
                        dr = new TimeAndDateFilterList.DateRange(drv.startDate, drv.endDate);
                    else
                        dr = new TimeAndDateFilterList.DateRange(drv.endDate, drv.startDate);
                    list.add(dr);
                }
            }
            excludedDates.setAll(list);
        });
        
        PopOver exclDatesPop = new PopOver(exclDatesList);
        exclDatesPop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        exclDatesPop.setTitle("Excluderede datoer");
        exclDatesPop.setDetachable(false);
        
        SVGPath downArrow0 = new SVGPath();
        downArrow0.setScaleX(0.4); downArrow0.setScaleY(0.4); downArrow0.setTranslateY(2);
        downArrow0.setContent("m 0.99414062,1039.8574 a 0.50005,0.50005 0 0 0 -0.34765624,0.8574 L 12,1052.0684 23.353516,1040.7148 a 0.50005,0.50005 0 1 0 -0.707032,-0.707 L 12,1050.6543 1.3535156,1040.0078 a 0.50005,0.50005 0 0 0 -0.35937498,-0.1504 z");
        Button exclDates = new Button("Excl datoer", downArrow0);
        exclDates.setGraphicTextGap(0);
        exclDates.setContentDisplay(ContentDisplay.RIGHT);
        exclDates.setFocusTraversable(false);
        exclDates.setOnAction(oa -> {
            if(exclDatesPop.isShowing()) {
                exclDatesPop.hide();
            } else {
                exclDatesPop.show(exclDates);
            }
        });
        addExclDate.fire();
        
        box.getChildren().add(exclDates);
        
        /*** Excluded time-intervals control ***/
        VBox exclTimesList = new VBox();
        exclTimesList.setPadding(new Insets(5));
        
        Button addExclTime = new Button("Tilføj");
        VBox.setMargin(addExclTime, new Insets(5, 0, 0, 0));
        addExclTime.setOnAction(oa -> {
            TimeRangeView trv = new TimeRangeView();
            trv.close.setOnMouseClicked(me -> {
                exclTimesList.getChildren().remove(trv);
            });
            exclTimesList.getChildren().add(exclTimesList.getChildren().size()-1, trv);
        });
        exclTimesList.getChildren().add(addExclTime);
        exclTimesList.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            List<TimeAndDateFilterList.TimeRange> list = new ArrayList<>();
            for(Node n : exclTimesList.getChildren()) {
                if(n != null && n instanceof TimeRangeView) {
                    TimeRangeView trv = (TimeRangeView) n;
                    TimeAndDateFilterList.TimeRange tr;
                    if(trv.startTime.isBefore(trv.endTime))
                        tr = new TimeAndDateFilterList.TimeRange(trv.startTime, trv.endTime);
                    else
                        tr = new TimeAndDateFilterList.TimeRange(trv.endTime, trv.startTime);
                    list.add(tr);
                }
            }
            excludedTimeIntervals.setAll(list);
        });
        
        PopOver exclTimesPop = new PopOver(exclTimesList);
        exclTimesPop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        exclTimesPop.setTitle("Excluderede tidsintervaller");
        exclTimesPop.setDetachable(false);
        
        SVGPath downArrow1 = new SVGPath();
        downArrow1.setScaleX(0.4); downArrow1.setScaleY(0.4); downArrow1.setTranslateY(2);
        downArrow1.setContent("m 0.99414062,1039.8574 a 0.50005,0.50005 0 0 0 -0.34765624,0.8574 L 12,1052.0684 23.353516,1040.7148 a 0.50005,0.50005 0 1 0 -0.707032,-0.707 L 12,1050.6543 1.3535156,1040.0078 a 0.50005,0.50005 0 0 0 -0.35937498,-0.1504 z");
        Button exclTimes = new Button("Excl tider", downArrow1);
        exclTimes.setGraphicTextGap(0);
        exclTimes.setContentDisplay(ContentDisplay.RIGHT);
        exclTimes.setFocusTraversable(false);
        exclTimes.setOnAction(oa -> {
            if(exclTimesPop.isShowing()) {
                exclTimesPop.hide();
            } else {
                exclTimesPop.show(exclTimes);
            }
        });
        addExclTime.fire();
        
        box.getChildren().add(exclTimes);
        
        return box;
    }
    
}
