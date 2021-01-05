package org.WetterApp.Data.RandomlyGeneratedModelContext;

import org.WetterApp.Data.*;
import org.WetterApp.Data.Interfaces.IMainControllerSettingsContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;

public class RandomDbModelContext extends DbModelContext {

    @Override
    public IMainControllerSettingsContext getMainControllerSettingsContext() { return new DefaultMainControllerSettingsContext();}

    @Override
    public IWetterDatenContext getWetterdatenContext() {
        return new RandomWetterDatenContext();
    }

    @Override
    public IWetterSensorContext getWetterSensorContext() {
        return new RandomWetterSensorContext();
    }
}
