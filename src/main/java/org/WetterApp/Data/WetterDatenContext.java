package org.WetterApp.Data;

import org.WetterApp.Models.WetterDatenModel;

import java.util.ArrayList;

public class WetterDatenContext {

    public WetterDatenModel getWetterdaten(int sensorId,long messZeitpunktInUnixTime){
        return new WetterDatenModel();
    }

    public ArrayList<WetterDatenModel> getWetterdaten(int sensorId, long startZeitpunkt, long endZeitpunkt){
        return new ArrayList<WetterDatenModel>();
    }

    public WetterDatenModel getWetterdaten(int sensorId){
        return new WetterDatenModel();
    }


    public boolean speichereWetterdaten(WetterDatenModel model){
        return false;
    }

    public boolean aendereWetterdaten(WetterDatenModel model){
        return false;
    }
}
