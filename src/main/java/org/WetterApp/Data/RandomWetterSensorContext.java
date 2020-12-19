package org.WetterApp.Data;

import org.WetterApp.Models.WetterSensorModel;

import java.util.ArrayList;

public class RandomWetterSensorContext extends WetterSensorContext{
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
}
