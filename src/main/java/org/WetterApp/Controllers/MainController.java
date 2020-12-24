package org.WetterApp.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import org.WetterApp.Data.IDbContext;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.WetterApp.Simulation.WetterSensor;
import org.WetterApp.WetterStation;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController extends BaseController implements IObserver<WetterDatenModel> {

    @FXML
    private TextField WetterSensorChoice;
    @FXML
    private LineChart tempChart;
    @FXML
    private LineChart co2Chart;
    @FXML
    private LineChart windChart;
    @FXML
    private LineChart feuchtChart;
    @FXML
    private LineChart druckChart;
    @FXML
    private ComboBox<String> wetterSensor;
    @FXML
    private TextField testMessage;

    private final IDbContext context;
    private int selectedSensor;
    private final ArrayList<LineChart> charts = new ArrayList<>();
    private final ArrayList<XYChart.Data>[] wetterDaten = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private WetterDatenModel modifiableWetterDaten;
    private WetterDatenModel aktuelleWetterDaten;
    private ArrayList<WetterSensorModel> sensoren = new ArrayList<>();

    public MainController() {
        this.context = IDbContext.DB_CONTEXT;

        selectedSensor = context.getMainControllerSettinContext().getSelectedSensorId().getId();

        WetterStation.getInstance().registerObserver(this);
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        charts.add(tempChart);
        charts.add(windChart);
        charts.add(druckChart);
        charts.add(feuchtChart);
        charts.add(co2Chart);

        Platform.runLater(() -> {
            ladeWetterdatenDesLetztenJahres();
            getSensoren();
            getAlteWetterdatenModel();
        });


        for (LineChart chart : charts) {
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            xAxis.setTickLabelRotation(270);
            xAxis.setTickLabelsVisible(false);
            xAxis.setMinorTickVisible(false);
            xAxis.setTickMarkVisible(false);
            xAxis.setMinorTickVisible(false);
        }
        super.initialize(location, resources);
    }

    @FXML
    public void doSmth() {
        WetterSensorChoice.setText("NO");
        wetterSensor.getItems().add("NOOOOOO");
    }

    @Override
    @FXML
    public synchronized void update(WetterDatenModel daten) {
        if (selectedSensor == daten.getGemessenVon().getId()) {
            Platform.runLater(() -> singleUpdate(daten));
        }

    }

    public void singleUpdate(WetterDatenModel daten) {
        if (wetterDaten[0].size() > 35040) for (ArrayList<XYChart.Data> data : wetterDaten) data.remove(0);

        XYChart.Data[] dataSet = createDataSet(daten);

        for (int i = 0; i < 5; i++) wetterDaten[i].add(dataSet[i]);

        redrawGraph();
    }

    @FXML
    private void redrawGraph() {
        for (int i = 0; i < 5; i++) {
            try {
                charts.get(i).getData().removeAll(charts.get(i).getData());
                XYChart.Series series = new XYChart.Series();
                //Color Graph maybe ?
                series.getData().setAll(wetterDaten[i]);
                charts.get(i).getData().setAll(series);
            }catch(NullPointerException ex)
            {
                System.out.println("Fehler: Kein Graph vorhanden in Tabelle Nummer: " + i);
            }
        }
    }

    private XYChart.Data[] createDataSet(WetterDatenModel daten) {
        long timeValue = daten.getZeitDesMessens().toEpochSecond() - (OffsetDateTime.now().minusYears(1).toEpochSecond() - (OffsetDateTime.now().toEpochSecond() % 900));
        //double timeValue = ((double) time) / 2628000;
        Rectangle rect = new Rectangle(0, 0);
        rect.setVisible(false);

        XYChart.Data[] dataSet = new XYChart.Data[]
                {
                        new XYChart.Data<>(timeValue, daten.getTempInC()),
                        new XYChart.Data<>(timeValue, daten.getWindGeschw()),
                        new XYChart.Data<>(timeValue, daten.getLuftDruck()),
                        new XYChart.Data<>(timeValue, daten.getLuftFeuchtigkeit()),
                        new XYChart.Data<>(timeValue, daten.getCo2())
                };

        for (XYChart.Data data : dataSet) data.setNode(rect);

        return dataSet;
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

    public void getAlteWetterdatenModel() {
        //Lade den letzen eintrag der Wetterdaten und fuege ihn den view ein
         modifiableWetterDaten = aktuelleWetterDaten = context.getWetterdatenContext().getWetterdaten(selectedSensor);

        //if not null aktualisiere den view else sage warte
        if (aktuelleWetterDaten != null) {

        } else {

        }
    }

    public void ladeWetterdatenDesLetztenJahres() {
        //Start zeit now - 1 jahr in unix time, end now
        ArrayList<WetterDatenModel> wetterdaten = context.getWetterdatenContext().getWetterdaten(selectedSensor, 0, 100);

        for (WetterDatenModel daten : wetterdaten) {
            XYChart.Data[] dataSet = createDataSet(daten);
            for (int i = 0; i < 5; i++) wetterDaten[i].add(dataSet[i]);
        }
        redrawGraph();
    }

    public void getSensoren() {
        //fuege sensoren in den view ein
        sensoren = context.getWetterSensorContext().ladeWetterSensoren();
        for (WetterSensorModel sensor : sensoren) {
            wetterSensor.getItems().add(sensor.getName());
        }
    }

    public void neuerWettersensor() {
        WetterSensor sensor = new WetterSensor();
        WetterSensorModel sensorModel = new WetterSensorModel();
        //lies Coord aus dem view

        sensorModel = context.getWetterSensorContext().neuerWettersensor(sensorModel);
        sensor.setId(sensorModel.getId());
        sensor.registerObserver(WetterStation.getInstance());
        sensor.start();

        //update sensorenlist im view
    }

    public void deleteSensor() {
        //lies ID vom view
        context.getWetterSensorContext().deleteWettersensor(0);
    }

}
