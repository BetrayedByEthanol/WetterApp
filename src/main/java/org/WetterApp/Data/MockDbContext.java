package org.WetterApp.Data;

public class MockDbContext extends DbContext {

    @Override
    public WetterDatenContext getWetterdatenContext() {
        return new MockWetterDatenContext();
    }

    @Override
    public WetterSensorContext getWetterSensorContext() {
        return new MockWetterSensorContext();
    }
}
