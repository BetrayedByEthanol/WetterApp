package org.WetterApp.Data;

import org.WetterApp.Data.Interfaces.IDbModelContext;
import org.WetterApp.Data.Interfaces.IMainControllerSettingsContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Data.ModelContext.MainControllerSettingsContext;
import org.WetterApp.Data.ModelContext.WetterDatenContext;
import org.WetterApp.Data.ModelContext.WetterSensorContext;

import java.sql.SQLException;

public class DbModelContext implements IDbModelContext {

    public IWetterDatenContext getWetterdatenContext() throws SQLException {
        return new WetterDatenContext();
    }

    public IWetterSensorContext getWetterSensorContext() throws SQLException {
        return new WetterSensorContext();
    }

    public IMainControllerSettingsContext getMainControllerSettingsContext() throws SQLException {
        return new MainControllerSettingsContext();
    }

}
