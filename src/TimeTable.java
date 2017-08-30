import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Bleeptrack on 28.09.2015.
 */
public class TimeTable {

    int[] posx;
    int[] posy;

    String name;
    String einrichtung;
    String vertrag;

    LocalDate vertragsBegin,
              vertragsEnde;

     int hourspm;
     int year;
     int month;

     int startday;
     int endday;
    //int endday = 15;
     int actualdays;
     int actualhous;


    // time configuration
     LocalTime dayStart;
     LocalTime dayEnd;
     LocalTime pauseStart;
     LocalTime pauseEnd;
     LocalTime interval;
     LocalTime urlaub;

    //working days of the week beginning with sunday
     boolean[] weekdays;

     int workingdays;

    int[][] hours;
    LocalTime[][] starts;

    public TimeTable(int month, int years, LocalTime dayStart, LocalTime dayEnd, LocalTime pauseStart, LocalTime pauseEnd,
                     LocalTime interval, int hourspm, LocalTime urlaub, boolean[] weekdays, int stday, int enday, String name, String einrichtung,
                     String vertrag, LocalDate vertragsBegin, LocalDate vertragsEnde){
        posy = new int[32];
        posx = new int[5];
        for(int i = 1; i<32; i++){
            posy[i]=480+(i-1)*49;
        }
        for(int i = 0; i<5; i++){
            posx[i] = 520+i*250;
        }




        this.einrichtung=einrichtung;
        this.name=name;
        this.vertrag=vertrag;
        this.vertragsBegin=vertragsBegin;
        this.vertragsEnde=vertragsEnde;

        this.month = month;

        this.year = years;

        Calendar cal = new GregorianCalendar(year, month, 1);
        startday = stday;
        if(enday<1) {
            endday = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }else{
            endday=enday;
        }
        //int endday = 15;
        actualdays = endday - startday + 1;
        actualhous = Math.round(actualdays * hourspm / cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.pauseStart = pauseStart;
        this.pauseEnd = pauseEnd;
        this.interval = interval;

        this.hourspm = hourspm;

        this.weekdays = weekdays;
        this.urlaub = urlaub;

        this.workingdays = calcWorkingDays();
        this.hours = new int[workingdays][3];
        this.starts = new LocalTime[workingdays][2];
        createHours();
    }

    private void createHours() {

        //first column in array is the day
        int count = 0;
        for (int i = startday; i <= endday; i++) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, i);
            Date date = c.getTime();
                       if (weekdays[c.get(Calendar.DAY_OF_WEEK) - 1] && !isHoliday(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH))) {
                hours[count][0] = c.get(Calendar.DAY_OF_MONTH);
                count++;
            }
        }

        //create random hours
        int elements = (actualhous * 60 - urlaub.getMinute()) / interval.getMinute();
        long am = Duration.between(dayStart, pauseStart).toMinutes() / interval.getMinute();
        long pm = Duration.between(pauseEnd, dayEnd).toMinutes() / interval.getMinute();
        for (int i = 0; i < elements; i++) {

            int x = (int) (Math.random() * workingdays);
            int y = (int) (Math.random() * 2 + 1);
            if (y == 0) {
                if (hours[x][y] < am) {
                    hours[x][y] += 1;
                } else {
                    i--;
                }
            } else {
                if (hours[x][y] < pm) {
                    hours[x][y] += 1;
                } else {
                    i--;
                }
            }

        }

        //create random beginnings
        for (int i = 0; i < workingdays; i++) {
            long restTime = Duration.between(dayStart, pauseStart).toMinutes() - hours[i][1] * interval.getMinute();
            long minutes = (long) (Math.random() * (restTime / interval.getMinute()+1));
            starts[i][0] = dayStart.plusMinutes(minutes * interval.getMinute());

            restTime = Duration.between(pauseEnd, dayEnd).toMinutes() - hours[i][2] * interval.getMinute();
            minutes = (long) (Math.random() * (restTime / interval.getMinute()+1));
            starts[i][1] = pauseEnd.plusMinutes(minutes * interval.getMinute());
        }

    }

    public void printResultsJPG(File f) throws Exception{
        BufferedImage img = null;
        //img = ImageIO.read(new File("src/template.jpg"));
        //InputStream in = getClass().getResourceAsStream("/template.jpg");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        img = ImageIO.read(getClass().getResourceAsStream("/template.jpg"));

        int w = img.getWidth();
        int h = img.getHeight();

        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);

        g2d.setFont(new Font("Serif", Font.BOLD, 60));
        g2d.drawString(""+einrichtung, 2060, 230);
        g2d.drawString(""+name, 500, 230);
        g2d.drawString(""+(month+1)+"/"+year, 1300, 135);

        g2d.setFont(new Font("Serif", Font.BOLD, 40));

        for(int i = 0; i<hours.length; i++){
            if (hours[i][1] != 0) {
                g2d.drawString("" + starts[i][0], posx[0], posy[hours[i][0]]);
                g2d.drawString("" + starts[i][0].plusMinutes(hours[i][1] * interval.getMinute()), posx[1], posy[hours[i][0]]);
            }
            if (hours[i][2] != 0) {
                g2d.drawString("" + starts[i][1], posx[2], posy[hours[i][0]]);
                g2d.drawString("" + starts[i][1].plusMinutes(hours[i][2] * interval.getMinute()), posx[3], posy[hours[i][0]]);
            }
            g2d.drawString(""+(hours[i][1] * interval.getMinute()+hours[i][2] * interval.getMinute())/60.0, posx[4], posy[hours[i][0]]);
        }

        g2d.drawString(""+hourspm, 1520, 2000);
        g2d.drawString(""+hourspm, 3015, 435);
        g2d.drawString(""+hourspm, 3015, 535);

        g2d.dispose();
        ImageIO.write(img, "jpg", f);
    }

    public void printResultsMD(File f) throws Exception {
      BufferedWriter writer = new BufferedWriter(new FileWriter(f));
      //writer.write(month+"."+year+"\n");
      writer.write("# Arbeitszeiterfassung " + (month+1) + "/" + year + "\n\n");
      writer.write("## Vertrag\n\n");
      writer.write("|||\n");
      writer.write("|---|---|\n");
      writer.write("|Name|"+name+"|\n");
      writer.write("|Einrichtung|"+einrichtung+"|\n");
      writer.write("|Vertragsname|"+vertrag+"|\n");
      writer.write("|Vertragslaufzeit|"+vertragsBegin+" bis "+vertragsEnde+"|\n");
      writer.write("|Monatliche Arbeitszeit|"+hourspm+"|\n");
      writer.write("|Monatlicher Urlaubsanspruch|"+urlaub+"|\n\n");
      writer.write("##Arbeitszeiten\n");
      writer.write("|Tag|von  |bis  |von  |bis  |Stunden|");
      writer.newLine();
      writer.write("|---|-----|-----|-----|-----|-------|");
      writer.newLine();

      double sum = 0;
      for (int i = 0; i < hours.length; i++) {
          String s = "";
          String s1, s2, s3, s4;
          if (hours[i][1] != 0) {
              s1 = "" + starts[i][0];
              s2 = "" + starts[i][0].plusMinutes(hours[i][1] * interval.getMinute());
          } else {
              s1 = "  -  ";
              s2 = "  -  ";
          }
          if (hours[i][2] != 0) {
              s3 = "" + starts[i][1];
              s4 = "" + starts[i][1].plusMinutes(hours[i][2] * interval.getMinute());
          } else {
              s3 = "  -  ";
              s4 = "  -  ";
          }
          double h = (hours[i][1] * interval.getMinute()+hours[i][2] * interval.getMinute())/60.0;
          sum += h;
          s = String.format("|%3d|%s|%s|%s|%s|%.1f|", hours[i][0], s1, s2, s3, s4,h);
          writer.write(s);
          writer.newLine();
      }
      writer.write("|---|---|---|---|---|---|\n");
      writer.write("|||||Summe: |"+String.format("%.1f", sum)+"|\n");

      writer.close();
    }

    public void printResults(File f) throws Exception {
      BufferedWriter writer = new BufferedWriter(new FileWriter(f));
      //writer.write(month+"."+year+"\n");
      writer.write("Tag;von  ;bis  ;von  ;bis  ;Stunden");
      writer.newLine();
      for (int i = 0; i < hours.length; i++) {
        String s = "";
        String s1, s2, s3, s4;
        if (hours[i][1] != 0) {
          s1 = "" + starts[i][0];
          s2 = "" + starts[i][0].plusMinutes(hours[i][1] * interval.getMinute());
        } else {
          s1 = "  -  ";
          s2 = "  -  ";
        }
        if (hours[i][2] != 0) {
          s3 = "" + starts[i][1];
          s4 = "" + starts[i][1].plusMinutes(hours[i][2] * interval.getMinute());
        } else {
          s3 = "  -  ";
          s4 = "  -  ";
        }
        double h = (hours[i][1] * interval.getMinute()+hours[i][2] * interval.getMinute())/60.0;
        s = String.format("%3d;%s;%s;%s;%s;%.1f", hours[i][0], s1, s2, s3, s4,h);
        writer.write(s);
        writer.newLine();
      }
      writer.close();
    }


    private int calcWorkingDays() {
        int workingdays = 0;
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, startday);

        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, endday + 1);

        while (c1.after(c2)) {
            if (weekdays[c2.get(Calendar.DAY_OF_WEEK) - 1] && !isHoliday(c2.get(Calendar.YEAR),c2.get(Calendar.MONTH),c2.get(Calendar.DAY_OF_MONTH)))
                workingdays++;
            c2.add(Calendar.DATE, 1);
        }
        return workingdays;
    }

    private boolean isHoliday(int year, int month, int day)
    {
        GregorianCalendar testday = new GregorianCalendar(year, month, day);


        int i = year % 19;
        int j = year / 100;
        int k = year % 100;

        int l = (19 * i + j - (j / 4) - ((j - ((j + 8) / 25) + 1) / 3) + 15) % 30;
        int m = (32 + 2 * (j % 4) + 2 * (k / 4) - l - (k % 4)) % 7;
        int n = l + m - 7 * ((i + 11 * l + 22 * m) / 451) + 114;

        int mo = n / 31;
        int d   = (n % 31) + 1;

        GregorianCalendar gc_ostersonntag = new GregorianCalendar(year, mo-1, d);
        GregorianCalendar gc_ostermontag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 1));
        GregorianCalendar gc_karfreitag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) - 2));
        GregorianCalendar gc_rosenmontag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) - 48));
        GregorianCalendar gc_christihimmelfahrt = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 39));
        GregorianCalendar gc_pfinstsonntag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 49));
        GregorianCalendar gc_pfinstmontag = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 50));
        GregorianCalendar gc_frohnleichnahm = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), gc_ostersonntag.get(Calendar.MONTH), (gc_ostersonntag.get(Calendar.DATE) + 60));
        GregorianCalendar gc_wiedervereinigung = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 9, 1);
        GregorianCalendar gc_weihnachten_1 = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 24);
        GregorianCalendar gc_weihnachten_2 = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 25);
        GregorianCalendar gc_weihnachten_3 = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 26);
        GregorianCalendar gc_silvester = new GregorianCalendar(gc_ostersonntag.get(Calendar.YEAR), 11, 31);
        GregorianCalendar gc_neujahr = new GregorianCalendar(gc_silvester.get(Calendar.YEAR), 0, 1);
        GregorianCalendar gc_tagderarbeit = new GregorianCalendar(gc_silvester.get(Calendar.YEAR), 4, 1);

        System.out.println(gc_karfreitag.get(Calendar.MONTH)+" "+gc_karfreitag.get(Calendar.MONTH));

        if(gc_tagderarbeit.equals(testday) || gc_pfinstsonntag.equals(testday) || gc_ostermontag.equals(testday) || gc_karfreitag.equals(testday) ||
                gc_rosenmontag.equals(testday) || gc_christihimmelfahrt.equals(testday) || gc_pfinstmontag.equals(testday) ||
                gc_frohnleichnahm.equals(testday) || gc_weihnachten_1.equals(testday) || gc_weihnachten_2.equals(testday) ||
                gc_weihnachten_3.equals(testday) || gc_silvester.equals(testday) || gc_neujahr.equals(testday) ||
                gc_wiedervereinigung.equals(testday))
        {
            return true;
        }
        else
        {
            return false;
        }


    }


}
