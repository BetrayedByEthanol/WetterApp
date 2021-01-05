package org.WetterApp.Data.RandomlyGeneratedModelContext;

import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Models.WetterSensorModel;

import java.util.ArrayList;

public class RandomWetterSensorContext extends SimulatedDbContext implements IWetterSensorContext {
    @Override
    public ArrayList<WetterSensorModel> ladeWetterSensoren() {
        ArrayList<WetterSensorModel> result = new ArrayList();
        for(int i = 0; i < 4; i++){
            WetterSensorModel model = new WetterSensorModel();
            model.setId(i+1);
            result.add(model);
        }
        return result;
    }

    @Override
    public WetterSensorModel ladeWetterSensor(int id) {
        return null;
    }

    @Override
    public WetterSensorModel neuerWettersensor(WetterSensorModel sensor) {
        return null;
    }

    @Override
    public boolean deleteWettersensor(int id) {
        return false;
    }
}
