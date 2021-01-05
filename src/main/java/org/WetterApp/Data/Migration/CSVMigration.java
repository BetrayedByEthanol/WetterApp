package org.WetterApp.Data.Migration;


import org.WetterApp.Data.ModelContext.WetterDatenContext;
import org.WetterApp.Data.ModelContext.WetterSensorContext;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.print.DocFlavor;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVMigration {

    public void up(){
        try {
            CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(new File(System.getProperty("user.dir") + "/10384.csv")));
            List<CSVRecord> records = parser.getRecords();

            WetterSensorModel superWetterSensor = new WetterSensorModel();
            superWetterSensor.setName("Supa Sensor");
            WetterSensorContext sensorContext = new WetterSensorContext();
            superWetterSensor = sensorContext.neuerWettersensor(superWetterSensor);
            sensorContext.saveChanges();

            CSVRecord testRecord = records.get(0);
            //0 Date
            //1 Time
            //2 Temp
            //7 Rain
            //8 Wind
            //10 Druck
            WetterDatenContext context = new WetterDatenContext();

            for(CSVRecord record : records) {
                WetterDatenModel model = new WetterDatenModel();
                try{
                    model.setTempInC(Double.parseDouble(record.get(2)));
                    model.setWindGeschw(Double.parseDouble(record.get(8)));
                    model.setLuftDruck(Double.parseDouble(record.get(10)));
                    model.setLuftFeuchtigkeit(calcFeucht(model, Double.parseDouble(record.get(7))));
                }catch (NumberFormatException ex){
                    System.out.println(ex.getMessage());
                }
                model.setCo2(2);

                model.setGemessenVon(superWetterSensor);


                //AHHHH
                OffsetDateTime dateTime = OffsetDateTime.now();
                String date = record.get(0);
                int hour =  Integer.parseInt(record.get(1));
                int min = 0;

                for(int i = 0; i < 24; i++){
                    OffsetDateTime.of(Integer.parseInt(date.substring(0,3))+2,Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(9,11)),hour,min,0,0, OffsetDateTime.now().getOffset());

                    model.setZeitDesMessens(dateTime);
                    model.setZeitDerLetztenAederung(dateTime);

                    context.speichereWetterdaten(model);
                    min += 15;
                    if(min == 60){
                        min = 0;
                        hour++;
                    }
                }

            }
            context.saveChanges();
        }catch (FileNotFoundException ex){
            System.out.println("file not found");
        }catch (IOException ex){
            System.out.println("cant read file");
        }
    }

    public void down(){

    }

    private double calcFeucht(WetterDatenModel model, double rain){
        if(rain > 0) return 92;
        else {
            double tempMod  = 0;
            if(model.getTempInC() > 0 ) tempMod = (model.getTempInC()) / 3;
            double druckMod = 40  * (model.getLuftDruck() / 1200);
            double feucht = 50 + tempMod + druckMod;
            if(feucht > 89) feucht = 89;
            return feucht;
        }
    }

}
