package org.WetterApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.WetterApp.Models.WetterSensorModel;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        //Erstelle die Wetterstation
        WetterStation.getInstance();

        launch(args);
    }

    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop()
    {
        for (WetterSensorModel model : WetterSensorModel.activeSensors)
        {
           model.stop();
        }
    }

}
