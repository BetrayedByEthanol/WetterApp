package org.WetterApp.Data.Interfaces;

import org.WetterApp.Data.DbModelContext;
import org.WetterApp.Data.RandomlyGeneratedModelContext.RandomDbModelContext;

import java.sql.SQLException;

public interface IDbModelContext
{
    IDbModelContext MODEL_CONTEXT = new DbModelContext();
    IWetterDatenContext getWetterdatenContext() throws SQLException;

    IWetterSensorContext getWetterSensorContext() throws SQLException;

    IMainControllerSettingsContext getMainControllerSettingsContext() throws SQLException;
}
