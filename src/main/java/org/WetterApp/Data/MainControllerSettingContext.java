package org.WetterApp.Data;

import org.WetterApp.Models.WetterSensorModel;

public class MainControllerSettingContext {

    public WetterSensorModel getSelectedSensorId(){
        return new WetterSensorModel();
    }

    public boolean aendere(int id){
        return false;
    }
}
