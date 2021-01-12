package org.WetterApp.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.WetterApp.Data.Interfaces.IDbModelContext;
import org.WetterApp.Data.Interfaces.IMainControllerSettingsContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.InvalidWetterDatenModel;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.WetterApp.Simulation.WetterSensor;

import java.net.URL;
import java.time.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class MainController extends BaseViewController implements IObserver<WetterDatenModel> {

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
    @FXML
    private Text header;


    private final IDbModelContext context;
    private int selectedSensor;
    private final ArrayList<LineChart> charts = new ArrayList<>();
    private WetterDatenModel modifiableWetterDaten;

    public MainController() {
        this.context = IDbModelContext.MODEL_CONTEXT;
        WetterSensorModel sensor = null;
        try(IMainControllerSettingsContext mContext = context.getMainControllerSettingsContext()){
            sensor = mContext.getSelectedSensorId();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        if(sensor != null) selectedSensor = sensor.getId();
        else {
            selectedSensor = 1;
        }


        WetterStationController.getInstance().registerObserver(this);
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        charts.add(tempChart);
        charts.add(windChart);
        charts.add(druckChart);
        charts.add(feuchtChart);
        charts.add(co2Chart);

        header.setStyle("-fx-font: 24 arial;");

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
            xAxis.setTickMarkVisible(true);
            xAxis.setMinorTickVisible(true);
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

            try(IWetterDatenContext wdContext =context.getWetterdatenContext()){
                modifiableWetterDaten = wdContext.getWetterdaten(selectedSensor,offsetDateTime.toEpochSecond());
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
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

    //Aktualisiert den View mit den neuen Wetterdaten
    public void singleUpdate(WetterDatenModel daten) {

        aktuelleTemperatur.setText(Double.toString(daten.getTempInC()));
        aktuelleWind.setText(Double.toString(daten.getWindGeschw()));
        aktuelleFeucht.setText(Double.toString(daten.getLuftFeuchtigkeit()));
        aktuellerDruck.setText(Double.toString(daten.getLuftDruck()));
        aktuelleCo2.setText(Double.toString(daten.getCo2()));

        if(daten.getClass().equals(InvalidWetterDatenModel.class)) return;

        ArrayList<XYChart.Data>[] wetterDaten = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
        for (int i = 0;i < 5;i++) {
            wetterDaten[i].addAll(((XYChart.Series)(charts.get(i).getData().get(0))).getData());
        }

        if (wetterDaten[0].size() > 35040) for (ArrayList<XYChart.Data> data : wetterDaten) data.remove(0);

        XYChart.Data[] dataSet = createDataSet(daten);

        for (int i = 0; i < 5; i++) wetterDaten[i].add(dataSet[i]);
        redrawGraph(wetterDaten);
    }

    //Laedt die Wetterdaten des letzten Jahres oder alle bisherigen, wenn noch in nicht
    //genug vorhanden sind, und fuegt die Graphen in den View ein
    @FXML
    public void ladeWetterdatenDesLetztenJahres() {
        header.setText("Loading...");
        ArrayList<WetterDatenModel> wetterdaten = new ArrayList<>();
        try(IWetterDatenContext wdContext = context.getWetterdatenContext()){
            wetterdaten = wdContext.getWetterdaten(selectedSensor, OffsetDateTime.now().minusYears(1).toEpochSecond(), OffsetDateTime.now().toEpochSecond());
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        ArrayList<XYChart.Data>[] wetterDaten = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};

        for (WetterDatenModel daten : wetterdaten) {
            XYChart.Data[] dataSet = createDataSet(daten);
            for (int i = 0; i < 5; i++) wetterDaten[i].add(dataSet[i]);
        }
        redrawGraph(wetterDaten);
    }

    //Aktualisiert die Graphen
    //Param ein Array mit den 5 ArrayListen der Punkte
    @FXML
    private void redrawGraph(ArrayList<XYChart.Data>[] wetterDaten) {
        for (int i = 0; i < 5; i++) {
            try {
                charts.get(i).getData().clear();
                XYChart.Series series = new XYChart.Series();
                //Color Graph maybe ?
                String rgb = switch (i) {
                    case 0 -> "#ff0000";
                    case 1 -> "#034100";
                    case 2 -> "#dce4ec";
                    case 3 -> "#396da0";
                    case 4 -> "#fde64b";
                    default -> "";
                };
                series.getData().setAll(wetterDaten[i]);

                charts.get(i).getData().setAll(series);
                ((XYChart.Series)(charts.get(i).getData().get(0))).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: " + rgb + ";");
            }catch(NullPointerException ex)
            {
                System.out.println("Fehler: Kein Graph vorhanden in Tabelle Nummer: " + i);
            }
        }
    }

    //Helfer Methode, die einen Punkt fuer jedes Diagramm an Hand der Wetterdaten erstellt
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

    //Action, wenn ein neuer Sensor ausgewaehlt wird
    @FXML
    public void sensorSelected() {
        selectedSensor = wetterSensor.getSelectionModel().getSelectedItem().getId();

        getAlteWetterdatenModel();
        resetChange();
        ladeWetterdatenDesLetztenJahres();

        try(IMainControllerSettingsContext mContext = context.getMainControllerSettingsContext()){
            mContext.aendere(selectedSensor);
            mContext.saveChanges();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    //Action, wenn alte Wetterdaten geaendert werden
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
            testMessage.setText("invalid input");
        }
        else{
            testMessage.setText("");
        }

    }

    //Speichert die Aenderung in die DB
    @FXML
    public void aendereWetterdaten() {
        if(modifiableWetterDaten != null && !modifiableWetterDaten.getClass().equals(InvalidWetterDatenModel.class)){
            try(IWetterDatenContext wdContext = context.getWetterdatenContext()){
                wdContext.aendereWetterdaten(modifiableWetterDaten);
                wdContext.saveChanges();
                resetChange();
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    //laedt die letzte Messung des ausgewaehlten Wettersensors
    @FXML
    public void getAlteWetterdatenModel() {
        //Lade den letzen eintrag der Wetterdaten und fuege ihn den view ein
        WetterDatenModel aktuelleWetterDaten = null;
        try(IWetterDatenContext wdContext = context.getWetterdatenContext()){
            aktuelleWetterDaten = wdContext.getWetterdaten(selectedSensor);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
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
        header.setText("Wetterstation");
    }

    //Resets die zu aenderenden Wetterdaten
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

    //Laedt die Wettersensoren aus der DB und fuegt sie in die ComboBox ein
    @FXML
    public void getSensoren() {
        ArrayList<WetterSensorModel> sensoren = new ArrayList<>();
        try (IWetterSensorContext wsContext = context.getWetterSensorContext()){
            sensoren = wsContext.ladeWetterSensoren();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        int id = 0;
        for (int i = 0; i < sensoren.size(); i++) {
            wetterSensor.getItems().add(sensoren.get(i));
            if(selectedSensor == sensoren.get(i).getId()) id = 0;
        }

        wetterSensor.getSelectionModel().select(id);
        longitude.setText(Double.toString(sensoren.get(id).getGpsXCoord()));
        latitude.setText(Double.toString( sensoren.get(id).getGpsYCoord()));

    }

    //Fuegt einen neuen Wettersensor in die DB
    public void neuerWettersensor() {
        WetterSensor sensor = new WetterSensor();
        WetterSensorModel sensorModel = new WetterSensorModel();
        //lies Coord aus dem view
        try (IWetterSensorContext wsContext = context.getWetterSensorContext() ){
            sensorModel = wsContext.neuerWettersensor(sensorModel);
            wsContext.saveChanges();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        sensor.setId(sensorModel.getId());
        sensor.registerObserver(WetterStationController.getInstance());
        sensor.start();

        //update sensorenlist im view
    }

    //Loescht einen Wettersensor aus der DB
    public void deleteSensor() {
        //lies ID vom view
        try(IWetterSensorContext wsContext = context.getWetterSensorContext() ){
            wsContext.deleteWettersensor(0);
            wsContext.saveChanges();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

}
