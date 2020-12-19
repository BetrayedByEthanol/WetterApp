package org.WetterApp.Models;

public class RandomWetterSensor extends WetterSensorModel
{
    public RandomWetterSensor() {
        super();
    }

    @Override
    public WetterDatenModel messeDaten()
    {
        return  new RandomWetterDatenModel();
    }
}
