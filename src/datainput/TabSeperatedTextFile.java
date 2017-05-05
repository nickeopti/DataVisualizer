package datainput;

import statistics.Point;

/**
 * @author Nicklas Boserup
 */
public class TabSeperatedTextFile extends TextFileInput {

    public TabSeperatedTextFile(String source) {
        super(source);
    }

    @Override
    public Point handleLine(String input) {
        if (input.indexOf('\t') == -1) {
            return null;
        }
        String s0 = input.substring(0, input.indexOf('\t'));
        String s1 = input.substring(input.indexOf('\t') + 1, input.length());
        try {
            long d0 = Long.parseLong(s0.replace(',', '.'));
            double d1 = Double.parseDouble(s1.replace(',', '.'));
            return new Point(d0, d1);
        } catch (NumberFormatException nfe) {}
        return null;
    }
    
}
