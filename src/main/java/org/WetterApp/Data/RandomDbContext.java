package org.WetterApp.Data;

public class RandomDbContext extends DbContext {

    @Override
    public WetterDatenContext getWetterdatenContext() {
        return new RandomWetterDatenContext();
    }

    @Override
    public WetterSensorContext getWetterSensorContext() {
        return new RandomWetterSensorContext();
    }
}
