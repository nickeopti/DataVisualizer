package graphing;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import statistics.Extremes;
import statistics.Point;

/**
 * @author Nicklas Boserup
 */
public class Graph {
    
    public GridPane pane;
    private ZoomScrollBar hScrollBar, vScrollBar;
    private ObservableList<Plotter> plots;
    private StackPane plotPane;
    private DoubleProperty minXVal, maxXVal, minYVal, maxYVal;
    
    public Graph() {
        pane = new GridPane();
        
        minXVal = new SimpleDoubleProperty();
        maxXVal = new SimpleDoubleProperty();
        minYVal = new SimpleDoubleProperty();
        maxYVal = new SimpleDoubleProperty();
        
        hScrollBar = new ZoomScrollBar(0, 100, 20, 50);
        hScrollBar.isHorizontal.set(true);
        hScrollBar.minimumValue.bind(minXVal);
        hScrollBar.maximumValue.bind(maxXVal);
        pane.add(hScrollBar, 1, 1);
        
        vScrollBar = new ZoomScrollBar(0, 100, 20, 50);
        vScrollBar.isHorizontal.set(false);
        vScrollBar.minimumValue.bind(minYVal);
        vScrollBar.maximumValue.bind(maxYVal);
        pane.add(vScrollBar, 0, 0);
        
        plotPane = new StackPane();
        plotPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        GridPane.setHgrow(plotPane, Priority.ALWAYS);
        GridPane.setVgrow(plotPane, Priority.ALWAYS);
        pane.add(plotPane, 1, 0);
        
        plots = FXCollections.observableArrayList();
        plots.addListener((ListChangeListener.Change<? extends Plotter> c) -> {
            while(c.next()) {
                for(Plotter p : c.getRemoved()) {
                    pane.getChildren().remove(p);
                    p.minimumXValue.unbindBidirectional(hScrollBar.currentMinValue);
                    p.maximumXValue.unbindBidirectional(hScrollBar.currentMaxValue);
                    p.minimumYValue.unbindBidirectional(vScrollBar.currentMinValue);
                    p.maximumYValue.unbindBidirectional(vScrollBar.currentMaxValue);
                }
                for(Plotter p : c.getAddedSubList()) {
                    plotPane.getChildren().add(p);
                    p.minimumXValue.bindBidirectional(hScrollBar.currentMinValue);
                    p.maximumXValue.bindBidirectional(hScrollBar.currentMaxValue);
                    p.minimumYValue.bindBidirectional(vScrollBar.currentMinValue);
                    p.maximumYValue.bindBidirectional(vScrollBar.currentMaxValue);
                    
                    p.dataPoints.addListener((ListChangeListener.Change<? extends Point> c1) -> {
                        if(p.dataPoints != null) {
                            minXVal.set(Extremes.extremePoint(p.dataPoints, Extremes.Extreme.MINIMUM, Extremes.Coordinate.X).x);
                            maxXVal.set(Extremes.extremePoint(p.dataPoints, Extremes.Extreme.MAXIMUM, Extremes.Coordinate.X).x);
                            minYVal.set(Extremes.extremePoint(p.dataPoints, Extremes.Extreme.MINIMUM, Extremes.Coordinate.Y).y);
                            maxYVal.set(Extremes.extremePoint(p.dataPoints, Extremes.Extreme.MAXIMUM, Extremes.Coordinate.Y).y);
                            System.out.println("maxXVal: " + hScrollBar.minimumValue);
                        }
                    });
                }
            }
        });
    }
    
    public ObservableList<Plotter> getPlots() {
        return plots;
    }
    
}
