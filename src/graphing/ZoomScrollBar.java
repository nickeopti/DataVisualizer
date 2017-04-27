package graphing;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
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
    public DoubleProperty minimumValue, maximumValue;
    public DoubleProperty currentMinValue, currentMaxValue;
    
    protected final double BOUNDARY_STROKE_WIDTH = 2;
    protected final static double ZOOM_AREA_LENGTH = 10;
    protected final static double MINIMUM_KNOB_LENGTH = 2*ZOOM_AREA_LENGTH + 5;
    protected final double MINIMUM_LENGTH = 50;
    protected final double MINIMUM_THICKNESS = 10;
    protected final double PREF_THICKNESS = 15;
    protected final double MAXIMUM_THICKNESS = 25;
    
    protected Rectangle knob;
    protected Rectangle boundary;
    
    public ZoomScrollBar(double minSlideVal, double maxSlideVal, double minVal, double maxVal) {
        initProporties(minSlideVal, maxSlideVal, minVal, maxVal);
        
        knob = new Rectangle();
        knob.setFill(Color.grayRgb(80));
        knob.setStroke(Color.grayRgb(10));
        knob.setStrokeWidth(1);
        knob.setStrokeType(StrokeType.INSIDE);
        DropShadow knobShadow = new DropShadow(5, Color.grayRgb(30));
        knob.setEffect(knobShadow);
        
        initKnobDragBehaviour();
        
        boundary = new Rectangle();
        boundary.setFill(Color.grayRgb(170));
        boundary.setStroke(Color.grayRgb(10));
        boundary.setStrokeWidth(BOUNDARY_STROKE_WIDTH);
        boundary.setStrokeType(StrokeType.INSIDE);
        
        getChildren().addAll(boundary, knob);
        setPadding(new Insets(5));
    }
    
    public ZoomScrollBar() {
        this(0, 0, 0, MINIMUM_KNOB_LENGTH);
    }
    
    public void scroll(double pixelLength) {
        double min = currentMinValue.get() + positionToValue(pixelLength);
        double max = currentMaxValue.get() + positionToValue(pixelLength);
        if (min < max) {
            double delta = 0;
            if (min < minimumValue.get()) {
                delta = min - minimumValue.get();
            } else if (max > maximumValue.get()) {
                delta = max - maximumValue.get();
            }
            System.out.println("delta: " + delta);
            currentMinValue.set(min - delta);
            currentMaxValue.set(max - delta);
        }
    }
    
    public double getZoomFactor() {
        return trackLength()/valueToPosition(currentMaxValue.get()-currentMinValue.get());
        //return (currentMaxValue.get()-currentMinValue.get())/(maximumValue.get()-minimumValue.get()) * trackLength();
    }
    
    private void initProporties(double minSlideVal, double maxSlideVal, double minVal, double maxVal) {
        isHorizontal = new SimpleBooleanProperty(true);
        isHorizontal.addListener((ob, oldVal, newVal) -> requestLayout());
        
        minimumValue = new SimpleDoubleProperty(minSlideVal);
        minimumValue.addListener((ob, oldVal, newVal) -> requestLayout());
        
        maximumValue = new SimpleDoubleProperty(maxSlideVal);
        maximumValue.addListener((ob, oldVal, newVal) -> requestLayout());
        
        currentMinValue = new SimpleDoubleProperty(minVal);
        currentMinValue.addListener((ob, oldVal, newVal) -> requestLayout());
        
        currentMaxValue = new SimpleDoubleProperty(maxVal);
        currentMaxValue.addListener((ob, oldVal, newVal) -> requestLayout());
    }
    
    private void initKnobDragBehaviour() {
        class ZoomEventHandler {
            public double x, y;
            public boolean zoomEvent, firstHandle; //firstHandle is the left or upper handle
        }
        final ZoomEventHandler ze = new ZoomEventHandler();
        
        knob.setOnMousePressed((me) -> {
            ze.x = me.getSceneX();
            ze.y = me.getSceneY();
            ze.zoomEvent = isHorizontal.get() ? me.getX() < ZOOM_AREA_LENGTH || me.getX() > knob.getWidth() - ZOOM_AREA_LENGTH
                    : me.getY() < ZOOM_AREA_LENGTH || me.getY() > knob.getHeight() - ZOOM_AREA_LENGTH;
            ze.firstHandle = isHorizontal.get() ? me.getX() < ZOOM_AREA_LENGTH : me.getY() < ZOOM_AREA_LENGTH;
        });
        
        knob.setOnMouseDragged((me) -> {
            double dragLength = isHorizontal.get() ? me.getSceneX() - ze.x : me.getSceneY() - ze.y;
            ze.x = me.getSceneX();
            ze.y = me.getSceneY();
            
            System.out.println("DragLength: " + dragLength + "\nposToVal: " + positionToValue(dragLength));
            
            double min = currentMinValue.get() + positionToValue(dragLength);
            double max = currentMaxValue.get() + positionToValue(dragLength);
            if(ze.zoomEvent) {
                if(ze.firstHandle) {
                    if(min < currentMaxValue.get() && currentMaxValue.get()-min >= positionToValue(MINIMUM_KNOB_LENGTH))
                        currentMinValue.set(Math.max(min, minimumValue.get()));
                } else {
                    if(max > currentMinValue.get() && max-currentMinValue.get() >= positionToValue(MINIMUM_KNOB_LENGTH))
                        currentMaxValue.set(Math.min(max, maximumValue.get()));
                }
            } else {
                scroll(dragLength);
            }
        });
    }
    
    private double knobInset() {
        //return (isHorizontal.get() ? getInsets().getLeft() : getInsets().getTop()) + BOUNDARY_STROKE_WIDTH;
        return (isHorizontal.get() ? boundary.getBoundsInParent().getMinX() : boundary.getBoundsInParent().getMinY()) + BOUNDARY_STROKE_WIDTH;
    }
    
    private double trackLength() { //Assumes the control is symmetrical...
        double w = isHorizontal.get() ? getWidth() : getHeight(); //Length of the entire control
        //return w - 2*knobInset();
        return (isHorizontal.get() ? boundary.getWidth() : boundary.getHeight()) - 2*BOUNDARY_STROKE_WIDTH;
    }
    
    private double valueToPosition(double val) {
        if(isHorizontal.get())
            return (val - minimumValue.get()) / (maximumValue.get() - minimumValue.get()) * trackLength();
        else
            return (maximumValue.get() - val) / (maximumValue.get() - minimumValue.get()) * trackLength();
    }
    
    private double positionToValue(double pos) {
        if(isHorizontal.get())
            //return (trackLength()*pos-knobInset()*trackLength()+maximumValue.get()*minimumValue.get() - Math.pow(minimumValue.get(), 2)) / (maximumValue.get() - minimumValue.get());
            //return (pos)/trackLength()*(maximumValue.get()-minimumValue.get()) + minimumValue.get();
            return (pos)/trackLength()*(maximumValue.get()-minimumValue.get()) + minimumValue.get();
        else
            return maximumValue.get() - ((trackLength()-pos)/trackLength()*(maximumValue.get() - minimumValue.get()));
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren(); //May really not be necessary...
        Insets insets = getInsets();
        boundary.setWidth(getWidth()-insets.getLeft()-insets.getRight());
        boundary.setHeight(getHeight()-insets.getTop()-insets.getBottom());
        boundary.relocate(insets.getLeft(), insets.getTop());
        if(isHorizontal.get()) {
            knob.setHeight(boundary.getHeight()-BOUNDARY_STROKE_WIDTH*2);
            knob.setWidth(valueToPosition(currentMaxValue.get()) - valueToPosition(currentMinValue.get()));
            knob.relocate(valueToPosition(currentMinValue.get())+knobInset(), knobInset());
            
            //System.out.println("max: " + maximumValue.get() + "\tcurMax" + currentMaxValue.get() + "\n" +
              //      "min: " + minimumValue.get() + "\tcurMin" + currentMinValue.get());
            
            boundary.setArcWidth(boundary.getHeight());
            boundary.setArcHeight(boundary.getHeight());
            knob.setArcWidth(knob.getHeight());
            knob.setArcHeight(knob.getHeight());
        } else {
            knob.setWidth(boundary.getWidth()-BOUNDARY_STROKE_WIDTH*2);
            knob.setHeight(valueToPosition(currentMaxValue.get()) - valueToPosition(currentMinValue.get()));
            //knob.relocate(knobInset(), valueToPosition(currentMinValue.get()));
            knob.relocate(knobInset(), valueToPosition(minimumValue.get()) - valueToPosition(currentMinValue.get()) + knobInset());
            
            //System.out.println("max: " + maximumValue.get() + "\tcurMax" + currentMaxValue.get() + "\n" +
              //      "min: " + minimumValue.get() + "\tcurMin" + currentMinValue.get());
            
            boundary.setArcWidth(boundary.getWidth());
            boundary.setArcHeight(boundary.getWidth());
            knob.setArcWidth(knob.getWidth());
            knob.setArcHeight(knob.getWidth());
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
