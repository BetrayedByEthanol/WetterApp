package org.WetterApp.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.WetterApp.Data.IDbContext;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.InvalidWetterDatenModel;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.WetterApp.Simulation.WetterSensor;
import org.WetterApp.WetterStation;
import java.net.URL;
import java.time.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class MainController extends BaseController implements IObserver<WetterDatenModel> {

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
    private ComboBox<WetterSensorModel> wetterSensor;
    @FXML
    private Text testMessage;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> alteStunden;
    @FXML
    private ComboBox<Integer> alteMinuten;
    @FXML
    private Text aktuelleTemperatur;
    @FXML
    private Text aktuelleWind;
    @FXML
    private Text aktuelleFeucht;
    @FXML
    private Text aktuellerDruck;
    @FXML
    private Text aktuelleCo2;
    @FXML
    private TextField alteTemperatur;
    @FXML
    private TextField alterWind;
    @FXML
    private TextField alteFeucht;
    @FXML
    private TextField alterDruck;
    @FXML
    private TextField alteCo2;
    @FXML
    private TextField latitude;
    @FXML
    private TextField longitude;



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
        wetterSensor.setConverter(new StringConverter<WetterSensorModel>() {
            @Override
            public String toString(WetterSensorModel object) {
                if(object != null) return object.getName();
                else return "";
            }

            @Override
            public WetterSensorModel fromString(String string) {
                return null;
            }
        });

        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {

            String newText = change.getControlNewText();
            if (newText.matches("(\\d+)?(\\.)?(\\d+)?") || newText.equals("")) {
                return change;
            }
            return null;
        };

        alteTemperatur.setTextFormatter(new TextFormatter<Double>(new DoubleStringConverter(),null, doubleFilter ));
        alterWind.setTextFormatter(new TextFormatter<Double>(new DoubleStringConverter(),null,doubleFilter ));
        alteFeucht.setTextFormatter(new TextFormatter<Double>(new DoubleStringConverter(),null,doubleFilter ));
        alterDruck.setTextFormatter(new TextFormatter<Double>(new DoubleStringConverter(),null,doubleFilter ));
        alteCo2.setTextFormatter(new TextFormatter<Double>(new DoubleStringConverter(),null,doubleFilter ));

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || today.compareTo(date) < 0 );
            }
        });

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
    public void datePicked(){
        LocalDate date =  datePicker.getValue();
        int h = 23;
        int min = 45;
        alteStunden.getItems().clear();
        alteMinuten.getItems().clear();
        LocalDateTime now = LocalDateTime.now();
        if(date.compareTo(now.toLocalDate()) == 0)
        {
            h = now.getHour();
        }
        for(int i = 0; i <= h; i++) alteStunden.getItems().add(i);
        for(int i = 0; i <= min; i += 15) alteMinuten.getItems().add(i);

        alteStunden.getSelectionModel().clearSelection();
        alteMinuten.getSelectionModel().clearSelection();
    }

    @FXML
    public void hourSelected(){
        LocalDateTime now = LocalDateTime.now();
        if(datePicker.getValue().compareTo(now.toLocalDate())== 0 && alteStunden.getSelectionModel().getSelectedItem().intValue() == now.getHour()){
            alteMinuten.getItems().clear();
            alteMinuten.getSelectionModel().clearSelection();
            int min = now.getMinute();
            for(int i = 0; i <= min; i += 15) alteMinuten.getItems().add(i);
        }
        ladeAlteWetterDaten();
    }

    @FXML
    public void ladeAlteWetterDaten(){
        if(alteStunden.getSelectionModel().getSelectedItem() != null && alteMinuten.getSelectionModel().getSelectedItem() != null){
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonthValue(),datePicker.getValue().getDayOfMonth(),alteStunden.getSelectionModel().getSelectedItem().intValue(),alteMinuten.getSelectionModel().getSelectedItem().intValue());
            OffsetDateTime offsetDateTime = dateTime.atOffset(OffsetDateTime.now().getOffset());
            modifiableWetterDaten = context.getWetterdatenContext().getWetterdaten(selectedSensor,offsetDateTime.toEpochSecond());

            alteTemperatur.setText(Double.toString(modifiableWetterDaten.getTempInC()));
            alterWind.setText(Double.toString(modifiableWetterDaten.getWindGeschw()));
            alteFeucht.setText(Double.toString(modifiableWetterDaten.getLuftFeuchtigkeit()));
            alterDruck.setText(Double.toString(modifiableWetterDaten.getLuftDruck()));
            alteCo2.setText(Double.toString(modifiableWetterDaten.getCo2()));
        }
    }

    @Override
    @FXML
    public synchronized void update(WetterDatenModel daten) {
        if (selectedSensor == daten.getGemessenVon().getId()) {
            Platform.runLater(() -> singleUpdate(daten));
        }
    }

    public void singleUpdate(WetterDatenModel daten) {

        aktuelleTemperatur.setText(Double.toString(daten.getTempInC()));
        aktuelleWind.setText(Double.toString(daten.getWindGeschw()));
        aktuelleFeucht.setText(Double.toString(daten.getLuftFeuchtigkeit()));
        aktuellerDruck.setText(Double.toString(daten.getLuftDruck()));
        aktuelleCo2.setText(Double.toString(daten.getCo2()));

        if(daten.getClass().equals(InvalidWetterDatenModel.class)) return;

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
                String rgb = "";
                switch (i){
                    case 0:
                        rgb = "#ff0000";
                        break;
                    case 1:
                        rgb = "#034100";
                        break;
                    case 2:
                        rgb = "#dce4ec";
                        break;
                    case 3:
                        rgb = "#89cff0";
                        break;
                    case 4:
                        rgb = "#fde64b";
                        break;
                }
                series.getData().setAll(wetterDaten[i]);

                charts.get(i).getData().setAll(series);

                ((XYChart.Series)(charts.get(i).getData().get(0))).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: " + rgb + ";");
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
        selectedSensor = wetterSensor.getSelectionModel().getSelectedItem().getId();

        getAlteWetterdatenModel();

        ladeWetterdatenDesLetztenJahres();

        context.getMainControllerSettinContext().aendere(selectedSensor);
    }

    @FXML
    public void onAlteWetterdatenChange(){
        if(alteTemperatur.getText().isEmpty() || alterWind.getText().isEmpty() || alteFeucht.getText().isEmpty() || alterDruck.getText().isEmpty() || alteCo2.getText().isEmpty()) return;
        modifiableWetterDaten.setTempInC(Double.parseDouble(alteTemperatur.getText()));
        modifiableWetterDaten.setWindGeschw(Double.parseDouble(alterWind.getText()));
        modifiableWetterDaten.setLuftFeuchtigkeit(Double.parseDouble(alteFeucht.getText()));
        modifiableWetterDaten.setLuftDruck(Double.parseDouble(alterDruck.getText()));
        modifiableWetterDaten.setCo2(Double.parseDouble(alteCo2.getText()));

        modifiableWetterDaten = WetterDatenValidation.validate(modifiableWetterDaten);

        if(modifiableWetterDaten.getClass().equals(InvalidWetterDatenModel.class)){
            testMessage.setText("oh no");
        }
        else{
            testMessage.setText("mah Man");
        }

    }

    @FXML
    public void aendereWetterdaten() {
        if(!modifiableWetterDaten.getClass().equals(InvalidWetterDatenModel.class))
        context.getWetterdatenContext().aendereWetterdaten(modifiableWetterDaten);
    }

    @FXML
    public void getAlteWetterdatenModel() {
        //Lade den letzen eintrag der Wetterdaten und fuege ihn den view ein
         modifiableWetterDaten = aktuelleWetterDaten = context.getWetterdatenContext().getWetterdaten(selectedSensor);

        //if not null aktualisiere den view else sage warte
        if (aktuelleWetterDaten != null) {
            aktuelleTemperatur.setText(Double.toString(aktuelleWetterDaten.getTempInC()));
            aktuelleWind.setText(Double.toString(aktuelleWetterDaten.getWindGeschw()));
            aktuelleFeucht.setText(Double.toString(aktuelleWetterDaten.getLuftFeuchtigkeit()));
            aktuellerDruck.setText(Double.toString(aktuelleWetterDaten.getLuftDruck()));
            aktuelleCo2.setText(Double.toString(aktuelleWetterDaten.getCo2()));
        } else {
            aktuelleTemperatur.setText("noch keine Messung vorhanden");
        }
    }

    @FXML
    public void resetChange(){
        alteTemperatur.setText("");
        alterWind.setText("");
        alteFeucht.setText("");
        alterDruck.setText("");
        alteCo2.setText("");
        alteStunden.getSelectionModel().clearSelection();
        alteMinuten.getSelectionModel().clearSelection();
        datePicker.getEditor().clear();
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
            wetterSensor.getItems().add(sensor);
        }
        wetterSensor.getSelectionModel().select(selectedSensor);

        longitude.setText(Double.toString(sensoren.get(selectedSensor).getGpsXCoord()));
        latitude.setText(Double.toString( sensoren.get(selectedSensor).getGpsYCoord()));

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
