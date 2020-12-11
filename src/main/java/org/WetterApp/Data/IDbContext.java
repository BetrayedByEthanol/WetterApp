package org.WetterApp.Data;

public interface IDbContext
{
    IDbContext DB_CONTEXT = new DbContext();
    WetterDatenContext getWetterdatenContext();

    WetterSensorContext getWetterSensorContext();

    MainControllerSettingContext getMainControllerSettinContext();
}
