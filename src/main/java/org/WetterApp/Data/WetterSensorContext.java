package org.WetterApp.Data;

import org.WetterApp.Models.WetterSensorModel;

import java.util.ArrayList;

public class WetterSensorContext {
    public ArrayList<WetterSensorModel> ladeWetterSensoren(){
        return new ArrayList<WetterSensorModel>();
    }

    public WetterSensorModel ladeWetterSensor(int id){
        return new WetterSensorModel();
    }
    //returns PK
    public WetterSensorModel neuerWettersensor(WetterSensorModel sensor){
        return sensor;
    }

    public boolean deleteWettersensor(int id){
        return false;
    }
}
