package org.WetterApp.Data.Interfaces;

import org.WetterApp.Models.WetterSensorModel;

import java.util.ArrayList;

public interface IWetterSensorContext extends IDbContext{
    ArrayList<WetterSensorModel> ladeWetterSensoren();

    WetterSensorModel ladeWetterSensor(int id);

    WetterSensorModel neuerWettersensor(WetterSensorModel sensor);

    boolean deleteWettersensor(int id);
}
