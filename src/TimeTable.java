import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Sabine on 28.09.2015.
 */
public class TimeTable {

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

    //working days of the week beginning with sunday
     boolean[] weekdays;

     int workingdays;

    int[][] hours;
    LocalTime[][] starts;

    public TimeTable(int month, int years, LocalTime dayStart, LocalTime dayEnd, LocalTime pauseStart, LocalTime pauseEnd,
                     LocalTime interval, int hourspm, boolean[] weekdays, int stday, int enday){
        this.month = month;
        System.out.println(month);
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
            System.out.println(isHoliday(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
            if (weekdays[c.get(Calendar.DAY_OF_WEEK) - 1] && !isHoliday(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH))) {
                hours[count][0] = c.get(Calendar.DAY_OF_MONTH);
                count++;
            }
        }

        //create random hours
        int elements = actualhous * 60 / interval.getMinute();
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
            long minutes = (long) (Math.random() * (restTime / interval.getMinute()));
            starts[i][0] = dayStart.plusMinutes(minutes * interval.getMinute());

            restTime = Duration.between(pauseEnd, dayEnd).toMinutes() - hours[i][2] * interval.getMinute();
            minutes = (long) (Math.random() * (restTime / interval.getMinute()));
            starts[i][1] = pauseEnd.plusMinutes(minutes * interval.getMinute());
        }

    }

    public void printResults(File f) {
        try {
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
        } catch (Exception e) {

        }
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
        int jahr = year;

        int a = jahr % 19;
        int b = jahr % 4;
        int c = jahr % 7;
        int monat = 0;

        int m = (8 * (jahr / 100) + 13) / 25 - 2;
        int s = jahr / 100 - jahr / 400 - 2;
        m = (15 + s - m) % 30;
        int n = (6 + s) % 7;

        int d = (m + 19 * a) % 30;

        if (d == 29)
            d = 28;
        else if (d == 28 && a >= 11)
            d = 27;

        int e = (2 * b + 4 * c + 6 * d + n) % 7;

        int tag = 21 + d + e + 1;

        if (tag > 31)
        {
            tag = tag % 31;
            monat = 3;
        }
        if (tag <= 31)
            monat = 2;

        GregorianCalendar gc_ostersonntag = new GregorianCalendar(jahr, monat, tag);
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

        System.out.println(gc_weihnachten_2.toString());
        System.out.println(testday.toString());

        if(gc_pfinstsonntag.equals(testday) || gc_ostermontag.equals(testday) || gc_karfreitag.equals(testday) ||
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
