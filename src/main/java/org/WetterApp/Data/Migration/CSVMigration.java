package org.WetterApp.Data.Migration;


import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Data.ModelContext.WetterDatenContext;
import org.WetterApp.Data.ModelContext.WetterSensorContext;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.print.DocFlavor;
import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

public class CSVMigration {

    public void up(){
        try(WetterDatenContext context = new WetterDatenContext()){
            CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(new File(System.getProperty("user.dir") + "/10384.csv")));
            List<CSVRecord> records = parser.getRecords();

            WetterSensorModel superWetterSensor = new WetterSensorModel();
            superWetterSensor.setName("Supa Sensor");
            try(IWetterSensorContext sensorContext = new WetterSensorContext()){
                superWetterSensor = sensorContext.neuerWettersensor(superWetterSensor);
                sensorContext.saveChanges();
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

            CSVRecord testRecord = records.get(0);
            //0 Date
            //1 Time
            //2 Temp
            //7 Rain
            //8 Wind
            //10 Druck

            ZoneOffset offset = OffsetDateTime.now().getOffset();
            WetterDatenContext.PreparedInsertingStatements statements = context.getPreparedInsertingStatements();
            for(CSVRecord record : records) {
                WetterDatenModel model = new WetterDatenModel();
                try{
                    String test = record.get(2);
                    if(test.isEmpty()){
                        test = "0";
                    }
                    model.setTempInC(Double.parseDouble(test));
                    test = record.get(8);
                    if(test.isEmpty()){
                        test = "0";
                    }
                    model.setWindGeschw(Double.parseDouble(test));
                    test = record.get(10);
                    if(test.isEmpty()){
                        test = "0";
                    }
                    model.setLuftDruck(Double.parseDouble(test));

                    String rain = record.get(7);
                    if(rain.isEmpty()) rain = "0";
                    model.setLuftFeuchtigkeit(calcFeucht(model, Double.parseDouble(rain)));
                }catch (NumberFormatException ex){
                    System.out.println(ex.getMessage());
                }
                model.setCo2(2);

                model.setGemessenVon(superWetterSensor);


                String date = record.get(0);
                int year = Integer.parseInt(date.substring(0,4))+4;
                if(year > 2018) {

                    int month = Integer.parseInt(date.substring(5,7));
                    int day = Integer.parseInt(date.substring(8,10));
                    int hour =  Integer.parseInt(record.get(1));
                    int min = 0;

                    for(int i = 0; i < 4; i++){
                        OffsetDateTime dateTime = OffsetDateTime.of(year,month,day,hour,min,0,0, OffsetDateTime.now().getOffset());

                        model.setZeitDesMessens(dateTime);
                        model.setZeitDerLetztenAederung(dateTime);

                        model = WetterDatenValidation.validate(model);

                        context.speichereWetterdaten(model,statements);
                        min += 15;
                        if(min == 60){
                            min = 0;
                            hour++;
                        }
                    }
                }
            }
            statements.endInserion();
            context.saveChanges();
        }catch (FileNotFoundException ex){
            System.out.println("file not found");
        }catch (IOException ex){
            System.out.println("cant read file");
        }catch (Exception ex){
            System.out.println("DB Contection issue");
        }
    }

    public void down(){

    }

    private double calcFeucht(WetterDatenModel model, double rain){
        if(rain > 0) {
            double feucht = 70 + rain / 10;
            if(feucht > 92) return 92; else return feucht;
        }
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
