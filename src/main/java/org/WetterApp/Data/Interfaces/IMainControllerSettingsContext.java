package org.WetterApp.Data.Interfaces;

import org.WetterApp.Models.WetterSensorModel;

public interface IMainControllerSettingsContext extends IDbContext {

    WetterSensorModel getSelectedSensorId();

    boolean aendere(int id);
}
