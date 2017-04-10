package datavisualizer;

import com.sun.javafx.scene.control.skin.SliderSkin;
import graphing.Plotter;
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
        Plotter plot1 = new Plotter();
        plot1.dataPoints.addAll(new Point(0, 100), new Point(30, 50), new Point(60, 90), new Point(90, 10));
        
        Plotter plot2 = new Plotter();
        plot2.dataPoints.addAll(new Point(0, 10), new Point(20, 30), new Point(40, 90), new Point(60, 30), new Point(90, 20));
        plot2.plot.setStroke(Color.RED);
        
        DoubleProperty xZoom = new SimpleDoubleProperty(100);
        
        Slider s1 = new Slider(10, 500, 0);
        plot1.minimumXValue.bindBidirectional(s1.valueProperty());
        plot2.minimumXValue.bindBidirectional(s1.valueProperty());
        Slider s2 = new Slider(10, 500, 100);
        plot1.maximumXValue.bind(xZoom.add(s2.valueProperty()));
        plot2.maximumXValue.bind(xZoom.add(s2.valueProperty()));
        Slider s3 = new Slider(10, 500, 0);
        plot1.minimumYValue.bindBidirectional(s3.valueProperty());
        plot2.minimumYValue.bindBidirectional(s3.valueProperty());
        Slider s4 = new Slider(10, 500, 100);
        plot1.maximumYValue.bindBidirectional(s4.valueProperty());
        plot2.maximumYValue.bindBidirectional(s4.valueProperty());
        
        StackPane root = new StackPane();
        root.getChildren().addAll(plot1, plot2);
        
        ZoomScrollBar zcb = new ZoomScrollBar(0, 100, 0);
        zcb.zoom.set(2);
        
        VBox sliders = new VBox(root, s1, s2, s3, s4, zcb);

        Scene scene = new Scene(sliders);

        primaryStage.setTitle("Hello World!");
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

}
