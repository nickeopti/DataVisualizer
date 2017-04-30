package datavisualizer;

import java.time.LocalTime;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.HBox.setHgrow;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * @author Nicklas Boserup
 */
public class TimeRangeView extends HBox {
    
    public LocalTime startTime, endTime;
    private TextField startH, endH, startM, endM;
    public SVGPath close;
    
    public TimeRangeView() {
        super(5);
        
        startH = new TextField("7");
        startH.setPrefColumnCount(2);
        startH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\d*"))
                startH.setText(newValue.replaceAll("[^\\d]", ""));
            if(startH.getText().isEmpty())
                startH.setText("0");
            if(Integer.parseInt(startH.getText()) > 23)
                startH.setText("23");
            startTime = LocalTime.of(Integer.parseInt(startH.getText()), Integer.parseInt(startM.getText()));
        });
        endH = new TextField("16");
        endH.setPrefColumnCount(2);
        endH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\d*"))
                endH.setText(newValue.replaceAll("[^\\d]", ""));
            if(endH.getText().isEmpty())
                endH.setText("0");
            if (Integer.parseInt(endH.getText()) > 23)
                endH.setText("23");
            endTime = LocalTime.of(Integer.parseInt(endH.getText()), Integer.parseInt(endM.getText()));
        });
        startM = new TextField("00");
        startM.setPrefColumnCount(2);
        startM.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\d*"))
                startM.setText(newValue.replaceAll("[^\\d]", ""));
            if(startM.getText().isEmpty())
                startM.setText("0");
            if(Integer.parseInt(startM.getText()) > 59)
                startM.setText("59");
            startTime = LocalTime.of(Integer.parseInt(startH.getText()), Integer.parseInt(startM.getText()));
        });
        endM = new TextField("00");
        endM.setPrefColumnCount(2);
        endM.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!newValue.matches("\\d*"))
                endM.setText(newValue.replaceAll("[^\\d]", ""));
            if(endM.getText().isEmpty())
                endM.setText("0");
            if (Integer.parseInt(endM.getText()) > 59)
                endM.setText("59");
            endTime = LocalTime.of(Integer.parseInt(endH.getText()), Integer.parseInt(endM.getText()));
        });
        
        close = new SVGPath();
        close.setContent("M 12 0 A 12 12 0 0 0 0 12 A 12 12 0 0 0 12 24 A 12 12 0 0 0 24 12 A 12 12 0 0 0 12 0 z M 4.8730469 4.1660156 L 12 11.292969 L 19.126953 4.1660156 L 19.833984 4.8730469 L 12.707031 12.001953 L 19.833984 19.128906 L 19.126953 19.835938 L 12 12.708984 L 4.8730469 19.835938 L 4.1660156 19.128906 L 11.292969 12.001953 L 4.1660156 4.8730469 L 4.8730469 4.1660156 z ");
        close.setScaleX(0.8);
        close.setScaleY(0.8);
        close.setFill(Color.gray(0.2));
        close.setOnMouseEntered(me -> close.setFill(Color.gray(0.4)));
        close.setOnMouseExited(me -> close.setFill(Color.gray(0.2)));
        close.setOnMousePressed(me -> close.setFill(Color.gray(0)));
        close.setOnMouseReleased(me -> close.setFill(Color.gray(0.2)));
        
        Region spring = new Region();
        setHgrow(spring, Priority.ALWAYS);
        
        getChildren().addAll(startH, new Label(":"), startM, new Label(" - "), endH, new Label(":"), endM, spring, close);
    }
    
}
