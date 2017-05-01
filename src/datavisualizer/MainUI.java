package datavisualizer;

import graphing.Graph;
import graphing.Plotter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import statistics.GoodOldWay;
import statistics.TimeAndDateFilterList;

/**
 * @author Nicklas Boserup
 */
public class MainUI extends BorderPane {
    
    private Graph graph;
    public ToggleButton mon, tue, wed, thu, fri, sat, sun, all, combined;
    public ObservableList<TimeAndDateFilterList.DateRange> excludedDates;
    public final TimeAndDateFilterList.DateRange dateRange;
    public final TimeAndDateFilterList.TimeRange timeRange;
    
    public MainUI() {
        super();
        graph = convenient();
        
        excludedDates = FXCollections.observableArrayList();
        //dateRange = new TimeAndDateFilterList.DateRange(LocalDate.now().minusMonths(1), LocalDate.now());
        dateRange = new TimeAndDateFilterList.DateRange(LocalDate.of(2015, 5, 9), LocalDate.of(2015, 5, 11));
        System.out.println("epoch " + dateRange.startDate.atTime(0, 0).toEpochSecond(ZoneOffset.UTC));
        timeRange = new TimeAndDateFilterList.TimeRange(LocalTime.of(7, 0), LocalTime.of(16, 0));
        
        setCenter(graph.pane);
        setTop(setupSelectionPanel());
    }
    
    private Graph convenient() {
        Graph g = new Graph();
        
        Plotter plot = new Plotter();
        g.getPlots().add(plot); //Seriously wierd behaviour... This needs to be BEFORE it is modified! I got no clue why, whatsoever.
        
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
    
    private VBox setupSelectionPanel() {
        HBox upper = new HBox(10);
        upper.setAlignment(Pos.CENTER_LEFT);
        upper.setPadding(new Insets(5, 10, 5, 10));
        HBox lower = new HBox(10);
        lower.setAlignment(Pos.CENTER_LEFT);
        lower.setPadding(new Insets(5, 10, 5, 10));
        lower.setSpacing(3);
        
        /*** Weekdays toggle buttons ***/
        mon = new ToggleButton("Man");
        mon.setFocusTraversable(false);
        mon.setSelected(true);
        tue = new ToggleButton("Tir");
        tue.setFocusTraversable(false);
        tue.setSelected(true);
        wed = new ToggleButton("Ons");
        wed.setFocusTraversable(false);
        wed.setSelected(true);
        thu = new ToggleButton("Tor");
        thu.setFocusTraversable(false);
        thu.setSelected(true);
        fri = new ToggleButton("Fre");
        fri.setFocusTraversable(false);
        fri.setSelected(true);
        sat = new ToggleButton("Lør");
        sat.setFocusTraversable(false);
        sat.setSelected(false);
        sun = new ToggleButton("Søn");
        sun.setFocusTraversable(false);
        sun.setSelected(false);
        all = new ToggleButton("I alt");
        all.setFocusTraversable(false);
        all.setSelected(false);
        combined = new ToggleButton("Kombineret");
        combined.setFocusTraversable(false);
        combined.setSelected(true);
        
        SegmentedButton weekDaysSegment = new SegmentedButton(mon, tue, wed, thu, fri, sat, sun, all, combined);
        weekDaysSegment.setToggleGroup(null);
        weekDaysSegment.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
        
        upper.getChildren().add(weekDaysSegment);
        
        /*** Excluded days control ***/
        VBox exclDatesList = new VBox();
        exclDatesList.setPadding(new Insets(5));
        
        Button addExclDate = new Button("Tilføj");
        VBox.setMargin(addExclDate, new Insets(5, 0, 0, 0));
        addExclDate.setOnAction(oa -> {
            DateRangeView drv = new DateRangeView();
            drv.close.setOnMouseClicked(me -> {
                exclDatesList.getChildren().remove(drv);
            });
            exclDatesList.getChildren().add(exclDatesList.getChildren().size()-1, drv);
        });
        exclDatesList.getChildren().add(addExclDate);
        exclDatesList.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            List<TimeAndDateFilterList.DateRange> list = new ArrayList<>();
            for(Node n : exclDatesList.getChildren()) {
                if(n != null && n instanceof DateRangeView) {
                    DateRangeView drv = (DateRangeView) n;
                    TimeAndDateFilterList.DateRange dr;
                    if(drv.startDate.isBefore(drv.endDate))
                        dr = new TimeAndDateFilterList.DateRange(drv.startDate, drv.endDate);
                    else
                        dr = new TimeAndDateFilterList.DateRange(drv.endDate, drv.startDate);
                    list.add(dr);
                }
            }
            excludedDates.setAll(list);
        });
        
