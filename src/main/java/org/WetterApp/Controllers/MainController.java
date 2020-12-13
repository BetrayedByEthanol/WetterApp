package org.WetterApp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import javafx.util.Pair;
import org.WetterApp.Data.IDbContext;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.WetterApp.WetterStation;

import java.awt.*;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController extends BaseController implements IObserver<WetterDatenModel> {

    private IDbContext context;
    private int selectedSensor = 1;

    @FXML
    private TextField WetterSensorChoice = new TextField();
    @FXML
    private LineChart tempChart = new LineChart(new NumberAxis(1, 12, 1), new NumberAxis(0, 50, 5));
    @FXML
    private LineChart co2Chart = new LineChart(new NumberAxis(1, 12, 1), new NumberAxis(0, 50, 5));
    @FXML
    private LineChart windChart = new LineChart(new NumberAxis(1, 12, 1), new NumberAxis(0, 50, 5));
    @FXML
    private LineChart feuchtChart = new LineChart(new NumberAxis(1, 12, 1), new NumberAxis(0, 50, 5));
    @FXML
    private LineChart druckChart = new LineChart(new NumberAxis(1, 12, 1), new NumberAxis(0, 50, 5));
    @FXML
    private ComboBox<String> wetterSensor = new ComboBox<String>();
    private XYChart.Series chartDaten = new XYChart.Series();


    public MainController() {
        this.context = IDbContext.DB_CONTEXT;

        //Lade Settings
        selectedSensor = context.getMainControllerSettinContext().getSelectedSensorId();


        WetterStation.getInstance().registerObserver(this);
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        getSensoren();

        getAlteWetterdatenModel();

        ladeWetterdatenDesLetztenJahres();

        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("test");
        wetterSensor.setItems(list);
        wetterSensor.getItems().add("Berlin Ost");
        super.initialize(location, resources);
    }

    @FXML
    public void doSmth() {
        WetterSensorChoice.setText("NO");
        wetterSensor.getItems().add("NOOOOOO");
    }

    @Override
    public synchronized void update(WetterDatenModel daten) {
        if (selectedSensor == (daten).getGemessenVon()) {
            WetterSensorChoice.setText(Double.toString(daten.getTempInC()));
        }

    }

    @FXML
    public void sensorSelected() {
        //platzhalter fuer view element value
        selectedSensor = 2;

        getAlteWetterdatenModel();

        ladeWetterdatenDesLetztenJahres();

        context.getMainControllerSettinContext().aendere(selectedSensor);
    }

    public void aendereWetterdaten() {
        WetterDatenModel model = new WetterDatenModel();

        //lies Werte vom View;

        context.getWetterdatenContext().aendereWetterdaten(model);
    }

    public void getAlteWetterdatenModel()
    {
        //Lade den letzen eintrag der Wetterdaten und fuege ihn den view ein
        WetterDatenModel model =  context.getWetterdatenContext().getWetterdaten(selectedSensor);

        //if not null aktualisiere den view else sage warte
        if(model != null){

        }else{

        }
    }

    public void ladeWetterdatenDesLetztenJahres() {
        //Start zeit now - 1 jahr in unix time, end now
        updateView(context.getWetterdatenContext().getWetterdaten(selectedSensor, 0, 100));
    }

    public void getSensoren() {
        //fuege sensoren in den view ein
        ArrayList<WetterSensorModel> sensoren = context.getWetterSensorContext().ladeWetterSensoren();
    }

    public void neuerWettersensor() {
        WetterSensorModel sensor = new WetterSensorModel();

        //lies Coord aus dem view

        sensor.setId(context.getWetterSensorContext().neuerWettersensor(sensor));
        sensor.registerObserver(WetterStation.getInstance());
        sensor.start();

        //update sensorenlist im view
    }

    public void deleteSensor() {
        //lies ID vom view
        context.getWetterSensorContext().deleteWettersensor(0);
    }

    public void updateView(ArrayList<WetterDatenModel> wetterdaten)
    {
        for(WetterDatenModel daten : wetterdaten)
        {
            tempChart.getData().add(new XYChart.Data(daten.getZeitDesMessens().toEpochSecond() - OffsetDateTime.now().toEpochSecond() - 31536000,daten.getTempInC()));

        }
    }

}
