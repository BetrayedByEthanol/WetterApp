package org.WetterApp.Models;

public class MockWetterSensor extends WetterSensorModel
{
    public MockWetterSensor() {
        super();
    }

    @Override
    public WetterDatenModel messeDaten()
    {
        return  new MockWetterDatenModel();
    }
}
