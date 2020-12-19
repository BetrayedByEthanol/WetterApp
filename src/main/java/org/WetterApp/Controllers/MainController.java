package org.WetterApp.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.WetterApp.Data.IDbContext;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.WetterApp.WetterStation;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController extends BaseController implements IObserver<WetterDatenModel> {

    private IDbContext context;
    private int selectedSensor = 1;

    @FXML
    private TextField WetterSensorChoice = new TextField();
    @FXML
    private LineChart tempChart;// = new LineChart(new NumberAxis(0, 31536000, 2628000), new NumberAxis(0, 50, 5));
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
    @FXML
    private TextField testMessage = new TextField();

    public MainController() {
        this.context = IDbContext.DB_CONTEXT;

        //Lade Settings
        selectedSensor = context.getMainControllerSettinContext().getSelectedSensorId();


        WetterStation.getInstance().registerObserver(this);
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        //NumberAxis xAxis = new NumberAxis();
        ladeWetterdatenDesLetztenJahres();
        NumberAxis xAxis = (NumberAxis) tempChart.getXAxis();

        xAxis.setTickLabelRotation(270);

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public String toString(Number number) {
                String result = "";
                    result = OffsetDateTime.now().plusMonths(number.longValue() - 12).getMonth().name();
                return result;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        xAxis.setTickLabelsVisible(true);


        getSensoren();

        getAlteWetterdatenModel();

        //ladeWetterdatenDesLetztenJahres();

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
    @FXML
    public synchronized void update(WetterDatenModel daten) {
        if (selectedSensor == (daten).getGemessenVon()) {
            Platform.runLater(()->{
                singleUpdate(daten);
            });
        }

    }

    public void singleUpdate(WetterDatenModel daten)
    {
        WetterSensorChoice.setText(Double.toString(daten.getTempInC()));

        XYChart.Series tempSeries = (XYChart.Series)(tempChart.getData().get(0));
        XYChart.Series windSeries = (XYChart.Series)(windChart.getData().get(0));
        XYChart.Series druckSeries = (XYChart.Series)(druckChart.getData().get(0));
        XYChart.Series feuchtSeries = (XYChart.Series)(feuchtChart.getData().get(0));
        XYChart.Series co2Series = (XYChart.Series)(co2Chart.getData().get(0));

        if(((XYChart.Series)tempChart.getData().get(0)).getData().size() > 35040)
        {
            tempSeries.getData().remove(0);
            windSeries.getData().remove(0);
            druckSeries.getData().remove(0);
            feuchtSeries.getData().remove(0);
            co2Series.getData().remove(0);
        }
        long time = daten.getZeitDesMessens().toEpochSecond() - (OffsetDateTime.now().minusYears(1).toEpochSecond() - (OffsetDateTime.now().toEpochSecond() % 900) );
        double timeValue = Double.parseDouble(Long.toString(time)) / 2628000;
        Rectangle rect = new Rectangle(0, 0);
        rect.setVisible(false);

        XYChart.Data tempData = new XYChart.Data(timeValue, daten.getTempInC());
        XYChart.Data windData = new XYChart.Data(timeValue,daten.getWindGeschw());
        XYChart.Data druckData = new XYChart.Data(timeValue,daten.getLuftDruck());
        XYChart.Data feuchtData = new XYChart.Data(timeValue,daten.getLuftFeuchtigkeit());
        XYChart.Data co2Data = new XYChart.Data(timeValue,daten.getCo2());

        tempData.setNode(rect);
        windData.setNode(rect);
        druckData.setNode(rect);
        feuchtData.setNode(rect);
        co2Data.setNode(rect);

        tempSeries.getData().add(tempData);
        windSeries.getData().add(windData);
        druckSeries.getData().add(druckData);
        feuchtSeries.getData().add(feuchtData);
        co2Series.getData().add(co2Data);


        //Check labels
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
        WetterDatenModel model = context.getWetterdatenContext().getWetterdaten(selectedSensor);

        //if not null aktualisiere den view else sage warte
        if (model != null) {

        } else {

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

    @FXML
    public void updateView(ArrayList<WetterDatenModel> wetterdaten) {
        testMessage.setText("lade...");

        LinkedList<XYChart.Data>[] dtoList = new LinkedList[5];
        for(int i = 0; i < 5; i++ ) dtoList[i] = new LinkedList<XYChart.Data>();
        for (WetterDatenModel daten : wetterdaten)
        {
            long time = daten.getZeitDesMessens().toEpochSecond() - (OffsetDateTime.now().minusYears(1).toEpochSecond() - (OffsetDateTime.now().toEpochSecond() % 900) );
            double timeValue = Double.parseDouble(Long.toString(time)) / 2628000;
            Rectangle rect = new Rectangle(0, 0);
            rect.setVisible(false);
            XYChart.Data tempData = new XYChart.Data(timeValue, daten.getTempInC());
            XYChart.Data windData = new XYChart.Data(timeValue, daten.getWindGeschw());
            XYChart.Data druckData = new XYChart.Data(timeValue, daten.getLuftDruck());
            XYChart.Data feuchtData = new XYChart.Data(timeValue, daten.getLuftFeuchtigkeit());
            XYChart.Data co2Data = new XYChart.Data(timeValue, daten.getCo2());
            tempData.setNode(rect);
            windData.setNode(rect);
            druckData.setNode(rect);
            feuchtData.setNode(rect);
            co2Data.setNode(rect);
            dtoList[0].add(tempData);
            dtoList[1].add(windData);
            dtoList[2].add(druckData);
            dtoList[3].add(feuchtData);
            dtoList[4].add(co2Data);
        }
        tempChart.getData().removeAll(tempChart.getData());
        windChart.getData().removeAll(windChart.getData());
        druckChart.getData().removeAll(druckChart.getData());
        feuchtChart.getData().removeAll(feuchtChart.getData());
        co2Chart.getData().removeAll(co2Chart.getData());

        XYChart.Series tempSeries = new XYChart.Series();
        XYChart.Series windSeries = new XYChart.Series();
        XYChart.Series druckSeries = new XYChart.Series();
        XYChart.Series feuchtSeries = new XYChart.Series();
        XYChart.Series co2Series = new XYChart.Series();

        tempSeries.getData().setAll(dtoList[0]);
        windSeries.getData().setAll(dtoList[1]);
        druckSeries.getData().setAll(dtoList[2]);
        feuchtSeries.getData().setAll(dtoList[3]);
        co2Series.getData().setAll(dtoList[4]);

        tempChart.getData().setAll(tempSeries);
        windChart.getData().setAll(windSeries);
        druckChart.getData().setAll(druckSeries);
        feuchtChart.getData().setAll(feuchtSeries);
        co2Chart.getData().setAll(co2Series);

        testMessage.setText(Long.toString(wetterdaten.get(0).getZeitDesMessens().toEpochSecond()));
        //testMessage.setText(Integer.toString(tempSeries.getData().size()));
    }

}
