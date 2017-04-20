package graphing;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import statistics.Point;
import threading.ThreadPoolSingleton;

/**
 * @author Nicklas Boserup
 */
public class Plotter extends Region {
    
    public DoubleProperty minimumXValue, maximumXValue, minimumYValue, maximumYValue;
    public ObservableList<Point> dataPoints;
    public Path plot;
    
    private boolean changeFlag = false;
    
    public Plotter() {
        minimumXValue = new SimpleDoubleProperty(0);
        minimumXValue.addListener((ob, oldVal, newVal) -> {changeFlag = true; requestLayout();});
        maximumXValue = new SimpleDoubleProperty(60);
        maximumXValue.addListener((ob, oldVal, newVal) -> {changeFlag = true; requestLayout();});
        minimumYValue = new SimpleDoubleProperty(0);
        minimumYValue.addListener((ob, oldVal, newVal) -> {changeFlag = true; requestLayout();});
        maximumYValue = new SimpleDoubleProperty(100);
        maximumXValue.addListener((ob, oldVal, newVal) -> {changeFlag = true; requestLayout();});
        dataPoints = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        dataPoints.addListener((ListChangeListener.Change<? extends Point> c) -> {changeFlag = true; requestLayout();});
        
        plot = new Path();
        plot.setStroke(Color.GREEN);
        plot.setStrokeWidth(2);
        
        getChildren().add(plot);
        
        setMinSize(100, 300);
        dataPoints.addListener((Observable c) -> update());
    }

    double lastWidth, lastHeight;
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth(), height = getHeight();
        System.out.println(changeFlag);
        if(lastWidth != width || lastHeight != height || changeFlag) {
            changeFlag = false;
            update();
            lastWidth = width;
            lastHeight = height;
        }
    }
    
    Task<ObservableList<PathElement>>  task;
    public void update() {
        final double width = getWidth(), height = getHeight();
        plot.setClip(new Rectangle(width, height));
        
        if(task != null)
            task.cancel();
        task = new Task<ObservableList<PathElement>>() {
            @Override
            protected ObservableList<PathElement> call() throws Exception {
                ObservableList<PathElement> coordinates = FXCollections.observableArrayList();
                
                if(dataPoints == null || dataPoints.size() < 2) return coordinates;
                
                double x = (dataPoints.get(0).x-minimumXValue.get()) / (maximumXValue.get()-minimumXValue.get()) * width;
                double y = height - (dataPoints.get(0).y-minimumYValue.get()) / (maximumYValue.get()-minimumYValue.get()) * height;
                
                coordinates.add(new MoveTo(x, y));
                
                for(int i = 1; i < dataPoints.size(); i++) { //Should be threadsafe, right?
                    if(isCancelled()) break;
                    x = (dataPoints.get(i).x-minimumXValue.get()) / (maximumXValue.get()-minimumXValue.get()) * width;
                    y = height - (dataPoints.get(i).y-minimumYValue.get()) / (maximumYValue.get()-minimumYValue.get()) * height;
                    coordinates.add(new LineTo(x, y));
                }
                
                return coordinates;
            }
        };
        task.setOnSucceeded((e) -> {
            ObservableList<PathElement> pe = null;
            if(task != null)
                pe = task.getValue();
            if(pe != null) {
                /*ObservableList el = plot.getElements();
                Observable<>
                for(int i = 1; i < el.size(); i++) {
                    ((LineTo) plot.getElements().get(i)).setX(width);
                }*/
                plot.getElements().setAll(pe);
                pe = null;
                task = null;
                System.gc();
            }
        });
        
        ThreadPoolSingleton.getExecutor().execute(task);
    }
    
}
