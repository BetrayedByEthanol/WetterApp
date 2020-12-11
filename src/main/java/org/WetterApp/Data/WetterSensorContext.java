package org.WetterApp.Data;

import org.WetterApp.Models.WetterSensorModel;

import java.util.ArrayList;

public class WetterSensorContext {
    public ArrayList<WetterSensorModel> ladeWetterSensoren(){
        return new ArrayList<WetterSensorModel>();
    }

    //returns PK
    public int neuerWettersensor(WetterSensorModel sensor){
        return 5;
    }

    public boolean deleteWettersensor(int id){
        return false;
    }
}
