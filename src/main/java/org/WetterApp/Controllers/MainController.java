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
    private ComboBox<String> wetterSensor = new ComboBox<String>();
    @FXML
    private TextField testMessage = new TextField();
    
    private ArrayList<LineChart> charts = new AurrayList<LineChart>();

    public MainController() {
        this.context = IDbContext.DB_CONTEXT;

        //Lade Settings
        selectedSensor = context.getMainControllerSettinContext().getSelectedSensorId();
        
        
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
    
        Platform.runLater(()->{
               ladeWetterdatenDesLetztenJahres();
               getSensoren();
               getAlteWetterdatenModel();
            });

        //Refactort into new Class
        StringConverter<Number> monthConverter = new StringConverter<Number>() {

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
        };
        
        for(LineChart chart : charts)
        { 
					NumberAxis xAxis = (NumberAxis) chart.getXAxis();
          xAxis.setTickLabelRotation(270);
          xAxis.setTickLabelFormatter(monthConverter);
          xAxis.setTickLabelsVisible(true);
        }
        super.initialize(location, resources);
    }

    @FXML
    public void doSmth() {
        WetterSensorChoice.setText("NO");
        wetterSensor.getItems().add("NOOOOOO");
        for(LineChart chart : charts) chart = new LineChart();
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
        if(((XYChart.Series)tempChart.getData().get(0)).getData().size() > 35040) for(LineChart chart : charts) chart.getData().get(0).getData().remove(0);
              
        XYChart.Data[] data = createDataSet(daten);

        for(int i = 0; i < 5; i++) charts[i].getData().get(0).getData().add(data[i]);

        //Check labels
    }

    private XYChart.Data[] createDataSet(WetterDatenModel daten)
    {
        long time = daten.getZeitDesMessens().toEpochSecond() - (OffsetDateTime.now().minusYears(1).toEpochSecond() - (OffsetDateTime.now().toEpochSecond() % 900) );
        double timeValue = ((double) time)) / 2628000;
        Rectangle rect = new Rectangle(0, 0);
        rect.setVisible(false);

        XYChart.Data[] dataSet = new XYChart.Data[] 
        { 
          new XYChart.Data(timeValue, daten.getTempInC()), 
          new XYChart.Data(timeValue,daten.getWindGeschw()), 
          new XYChart.Data(timeValue,daten.getLuftDruck()), 
          new XYChart.Data(timeValue,daten.getLuftFeuchtigkeit()), 
          new XYChart.Data(timeValue,daten.getCo2())
        };
        
        for(XYChart.Data data : dataSet) data.setNode(rect);
             
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
            XYChart.Data[] dataSet = createDataSet(daten);
            for(int i = 0; i < 5; i++ ) dtoList[i].add(dataSet[i]);
        }
        
        for(int i = 0; i < 5; i++)
        {
            charts[i].getData().removeAll(charts[i].getData());
            XYChart.Series series = new XYChart.Series();
            series.getData().setAll(dtoList[i]);
            charts[i].getData().setAll(series);
        }
    }

}
