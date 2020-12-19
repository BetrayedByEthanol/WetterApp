package org.WetterApp.Data;

import org.WetterApp.Models.RandomWetterDatenModel;
import org.WetterApp.Models.WetterDatenModel;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class RandomWetterDatenContext extends WetterDatenContext{
    @Override
    public WetterDatenModel getWetterdaten(int sensorId, long messZeitpunktInUnixTime) {
        RandomWetterDatenModel model = new RandomWetterDatenModel();
        model.setGemessenVon(sensorId);
        OffsetDateTime time = OffsetDateTime.now();
        time = time.minusSeconds(time.toEpochSecond() % 900);
        model.setZeitDesMessens(time);
        model.setZeitDerLetztenAederung(time);
        return model;
    }

    @Override
    public ArrayList<WetterDatenModel> getWetterdaten(int sensorId, long startZeitpunkt, long endZeitpunkt) {
        ArrayList<WetterDatenModel> models = new ArrayList<WetterDatenModel>();
        OffsetDateTime time = OffsetDateTime.now();
        time = time.minusSeconds(time.toEpochSecond() % 900);
        time = time.minusYears(1);
        while(time.toEpochSecond() < OffsetDateTime.now().toEpochSecond())
        {
            RandomWetterDatenModel model = new RandomWetterDatenModel();
            model.setGemessenVon(sensorId);
            model.setZeitDesMessens(time);
            model.setZeitDerLetztenAederung(time);
            time = time.plusMinutes(15);
            models.add(model);
        }
        Collections.reverse(models);
        return models;
    }

    @Override
    public WetterDatenModel getWetterdaten(int sensorId) {
        RandomWetterDatenModel model = new RandomWetterDatenModel();
        model.setGemessenVon(sensorId);
        OffsetDateTime time = OffsetDateTime.now();
        time = time.minusSeconds(time.toEpochSecond() % 900);
        model.setZeitDesMessens(time);
        model.setZeitDerLetztenAederung(time);
        return model;
    }
}
