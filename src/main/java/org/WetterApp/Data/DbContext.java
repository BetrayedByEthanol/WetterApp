package org.WetterApp.Data;


public class DbContext implements IDbContext
{
    private String connection;

    public DbContext()
    {
    }

    public WetterDatenContext getWetterdatenContext() {
        return new WetterDatenContext();
    }

    public WetterSensorContext getWetterSensorContext() {
        return new WetterSensorContext();
    }


    public MainControllerSettingContext getMainControllerSettinContext() {
        return new MainControllerSettingContext();
    }
}
