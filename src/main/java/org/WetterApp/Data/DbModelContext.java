package org.WetterApp.Data;

import org.WetterApp.Data.Interfaces.IDbModelContext;
import org.WetterApp.Data.Interfaces.IMainControllerSettingsContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Data.ModelContext.MainControllerSettingsContext;
import org.WetterApp.Data.ModelContext.WetterDatenContext;
import org.WetterApp.Data.ModelContext.WetterSensorContext;

public class DbModelContext implements IDbModelContext {
    public IWetterDatenContext getWetterdatenContext() {
        return new WetterDatenContext();
    }

    public IWetterSensorContext getWetterSensorContext() {
        return new WetterSensorContext();
    }

    public IMainControllerSettingsContext getMainControllerSettingsContext() {
        return new MainControllerSettingsContext();
    }

}
