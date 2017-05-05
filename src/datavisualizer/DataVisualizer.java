package datavisualizer;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import threading.ThreadPoolSingleton;

/**
 * @author Nicklas Boserup
 */
public class DataVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        //setupDataForGraph(g);

        
        MainUI mainUI = new MainUI();
        DataController dc = new DataController(mainUI);
        mainUI.getGraph().getPlots().get(0).dataPoints.setAll(dc.getComputedData());
        
        Scene scene = new Scene(mainUI);
        scene.setOnKeyPressed(ke -> {
            if(ke.isShortcutDown() && ke.getCode() == KeyCode.R)
                mainUI.getGraph().getPlots().get(0).dataPoints.setAll(dc.getComputedData());
        });
        
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
    
}
