package org.WetterApp.Simulation;

import org.WetterApp.Controllers.WetterStationController;

public class SimulationInitilizer {
    public static void init()
    {
        //Lade bzw. registiere Sensoren
        WetterStationController station = WetterStationController.getInstance();

        RandomWetterSensor sensor2 = new RandomWetterSensor();
        sensor2.registerObserver(station);
        sensor2.setId(2);
        sensor2.start();
        RandomWetterSensor sensor3 = new RandomWetterSensor();
        sensor3.registerObserver(station);
        sensor3.setId(3);
        sensor3.start();
        RandomWetterSensor sensor4 = new RandomWetterSensor();
        sensor4.setId(4);
        sensor4.start();
        sensor4.registerObserver(station);
        RandomWetterSensor sensor5 = new RandomWetterSensor();
        sensor5.registerObserver(station);
        sensor5.setId(5);
        sensor5.start();
    }

}
