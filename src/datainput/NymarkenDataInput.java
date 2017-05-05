package datainput;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import statistics.Point;

/**
 * @author Nicklas Boserup
 */
public class NymarkenDataInput extends TextFileInput {

    private int dateCol = 24, dataCol = 25; //Evt. 40 og 41
    
    public NymarkenDataInput(String source) {
        super(source);
    }
    
    public NymarkenDataInput(String source, int dataCol) {
        super(source);
        this.dateCol = dataCol-1;
        this.dataCol = dataCol;
    }

    public int getDateCol() {
        return dateCol;
    }

    public int getDataCol() {
        return dataCol;
    }

    public void setDateCol(int dateCol) {
        this.dateCol = dateCol;
        this.dataCol = dateCol+1; 
    }
    
    public void setDataCol(int dataCol) {
        this.dateCol = dataCol-1;
        this.dataCol = dataCol;
    }
    
    @Override
    public Point handleLine(String input) {
        if (input.indexOf(';') == -1) {
            return null;
        }
        String s0 = input.split(";")[dateCol];
        if(s0.length() != 19) return null;
        int d = Integer.parseInt(s0.substring(0,2));
        int m = Integer.parseInt(s0.substring(3,5));
        int y = Integer.parseInt(s0.substring(6,10));
        
        int hour = Integer.parseInt(s0.substring(11, 13));
        int minute = Integer.parseInt(s0.substring(14, 16));
        
        LocalDateTime date = LocalDateTime.of(y, m, d, hour, minute);
        //System.out.println("nymepoch " + date.toEpochSecond(ZoneOffset.UTC));
        
        try {
            double value = Double.parseDouble(input.split(";")[dataCol].replace(',', '.'));
            return new Point(date.toEpochSecond(ZoneOffset.UTC), value);
        } catch (NumberFormatException nfe) {}
        return null;
    }
    
}
