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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import statistics.Extremes;
import statistics.Point;

/**
 * @author Nicklas Boserup
 */
public class Graph {
    
    public GridPane pane;
    public ZoomScrollBar hScrollBar, vScrollBar;
    private final ObservableList<Plotter> plots;
    private StackPane plotPane;
    private DoubleProperty minXVal, maxXVal, minYVal, maxYVal;
    
    public Graph() {
        pane = new GridPane();
        
        minXVal = new SimpleDoubleProperty();
        maxXVal = new SimpleDoubleProperty();
        minYVal = new SimpleDoubleProperty();
        maxYVal = new SimpleDoubleProperty();
        
        hScrollBar = new ZoomScrollBar();
        hScrollBar.isHorizontal.set(true);
        hScrollBar.minimumValue.bind(minXVal);
        hScrollBar.maximumValue.bind(maxXVal);
        pane.add(hScrollBar, 1, 1);
        
        vScrollBar = new ZoomScrollBar();
        vScrollBar.isHorizontal.set(false);
        vScrollBar.minimumValue.bind(minYVal);
        vScrollBar.maximumValue.bind(maxYVal);
        pane.add(vScrollBar, 0, 0);
        
        plotPane = new StackPane();
        setupPlotPaneScrollHandling();
        plotPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        GridPane.setHgrow(plotPane, Priority.ALWAYS);
        GridPane.setVgrow(plotPane, Priority.ALWAYS);
        pane.add(plotPane, 1, 0);
        
        plots = FXCollections.observableArrayList();
        plots.addListener((ListChangeListener.Change<? extends Plotter> c) -> {
            while(c.next()) {
                for(Plotter p : c.getRemoved()) {
                    plotPane.getChildren().remove(p);
                    p.minimumXValue.unbindBidirectional(hScrollBar.currentMinValue);
                    p.maximumXValue.unbindBidirectional(hScrollBar.currentMaxValue);
                    p.minimumYValue.unbindBidirectional(vScrollBar.currentMinValue);
                    p.maximumYValue.unbindBidirectional(vScrollBar.currentMaxValue);
                    
                    setBarValues();
                }
                for(Plotter p : c.getAddedSubList()) {
                    plotPane.getChildren().add(p);
                    p.minimumXValue.bindBidirectional(hScrollBar.currentMinValue);
                    p.maximumXValue.bindBidirectional(hScrollBar.currentMaxValue);
                    p.minimumYValue.bindBidirectional(vScrollBar.currentMinValue);
                    p.maximumYValue.bindBidirectional(vScrollBar.currentMaxValue);
                    p.requestLayout();
                    
                    
                    p.dataPoints.addListener((ListChangeListener.Change<? extends Point> c1) -> {
                        setBarValues();
                    });
                }
            }
        });
        
        //getChildren().add(pane);
    }
    
    private void setBarValues() {
        double minX = 0, maxX = 0, minY = 0, maxY = 0;
        for(Plotter pl : plots) {
            minX = Extremes.extremePoint(pl.dataPoints, Extremes.Extreme.MINIMUM, Extremes.Coordinate.X).x;
            maxX = Extremes.extremePoint(pl.dataPoints, Extremes.Extreme.MAXIMUM, Extremes.Coordinate.X).x;
            minY = Extremes.extremePoint(pl.dataPoints, Extremes.Extreme.MINIMUM, Extremes.Coordinate.Y).y;
            maxY = Extremes.extremePoint(pl.dataPoints, Extremes.Extreme.MAXIMUM, Extremes.Coordinate.Y).y;
        }
        minXVal.set(minX);
        maxXVal.set(maxX);
        minYVal.set(minY);
        maxYVal.set(maxY);
        hScrollBar.currentMinValue.set(minXVal.get());
        hScrollBar.currentMaxValue.set(maxXVal.get());
        vScrollBar.currentMinValue.set(minYVal.get());
        vScrollBar.currentMaxValue.set(maxYVal.get());
    }
    
    private void setupPlotPaneScrollHandling() {
        plotPane.setOnScroll(se -> {
            if(se.isControlDown()) {
                
            } else {
                hScrollBar.scroll(-se.getDeltaX()/hScrollBar.getZoomFactor());
                vScrollBar.scroll(se.getDeltaY()/vScrollBar.getZoomFactor());
            }
        });
        plotPane.setOnZoom(ze -> {
            
        });
    }
    
    public ObservableList<Plotter> getPlots() {
        return plots;
    }
    
}
