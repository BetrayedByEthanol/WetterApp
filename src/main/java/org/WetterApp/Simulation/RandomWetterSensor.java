package org.WetterApp.Simulation;

import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;

public class RandomWetterSensor extends WetterSensor
{
    public RandomWetterSensor() {
        super();
    }

    @Override
    public WetterDaten messeDaten()
    {
        return  new RandomWetterDaten();
    }
}
