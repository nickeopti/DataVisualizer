package graphing;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author Nicklas Boserup
 */
public class ZoomScrollBar extends Region {
    
    public BooleanProperty isHorizontal;
    public DoubleProperty minimumValue, maximumValue, currentValue, zoom;
    protected final double boundaryStrokeWidth = 2;
    
    protected final double MINIMUM_LENGTH = 50;
    protected final double MINIMUM_THICKNESS = 10;
    protected final double PREF_THICKNESS = 15;
    protected final double MAXIMUM_THICKNESS = 25;
    
    protected Rectangle knob;
    protected Rectangle boundary;
    
    public ZoomScrollBar(double minSlideVal, double maxSlideVal, double val) {
        isHorizontal = new SimpleBooleanProperty(true);
        
        minimumValue = new SimpleDoubleProperty(minSlideVal);
        minimumValue.addListener((ob, oldVal, newVal) -> {
            requestLayout();
        });
        maximumValue = new SimpleDoubleProperty(maxSlideVal);
        maximumValue.addListener((ob, oldVal, newVal) -> {
            requestLayout();
        });
        currentValue = new SimpleDoubleProperty(val);
        currentValue.addListener((ob, oldVal, newVal) -> {
            if(newVal.doubleValue() < minimumValue.get())
                currentValue.set(minimumValue.get());
            else if(newVal.doubleValue() > maximumValue.get())
                currentValue.set(maximumValue.get());
            requestLayout();
        });
        zoom = new SimpleDoubleProperty(1);
        zoom.addListener((ObservableValue<? extends Number> ob, Number oldVal, Number newVal) -> {
            if(newVal.doubleValue() < 1)
                zoom.set(1);
            requestLayout();
        });
        
        knob = new Rectangle(getPrefWidth(), getPrefHeight(), Color.grayRgb(80));
        knob.setStroke(Color.grayRgb(10));
        knob.setStrokeWidth(1);
        knob.setStrokeType(StrokeType.INSIDE);
        knob.arcHeightProperty().bind(knob.heightProperty());
        knob.arcWidthProperty().bind(knob.heightProperty());
        DropShadow knobShadow = new DropShadow(5, Color.grayRgb(30));
        knob.setEffect(knobShadow);
        initKnobDragBehaviour();
        
        boundary = new Rectangle(getPrefWidth(), getPrefHeight(), Color.grayRgb(170));
        boundary.setStroke(Color.grayRgb(10));
        boundary.setStrokeWidth(boundaryStrokeWidth);
        boundary.setStrokeType(StrokeType.INSIDE);
        boundary.arcHeightProperty().bind(boundary.heightProperty());
        boundary.arcWidthProperty().bind(boundary.heightProperty());
        
        getChildren().addAll(boundary, knob);
        setPadding(new Insets(5));
    }
    
    Point2D dragStartPos;
    double dragStartValue;
    private void initKnobDragBehaviour() {
        knob.setOnMousePressed((me) -> {
            dragStartPos = localToParent(me.getX(), me.getY());
            dragStartValue = (currentValue.get() - minimumValue.get()) / (maximumValue.get() - minimumValue.get());
        });
        knob.setOnMouseDragged((me) -> {
            double l = isHorizontal.get() ? knob.getWidth() : knob.getHeight(); //Length of the knob
            double w = isHorizontal.get() ? getWidth() : getHeight(); //Length of the control
            double i = getKnobInset();
            
            Point2D curPos = localToParent(me.getX(), me.getY());
            double dragLength = isHorizontal.get() ? curPos.getX()-dragStartPos.getX() : curPos.getY()-dragStartPos.getY();
            double val = dragStartValue + dragLength/(w - 2*i - l);
            
            currentValue.set(knobValue( knobPosition(currentValue.get()) + dragLength ));
            
            //currentValue.set(val*(maximumValue.get()-minimumValue.get()) + minimumValue.get());
            requestLayout();
            
            System.out.println("val: " + currentValue.get());
        });
    }
    
    private double getKnobInset() {
        return (isHorizontal.get() ? boundary.getBoundsInParent().getMinX() : boundary.getBoundsInParent().getMinY()) + boundaryStrokeWidth;
    }
    
    /**
     * Calculates the knob's correct position in the control, based upon
     * the minimum and maximum values as well as the value, val, and length of the knob.
     * @param val The value to calculate the knob's position upon
     * @param inset The length between the edge of the control and the min- and max positions of the knob
     * @return The position of the edge of the knob, in the longitudinal direction of the knob/control
     */
    private double knobPosition(double val) { //val = x
        double l = isHorizontal.get() ? knob.getWidth() : knob.getHeight(); //Length of the knob
        double w = isHorizontal.get() ? getWidth() : getHeight(); //Length of the control
        double i = getKnobInset();
        double m = (val - minimumValue.get()) / (maximumValue.get() - minimumValue.get()) * (w - 2*i - l) + i;
        
        return m;
    }
    
    private double knobValue(double pos) { //pos = m
        double l = isHorizontal.get() ? knob.getWidth() : knob.getHeight(); //Length of the knob
        double w = isHorizontal.get() ? getWidth() : getHeight(); //Length of the control
        double i = getKnobInset();
        double x = (pos-i) / (w-2*i-l) * (maximumValue.get() - minimumValue.get()) + minimumValue.get();
        
        return x;
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren(); //May really not be necessary
        boundary.setWidth(getWidth()-getInsets().getLeft()-getInsets().getRight());
        boundary.setHeight(getHeight()-getInsets().getTop()-getInsets().getBottom());
        boundary.relocate(getInsets().getLeft(), getInsets().getTop());
        if(isHorizontal.get()) {
            knob.setHeight(boundary.getHeight()-boundaryStrokeWidth*2);
            //compute the width
            knob.setWidth((boundary.getWidth()-boundaryStrokeWidth*2)/zoom.get());
            knob.relocate(knobPosition(currentValue.get()), boundary.getBoundsInParent().getMinY() + boundaryStrokeWidth);
        } else {
            knob.setWidth(getWidth()-boundaryStrokeWidth*2);
            //compute the height
            knob.relocate(boundaryStrokeWidth, knobPosition(currentValue.get()));
        }
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets insets = getInsets();
        if(isHorizontal.get())
            return insets.getTop() + insets.getBottom() + MINIMUM_THICKNESS;
        else
            return insets.getTop() + insets.getBottom() + MINIMUM_LENGTH;
    }

    @Override
    protected double computeMaxHeight(double width) {
        Insets insets = getInsets();
        if(isHorizontal.get())
            return insets.getTop() + insets.getBottom() + MAXIMUM_THICKNESS;
        else
            return Double.MAX_VALUE;
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets insets = getInsets();
        if(isHorizontal.get())
            return insets.getTop() + insets.getBottom() + PREF_THICKNESS;
        else
            return super.computePrefHeight(width);
    }
    
    @Override
    protected double computeMinWidth(double height) {
        Insets insets = getInsets();
        if(isHorizontal.get())
            return insets.getRight() + insets.getLeft() + MINIMUM_LENGTH;
        else
            return insets.getRight() + insets.getLeft() + MINIMUM_THICKNESS;
    }

    @Override
    protected double computeMaxWidth(double height) {
        Insets insets = getInsets();
        if(isHorizontal.get())
            return Double.MAX_VALUE;
        else
            return insets.getRight() + insets.getLeft() + MAXIMUM_THICKNESS;
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets insets = getInsets();
        if(isHorizontal.get())
            return super.computePrefWidth(height);
        else
            return insets.getRight() + insets.getLeft() + PREF_THICKNESS;
    }
    
}
