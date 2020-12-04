package org.WetterApp.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.WetterStation;

import java.util.ArrayList;

public class MainController extends BaseController implements IObserver
{
    public MainController()
    {
        WetterStation.getInstance().registerObserver(this);
    }

    public void finalize()
    {
        WetterStation.getInstance().removeObserver(this);
    }

    @FXML
    private TextField WetterSensorChoice = new TextField();


    @FXML
    public void doSmth()
    {
        WetterSensorChoice.setText("NO");

    }

    @Override
    public synchronized void update(Object daten) {
        WetterSensorChoice.setText(Double.toString(((ArrayList<WetterDatenModel>)daten).get(0).getTempInC()));
    }
}
