package org.WetterApp.Data;

public interface IDbContext
{
    IDbContext DB_CONTEXT = new RandomDbContext();
    WetterDatenContext getWetterdatenContext();

    WetterSensorContext getWetterSensorContext();

    MainControllerSettingContext getMainControllerSettinContext();
}
