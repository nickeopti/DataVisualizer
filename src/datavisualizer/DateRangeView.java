package datavisualizer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * @author Nicklas Boserup
 */
public class DateRangeView extends HBox {
    
    public LocalDate startDate, endDate;
    private DatePicker startPicker, endPicker;
    private StringConverter<LocalDate> converter;
    private final String pattern = "dd/MM-yyyy";
    public SVGPath close;
    
    public DateRangeView() {
        super(5);
        startDate = LocalDate.now().minusDays(1);
        endDate = LocalDate.now();
        
        double width = new Text("10/10/2017").getLayoutBounds().getWidth()*1.7;
        
        startPicker = new DatePicker(startDate);
        startPicker.setShowWeekNumbers(true);
        startPicker.setPrefWidth(width);
        startPicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> startDate = startPicker.getValue());
        endPicker = new DatePicker(endDate);
        endPicker.setShowWeekNumbers(true);
        endPicker.setPrefWidth(width);
        endPicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> endDate = endPicker.getValue());
        
        converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if(date != null)
                    return dateFormatter.format(date);
                else
                    return pattern.toLowerCase();
            }

            @Override
            public LocalDate fromString(String string) {
                if(string != null && !string.isEmpty())
                    return LocalDate.parse(string, dateFormatter);
                else
                    return null;
            }
        };
        startPicker.setConverter(converter);
        endPicker.setConverter(converter);
        
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
        
        getChildren().addAll(startPicker, endPicker, spring, close);
    }
    
}
