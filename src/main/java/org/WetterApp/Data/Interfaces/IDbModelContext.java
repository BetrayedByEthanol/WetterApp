package org.WetterApp.Data.Interfaces;

import org.WetterApp.Data.RandomlyGeneratedModelContext.RandomDbModelContext;

public interface IDbModelContext
{
    IDbModelContext MODEL_CONTEXT = new RandomDbModelContext();
    IWetterDatenContext getWetterdatenContext();

    IWetterSensorContext getWetterSensorContext();

    IMainControllerSettingsContext getMainControllerSettingsContext();
}
