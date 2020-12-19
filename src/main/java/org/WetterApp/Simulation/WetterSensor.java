package org.WetterApp.Simulation;

import com.google.gson.Gson;
import org.WetterApp.Mapping.WetterDatenMapper;
import org.WetterApp.Models.DataTransitionalObjects.WetterDatenDTO;
import org.WetterApp.Models.WetterDatenModel;

import java.time.OffsetDateTime;

public class WetterSensor {

    public String send(){
        WetterDaten daten = new WetterDaten();
        daten.setCo2(2);
        daten.setGemessenVon(1);
        daten.setTempInC(23);
        daten.setLuftDruck(23);
        daten.setLuftFeuchtigkeit(22);
        daten.setWindGeschw(22);
        daten.setZeitDesMessens(OffsetDateTime.now().toString());
        Gson gson = new Gson();
        String result = gson.toJson(daten);

        WetterDatenDTO dto =  gson.fromJson(result, WetterDatenDTO.class);

        WetterDatenModel model = WetterDatenMapper.MAPPER.map(dto);

        System.out.println(model.getZeitDesMessens().toString());
        return gson.toJson(result);
    }
}
