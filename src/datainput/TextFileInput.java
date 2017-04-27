package datainput;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import statistics.Point;

/**
 * @author Nicklas Boserup
 */
public class TextFileInput extends DataInput {

    public TextFileInput(String source) {
        super(source);
    }

    @Override
    public List<Point> readDataPointList() {
        List<Point> dataPoints = new ArrayList<>();
        try {
            Files.lines(new File(source).toPath()).forEach(e -> {
                e = e.trim();
                if(e.indexOf('\t') == -1) return;
                String s0 = e.substring(0, e.indexOf('\t'));
                String s1 = e.substring(e.indexOf('\t')+1, e.length());
                try {
                double d0 = Double.parseDouble(s0);
                double d1 = Double.parseDouble(s1);
                dataPoints.add(new Point(d0, d1));
                } catch(NumberFormatException nfe) {} //Just carry on to the next line...
            });
        } catch (IOException ex) {
            Logger.getLogger(TextFileInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return dataPoints;
    }
    
    
    
}
