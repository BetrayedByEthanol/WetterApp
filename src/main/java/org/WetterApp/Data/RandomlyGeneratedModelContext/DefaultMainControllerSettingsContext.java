package org.WetterApp.Data.RandomlyGeneratedModelContext;

import org.WetterApp.Data.Interfaces.IMainControllerSettingsContext;
import org.WetterApp.Models.WetterSensorModel;

public class DefaultMainControllerSettingsContext extends SimulatedDbContext implements IMainControllerSettingsContext {

    @Override
    public WetterSensorModel getSelectedSensorId() {
        WetterSensorModel model = new WetterSensorModel();
        model.setId(1);
        return model;
    }

    @Override
    public boolean aendere(int id) {
        return false;
    }
}
