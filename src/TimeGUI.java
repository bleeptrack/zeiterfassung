/**
 * Created by Sabine on 04.12.2015.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalTime;
import java.util.Calendar;


public class TimeGUI extends Application {

    private TimeTable tt;

    public static void main(String[] args) {
        System.setProperty("glass.accessible.force", "false");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Zeiterfassung");
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(pane, 600, 350);


        Label total = new Label("Monat");
        pane.add(total, 0, 0);
        final ComboBox months = new ComboBox();
        months.getItems().addAll(
                "Januar",
                "Februar",
                "Maerz",
                "April",
                "Mai",
                "Juni",
                "Juli",
                "August",
                "September",
                "Oktober",
                "November",
                "Dezember"
        );
        months.getSelectionModel().select(Calendar.getInstance().get(Calendar.MONTH));
        pane.add(months, 1, 0);

        Label lyear = new Label("Jahr:");
        pane.add(lyear,2,0);
        final TextField year = new TextField();
        year.setText(""+Calendar.getInstance().get(Calendar.YEAR));
        pane.add(year, 3, 0);

        Label lmstart = new Label("erster Arbeitstag:");
        pane.add(lmstart,0,1);
        final TextField mstart = new TextField();
        mstart.setText("1");
        mstart.setDisable(true);
        pane.add(mstart, 1, 1);

        Label lmend = new Label("letzter Arbeitstag:");
        pane.add(lmend,2,1);
        final TextField mend = new TextField();
        mend.setDisable(true);
        mend.setText("30");
        pane.add(mend, 3, 1);

        CheckBox atag = new CheckBox();
        pane.add(atag,4,1);

        Label ldaystart = new Label("Arbeitsbeginn:");
        pane.add(ldaystart,0,2);
        final TextField daystart = new TextField();
        daystart.setText("8:00");
        pane.add(daystart, 1, 2);

        Label ldayend = new Label("Arbeitsende:");
        pane.add(ldayend,2,2);
        final TextField dayend = new TextField();
        dayend.setText("17:00");
        pane.add(dayend, 3, 2);


        Label lpstart = new Label("Pausenbeginn:");
        pane.add(lpstart,0,3);
        final TextField pstart = new TextField();
        pstart.setText("12:00");
        pane.add(pstart, 1, 3);

        Label lpend = new Label("Pausenende:");
        pane.add(lpend,2,3);
        final TextField pend = new TextField();
        pend.setText("13:00");
        pane.add(pend, 3, 3);


        Label lhours = new Label("Stunden/Monat:");
        pane.add(lhours,0,4);
        final TextField hours = new TextField();
        hours.setText("40");
        pane.add(hours, 1, 4);

        Label linterval = new Label("Interval (Minuten):");
        pane.add(linterval,2,4);
        final TextField interval = new TextField();
        interval.setText("30");
        pane.add(interval, 3, 4);


        CheckBox mo = new CheckBox();
        mo.setSelected(true);
        CheckBox di = new CheckBox();
        di.setSelected(true);
        CheckBox mi = new CheckBox();
        mi.setSelected(true);
        CheckBox don = new CheckBox();
        don.setSelected(true);
        CheckBox fr = new CheckBox();
        fr.setSelected(true);
        CheckBox sa = new CheckBox();
        CheckBox so = new CheckBox();

        Label lmo = new Label("Mo");
        Label ldi = new Label("Di");
        Label lmi = new Label("Mi");
        Label ldo = new Label("Do");
        Label lfr = new Label("Fr");
        Label lsa = new Label("Sa");
        Label lso = new Label("So");
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(lmo);
        hbox.getChildren().add(mo);
        hbox.getChildren().add(ldi);
        hbox.getChildren().add(di);
        hbox.getChildren().add(lmi);
        hbox.getChildren().add(mi);
        hbox.getChildren().add(ldo);
        hbox.getChildren().add(don);
        hbox.getChildren().add(lfr);
        hbox.getChildren().add(fr);
        hbox.getChildren().add(lsa);
        hbox.getChildren().add(sa);
        hbox.getChildren().add(lso);
        hbox.getChildren().add(so);
        pane.add(hbox, 0, 5, 4,5);

        HBox genbox = new HBox();
        genbox.setAlignment(Pos.CENTER);
        Button createbtn = new Button("Generieren und speichern");
        genbox.getChildren().add(createbtn);
        pane.add(genbox,0,8,4,8);




        final Text taxMessage = new Text();
        pane.add(taxMessage, 1, 6);

        atag.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(atag.isSelected()){
                    mstart.setDisable(false);
                    mend.setDisable(false);
                }else{
                    mstart.setDisable(true);
                    mend.setDisable(true);
                }
            }
        });

        createbtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Zeiterfassung speichern");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter(".cvs", "*.cvs")
                );
                //System.out.println(pic.getId());
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {

                    String[] spl = daystart.getText().split(":");
                    LocalTime ds = LocalTime.of(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
                    spl = dayend.getText().split(":");
                    LocalTime de = LocalTime.of(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
                    spl = pstart.getText().split(":");
                    LocalTime ps = LocalTime.of(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
                    spl = pend.getText().split(":");
                    LocalTime pe = LocalTime.of(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
                    int h = Integer.parseInt(hours.getText());
                    LocalTime iv = LocalTime.of(0, Integer.parseInt(interval.getText()));
                    boolean[] wd = {
                            so.isSelected(),
                            mo.isSelected(),
                            di.isSelected(),
                            mi.isSelected(),
                            don.isSelected(),
                            fr.isSelected(),
                            sa.isSelected()
                    };
                    int mst = 1;
                    int men = -1;
                    if(atag.isSelected()){
                        mst=Integer.parseInt(mstart.getText());
                        men=Integer.parseInt(mend.getText());
                    }
                    tt = new TimeTable(months.getSelectionModel().getSelectedIndex(), Integer.parseInt(year.getText()), ds, de, ps, pe, iv, h, wd, mst, men);
                    tt.printResults(file);

                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