        PopOver exclDatesPop = new PopOver(exclDatesList);
        exclDatesPop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        exclDatesPop.setTitle("Excluderede datoer");
        exclDatesPop.setDetachable(false);
        
        SVGPath downArrow0 = new SVGPath();
        downArrow0.setScaleX(0.4); downArrow0.setScaleY(0.4); downArrow0.setTranslateY(2);
        downArrow0.setContent("m 0.99414062,1039.8574 a 0.50005,0.50005 0 0 0 -0.34765624,0.8574 L 12,1052.0684 23.353516,1040.7148 a 0.50005,0.50005 0 1 0 -0.707032,-0.707 L 12,1050.6543 1.3535156,1040.0078 a 0.50005,0.50005 0 0 0 -0.35937498,-0.1504 z");
        Button exclDates = new Button("Excl datoer", downArrow0);
        exclDates.setGraphicTextGap(0);
        exclDates.setContentDisplay(ContentDisplay.RIGHT);
        exclDates.setFocusTraversable(false);
        exclDates.setOnAction(oa -> {
            if(exclDatesPop.isShowing()) {
                exclDatesPop.hide();
            } else {
                exclDatesPop.show(exclDates);
            }
        });
        addExclDate.fire();
        
        upper.getChildren().add(exclDates);
        
        /*** Selected date-interval control ***/
        double width = new Text("10/10-2017").getLayoutBounds().getWidth()*1.7;
        final String pattern = "dd/MM-yyyy";
        
        DatePicker startPicker = new DatePicker(dateRange.startDate);
        startPicker.setShowWeekNumbers(true);
        startPicker.setPrefWidth(width);
        startPicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> dateRange.startDate = startPicker.getValue());
        DatePicker endPicker = new DatePicker(dateRange.endDate);
        endPicker.setShowWeekNumbers(true);
        endPicker.setPrefWidth(width);
        endPicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> dateRange.endDate = endPicker.getValue());

        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return pattern.toLowerCase();
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        startPicker.setConverter(converter);
        endPicker.setConverter(converter);
        
        lower.getChildren().addAll(new Label("Fra "), startPicker, new Label(" til "), endPicker);
        
        /*** Selected date-interval control ***/
        TextField startH, endH, startM, endM;
        startH = new TextField("7");
        endH = new TextField("16");
        startM = new TextField("00");
        endM = new TextField("00");
        
        startH.setPrefColumnCount(2);
        startH.setAlignment(Pos.CENTER_RIGHT);
        startH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                startH.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (startH.getText().isEmpty()) {
                startH.setText("0");
            }
            if (Integer.parseInt(startH.getText()) > 23) {
                startH.setText("23");
            }
            timeRange.startTime = LocalTime.of(Integer.parseInt(startH.getText()), Integer.parseInt(startM.getText()));
        });
        endH.setPrefColumnCount(2);
        endH.setAlignment(Pos.CENTER_RIGHT);
        endH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                endH.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (endH.getText().isEmpty()) {
                endH.setText("0");
            }
            if (Integer.parseInt(endH.getText()) > 23) {
                endH.setText("23");
            }
            timeRange.endTime = LocalTime.of(Integer.parseInt(endH.getText()), Integer.parseInt(endM.getText()));
        });
        startM.setPrefColumnCount(2);
        startM.setAlignment(Pos.CENTER_RIGHT);
        startM.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                startM.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (startM.getText().isEmpty()) {
                startM.setText("0");
            }
            if (Integer.parseInt(startM.getText()) > 59) {
                startM.setText("59");
            }
            timeRange.startTime = LocalTime.of(Integer.parseInt(startH.getText()), Integer.parseInt(startM.getText()));
        });
        endM.setPrefColumnCount(2);
        endM.setAlignment(Pos.CENTER_RIGHT);
        endM.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                endM.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (endM.getText().isEmpty()) {
                endM.setText("0");
            }
            if (Integer.parseInt(endM.getText()) > 59) {
                endM.setText("59");
            }
            timeRange.endTime = LocalTime.of(Integer.parseInt(endH.getText()), Integer.parseInt(endM.getText()));
        });
        
        lower.getChildren().addAll(new Label(" i tidsintervallet "), startH, new Label(":"), startM, new Label(" - "), endH, new Label(":"), endM);
        
        return new VBox(0, lower, upper);
    }
    
}
