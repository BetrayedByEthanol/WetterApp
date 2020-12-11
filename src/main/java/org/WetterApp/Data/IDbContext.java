package org.WetterApp.Data;

public interface IDbContext
{
    IDbContext DB_CONTEXT = new MockDbContext();
    WetterDatenContext getWetterdatenContext();

    WetterSensorContext getWetterSensorContext();

    MainControllerSettingContext getMainControllerSettinContext();
}
