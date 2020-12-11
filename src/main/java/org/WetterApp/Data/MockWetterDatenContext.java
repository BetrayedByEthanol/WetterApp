package org.WetterApp.Data;

import org.WetterApp.Models.MockWetterDatenModel;
import org.WetterApp.Models.WetterDatenModel;

import java.util.ArrayList;

public class MockWetterDatenContext extends WetterDatenContext{
    @Override
    public WetterDatenModel getWetterdaten(int sensorId, long messZeitpunktInUnixTime) {
        MockWetterDatenModel model = new MockWetterDatenModel();
        model.setGemessenVon(sensorId);
        return model;
    }

    @Override
    public ArrayList<WetterDatenModel> getWetterdaten(int sensorId, long startZeitpunkt, long endZeitpunkt) {
        ArrayList<WetterDatenModel> models = new ArrayList<>();
        for(int i = 0; i < 35040; i++)
        {
            MockWetterDatenModel model = new MockWetterDatenModel();
            model.setGemessenVon(sensorId);
            models.add(model);
        }
        return models;
    }

    @Override
    public WetterDatenModel getWetterdaten(int sensorId) {
        MockWetterDatenModel model = new MockWetterDatenModel();
        model.setGemessenVon(sensorId);
        return model;
    }
}
