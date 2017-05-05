package datainput;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
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
public abstract class TextFileInput extends DataInput {

    public TextFileInput(String source) {
        super(source);
    }

    @Override
    public List<Point> readDataPointList() {
        List<Point> dataPoints = new ArrayList<>();
        try {
            Files.lines(new File(source).toPath(), Charset.forName("ISO-8859-1")).forEach(e -> { //ISO-8859-1
                Point p = handleLine(e.trim());
                if(p != null)
                    dataPoints.add(p);
            });
        } catch (IOException ex) {
            Logger.getLogger(TextFileInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return dataPoints;
    }
    
    public abstract Point handleLine(String input);
    
    
}
