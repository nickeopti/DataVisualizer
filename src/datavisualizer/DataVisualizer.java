package datavisualizer;

import com.sun.javafx.scene.control.skin.SliderSkin;
import datainput.TextFileInput;
import graphing.Graph;
import graphing.Plotter;
import graphing.ZoomScrollBar;
import graphing.ZoomScrollBar;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
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
import statistics.Point;
import threading.ThreadPoolSingleton;

/**
 * @author Nicklas Boserup
 */
public class DataVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) {
        Graph g = new Graph();
        g.getPlots().add(new Plotter());
        g.getPlots().get(0).dataPoints.addAll(new Point(0, 10), new Point(20, 30), new Point(40, 90), new Point(60, 30), new Point(90, 20));
        g.getPlots().add(new Plotter());
        g.getPlots().get(1).dataPoints.addAll(new Point(0, 100), new Point(30, 50), new Point(60, 90), new Point(90, 10));
        g.getPlots().get(1).plot.setStroke(Color.RED);
        g.getPlots().remove(0,1);
        
        Stop[] stops = {new Stop(0, Color.GREEN), new Stop(0.7, Color.YELLOW), new Stop(1, Color.RED)};
        g.getPlots().get(0).plot.setStroke(new LinearGradient(1, 1, 1, 0, true, CycleMethod.NO_CYCLE, stops));
        
        Scene scene = new Scene(g.pane);
        
        System.out.println("path: " + new File("data.txt").getAbsolutePath());
        TextFileInput tfi0 = new TextFileInput("data.txt");
        g.getPlots().get(0).dataPoints.setAll(tfi0.readDataPointList());

        primaryStage.setTitle("Visual Data Analyzer");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(500);
        primaryStage.setOnCloseRequest((e) -> ThreadPoolSingleton.getExecutor().shutdown());
        primaryStage.show();
        g.pane.requestLayout();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(ThreadPoolSingleton.getExecutor());
        System.out.println(ThreadPoolSingleton.getExecutor());
        
        launch(args);
    }

}
