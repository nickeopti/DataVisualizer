package datavisualizer;

import graphing.Graph;
import graphing.Plotter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
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

/**
 * @author Nicklas Boserup
 */
public class MainUI extends BorderPane {
    
    private Graph graph;
    
    public MainUI() {
        super();
        graph = convenient();
        
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
        
        /*** Excluded days and time-intervals ***/
        VBox exclDatesList = new VBox();
        exclDatesList.setPadding(new Insets(5));
        
        Button addExclDate = new Button("Tilføj");
        VBox.setMargin(addExclDate, new Insets(5, 0, 0, 0));
        addExclDate.setOnAction(oa -> {
            DateRangeView drv = new DateRangeView();
            drv.close.setOnMouseClicked(me -> {
                exclDatesList.getChildren().remove(drv);
                //Implement removing constraints from the data controller
            });
            //Implement adding constraints to the data controller
            exclDatesList.getChildren().add(exclDatesList.getChildren().size()-1, drv);
        });
        exclDatesList.getChildren().add(addExclDate);
        
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
        
        return box;
    }
    
}
