package datavisualizer;

import graphing.Graph;
import graphing.Plotter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import statistics.GoodOldWay;

/**
 * @author Nicklas Boserup
 */
public class MainUI extends BorderPane {
    
    private Graph graph;
    
    public MainUI() {
        super();
        //graph = new Graph();
        
        //addGraph();
        
        graph = convenient();
        
        setCenter(graph.pane);
    }
    
    private Graph convenient() {
        Graph g = new Graph();
        
        Plotter plot = new Plotter();
        g.getPlots().add(plot);
        
        //g.getPlots().add(new Plotter());
        
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
    
}
