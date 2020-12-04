package org.WetterApp.Data;

import org.WetterApp.Models.MockWetterSensor;

public class DbContext
{
    private String connection;
    private MockWetterDatenContext fakeWetterdaten;
    private MockWetterSensor fakeAdruinos;
    private WetterDatenContext Wetterdaten;
    private WetterSensorContext WetterSensor;

    public DbContext()
    {
        this.fakeWetterdaten = new MockWetterDatenContext();
        this.fakeAdruinos = new MockWetterSensor();
        Wetterdaten = new WetterDatenContext();
        WetterSensor = new WetterSensorContext();
    }

    public MockWetterDatenContext getFakeWetterdaten() {
        return fakeWetterdaten;
    }

    public MockWetterSensor getFakeAdruinos() {
        return fakeAdruinos;
    }

    public WetterDatenContext getWetterdaten() {
        return Wetterdaten;
    }

    public WetterSensorContext getWetterSensor() {
        return WetterSensor;
    }
}
