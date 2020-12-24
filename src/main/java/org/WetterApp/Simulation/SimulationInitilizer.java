package org.WetterApp.Simulation;

import org.WetterApp.WetterStation;

public class SimulationInitilizer {
    public static void init()
    {
        //Ueberpruefe, ob die Datenbank exsistiert.

        //Lade bzw. registiere Sensoren
        WetterStation station = WetterStation.getInstance();

        RandomWetterSensor sensor1 = new RandomWetterSensor();
        sensor1.registerObserver(station);
        sensor1.setId(1);
        sensor1.start();
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
    }

}
