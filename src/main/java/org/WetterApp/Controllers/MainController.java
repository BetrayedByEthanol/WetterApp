package org.WetterApp.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController
{
    @FXML
    private TextField WetterSensorChoice = new TextField();


    @FXML
    public void doSmth()
    {
        WetterSensorChoice.setText("NO");

    }
}
