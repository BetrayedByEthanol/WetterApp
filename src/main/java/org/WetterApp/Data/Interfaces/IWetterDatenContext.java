package org.WetterApp.Data.Interfaces;

import org.WetterApp.Models.WetterDatenModel;

import java.util.ArrayList;

public interface IWetterDatenContext extends IDbContext{

    public WetterDatenModel getWetterdaten(int sensorId, long messZeitpunktInUnixTime);

    public ArrayList<WetterDatenModel> getWetterdaten(int sensorId, long startZeitpunkt, long endZeitpunkt);

    WetterDatenModel getWetterdaten(int sensorId);

    WetterDatenModel speichereWetterdaten(WetterDatenModel model);

    boolean aendereWetterdaten(WetterDatenModel model);
}
