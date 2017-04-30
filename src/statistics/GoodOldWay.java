package statistics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nicklas Boserup
 */
public class GoodOldWay {
    
    public static List<Point> readValuesFromFile() throws IOException {
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Double>[][][] timeBasedMedianValues = new ArrayList[5][9][60];
        for(int i = 0; i<5; i++) {
            for(int j = 0; j<9; j++) {
                for(int h = 0; h<60; h++) {
                    timeBasedMedianValues[i][j][h] = new ArrayList<>();
                }
            }
        }
        boolean firstLine = true;
        int countAll = 0;
        for(String s : Files.readAllLines(new File("nyedata.txt").toPath())) {
            if(firstLine) {firstLine = false; continue;}
            countAll++;
            String s1 = s.split(";")[24]; //24 = lokale 623 og 40 = lokale 634
            if(s1.length() == 19) {
                int d = Integer.parseInt(s1.substring(0, 2)); //Day of month
                int m = Integer.parseInt(s1.substring(3, 5)); //Month
                if((m == 2 && d >= 14 && d <= 22) || (m == 2 && d <= 4)) continue; //Ignore holidays
                if(m < 4 || d < 27) continue;//Kun efter 27/4, skal fjernes igen senere. Perioden efter klassen har været på lejrskole
                LocalDate date = LocalDate.parse("2015-0"+m+"-"+(d<10? "0"+d : d));
                if(date.getDayOfWeek().getValue() >= 6) continue; //Ignore weekends
                
                String s2 = s1.substring(11); //Substring containing only the time, not the date
                int h = Integer.parseInt(s2.substring(0, 2));
                if(h < 6 || h > 14) continue; //Ignore the time before and after schooltime
                
                int minute = Integer.parseInt(s2.substring(3, 5));
                timeBasedMedianValues[date.getDayOfWeek().getValue()-1][h-6][minute]
                        .add(Double.parseDouble(s.split(";")[25].replace(',', '.'))); //25 = lokale 623 og 41 = lokale 634
                
                values.add(Double.parseDouble(s.split(";")[25].replace(',', '.'))); //25 = lokale 623 og 41 = lokale 634
            }
        }
        System.out.println("Målinger i alt: " + countAll);
        System.out.println("Antal valide målinger: " + values.size());
        double sum = 0;
        for(double i : values) sum += i;
        System.out.println("Gennemsnit: " + sum/values.size() + "\nSum = " + sum);
        double max = 0;
        for(double i : values) if(i > max) max = i;
        System.out.println("Max: " + max);
        double min = 2000;
        for(double i : values) if(i < min) min = i;
        System.out.println("Min: " + min);
        Collections.sort(values);
        if(values.size()%2 == 0) {
            System.out.println("Median: " + (values.get(values.size()/2-1) + values.size()/2)/2);
        } else {
            System.out.println("Median: " + values.get(values.size()/2));
        }
        System.out.println("Tidsbaserede beregninger:");
        double[][][] calculatedMediansByTime = new double[5][9][60];
        int x = 0, y = 0, z = 0;
        /*for(ArrayList<Double>[][] a : timeBasedMedianValues) {
            for(ArrayList<Double>[] as: a) {
                for(ArrayList<Double> at: as) {
                    Collections.sort(at);
                    if(at.size()%2 == 0)
                        calculatedMediansByTime[x][y][z] = (at.get(at.size()/2-1) + at.get(at.size()/2)) /2;
                    else
                        calculatedMediansByTime[x][y][z] = at.get(at.size()/2);
                    z++;
                }
                z = 0;
                y++;
            }
            y = z = 0;
            x++;
        }
        */
        for(int i = 0; i<9*60; i++) {
            System.out.print("" + (i/60+6) + ":" + (i%60<10? "0"+i%60 : i%60) + "\t");
        }
        System.out.println();
        System.out.println("Samlet median");
        ArrayList<Double>[] mainMedian = new ArrayList[9*60];
        for(int i = 0; i<9*60; i++)
            mainMedian[i] = new ArrayList<>();
        for(int i = 0; i<9*60; i++) {
            for(int j = 0; j<5; j++) {
                mainMedian[i].addAll(timeBasedMedianValues[j][i/60][i%60]);
            }
        }
        double[] medians = new double[9*60];
        int count = 0;
        for(ArrayList<Double> a : mainMedian){
            Collections.sort(a);
            if(a.size()%2 == 0)
                medians[count] = (a.get(a.size()/2-1) + a.get(a.size()/2)) /2;
            else
                medians[count] = a.get(a.size()/2);
            count++;
        }
        String m = "";
        for(double d : medians)
            m = m + "\t" + (""+d).replace('.', ',');
        System.out.println(m);
        
        System.out.println("Gennemsnit over tid");
        ArrayList<Double> ave = new ArrayList<>();
        for(int i = 0; i<9*60; i++) {
            double aveSum = 0;
            int co = 0;
            for(int j = 0; j<5; j++) {
                for(double d : timeBasedMedianValues[j][i/60][i%60]) {
                    aveSum += d;
                    co++;
                }
            }
            ave.add(aveSum/co);
        }
        String a = "";
        for(double d : ave)
            a = a + "\t" + (""+d).replace('.', ',');
        System.out.println(a);
        
        String v = "";
        for(double[][] d0 : calculatedMediansByTime) {
            System.out.println("Ny dag");
            for(double[] d1 : d0) {
                for(double d2 : d1) {
                    v = v + "\t" + (""+d2).replace('.', ',');
                }
            }
            System.out.println(v);
            v = "";
        }
        
        
        
        /*********** For the new way of doing things :D ***********/
        List<Point> pointList = new ArrayList<>();
        for(int i = 0; i < medians.length; i++) {
            pointList.add(new Point(i, medians[i]));
        }
        return pointList;
    }
    
}
