package org.WetterApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.WetterApp.Data.DbValidation;
import org.WetterApp.Data.Migration.CSVMigration;
import org.WetterApp.Simulation.SimulationInitilizer;
import org.WetterApp.Simulation.WetterSensor;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {

        try(DbValidation dbValidation = new DbValidation()){
            dbValidation.validate();
        }catch (Exception ex){
            System.out.println("Can't connect to DB");
        }

        SimulationInitilizer.init();

        launch(args);
    }

    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        stage.setTitle("Hello Simon");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop()
    {
        for (WetterSensor model : WetterSensor.activeSensors)
        {
           model.stop();
        }
    }

}
