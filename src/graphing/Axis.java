package graphing;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Control;

/**
 * @author Nicklas Boserup
 */
public class Axis extends Control {
    
    public DoubleProperty minimumValue, maximumValue, tickCount;
    
    public Axis() {
        minimumValue = new SimpleDoubleProperty(0);
        maximumValue = new SimpleDoubleProperty(50);
        
        setMinSize(100, 300);
    }
    
    public void update() {
        
    }
    
    { //Gets called along every contructor
        widthProperty().addListener((ob, oldVal, newVal) -> update());
        heightProperty().addListener((ob, oldVal, newVal) -> update());
    }
    
}
