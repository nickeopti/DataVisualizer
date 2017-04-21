package datavisualizer;

import com.sun.javafx.scene.control.skin.SliderSkin;
import graphing.Graph;
import graphing.Plotter;
import graphing.ZoomScrollBar;
import graphing.ZoomScrollBar;
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
        
        Scene scene = new Scene(g.pane);

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
        System.out.println(ThreadPoolSingleton.getExecutor());
        System.out.println(ThreadPoolSingleton.getExecutor());
        
        launch(args);
    }

}
