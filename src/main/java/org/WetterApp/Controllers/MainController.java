package org.WetterApp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.Pair;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.WetterStation;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController extends BaseController implements IObserver<WetterDatenModel>
{

	private IDbContext context;
    private int selectedSensor = 1;
	
	@FXML
    private TextField WetterSensorChoice = new TextField();
    @FXML
    private LineChart tempChart = new LineChart(new NumberAxis(1,12,1), new NumberAxis(0,50,5));
    @FXML
    private LineChart co2Chart = new LineChart(new NumberAxis(1,12,1), new NumberAxis(0,50,5));
    @FXML
    private LineChart windChart = new LineChart(new NumberAxis(1,12,1), new NumberAxis(0,50,5));
    @FXML
    private LineChart feuchtChart = new LineChart(new NumberAxis(1,12,1), new NumberAxis(0,50,5));
    @FXML
    private LineChart druckChart = new LineChart(new NumberAxis(1,12,1), new NumberAxis(0,50,5));
    @FXML
    private ComboBox<String> wetterSensor = new ComboBox<String>();
    private XYChart.Series chartDaten = new XYChart.Series();

    public MainController()
    {
		this.context = IDbContext.DB_CONTEXT;
        WetterStation.getInstance().registerObserver(this);
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources){

        ObservableList<String> list =  FXCollections.observableArrayList();
        list.add("test");
        wetterSensor.setItems(list);
        wetterSensor.getItems().add("Berlin Ost");
        super.initialize(location,resources);
    }

    @FXML
    public void doSmth()
    {
        WetterSensorChoice.setText("NO");
        wetterSensor.getItems().add("NOOOOOO");
    }

    @Override
    public synchronized void update(WetterDatenModel daten)
    {
        if(selectedSensor == (daten).getGemessenVon())
		{
            WetterSensorChoice.setText(Double.toString(daten.getTempInC()));
        }

    }

    @FXML
    public void sensorSelected()
    {

    }

    public void aendereWetterdaten()
    {

    }

    public void getAlteWetterdatenModel()
    {

    }

    public void ladeWetterdatenDesLetztenJahres()
    {

    }
    public void getSensoren()
    {

    }

    public void neuerWettersensor()
    {

    }

    public void deleteSensor(){

    }

    public void updateSensor(){

    }

    public void getSettings()
    {

    }
}
