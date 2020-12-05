package org.WetterApp.Models;

public class MockWetterSensor extends WetterSensorModel
{
    public MockWetterSensor() {
        super();
    }

    @Override
    public WetterDatenModel messeDaten()
    {
        MockWetterDatenModel daten = new MockWetterDatenModel();
        daten.setGemessenVon(this);
        return daten;
    }
}
