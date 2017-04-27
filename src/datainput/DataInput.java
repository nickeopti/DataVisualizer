package datainput;

import java.util.List;
import statistics.Point;

/**
 * @author Nicklas Boserup
 */
public abstract class DataInput {
    
    protected String source;
    
    public DataInput(String source) {
        this.source = source;
    }
    
    public abstract List<Point> readDataPointList();
    
}
