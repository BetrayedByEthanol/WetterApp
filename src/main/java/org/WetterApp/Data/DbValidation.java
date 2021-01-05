package org.WetterApp.Data;

import java.sql.SQLException;
import java.sql.Statement;

public class DbValidation extends ADbContext {

    public DbValidation() {
        super();
    }

    public void validate(){
        valSettings();
        valSenoren();
        valMessungen();
        valWettterdaten();
        valInvalidWetterdaten();
        valAenderung();
        saveChanges();
    }

    private void valAenderung(){
        try {
            Statement stmt = con.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"aenderungen\" (" +
                    "\"id\"                         INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "\"unixTime\"                   INTEGER NOT NULL, " +
                    "\"wetterdaten_id\"             INTEGER NOT NULL, " +
                    "\"vorherige_wetterdaten_id\"   InTEGER NOT NULL, " +
                    "FOREIGN KEY(wetterdaten_id) REFERENCES wetterdaten(id) " +
                        "ON DELETE CASCADE " +
                        "ON UPDATE CASCADE, " +
                    "FOREIGN KEY(vorherige_Wetterdaten_id) REFERENCES wetterdaten(id) " +
                        "ON DELETE CASCADE " +
                        "ON UPDATE CASCADE " +
                    ")");
            stmt.close();
        }catch (SQLException ex){
            System.out.println("Aenderungen exception: " + ex.getMessage());
        }
    }

    private void valWettterdaten(){
        try {
            Statement stmt = con.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"wetterdaten\" (" +
                            "\"id\"                     INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "\"temperatur\"             DOUBLE NOT NULL," +
                            "\"windgeschwindigkeit\"    DOUBLE NOT NULL," +
                            "\"luftfeuchtigkeit\"       DOUBLE NOT NULL," +
                            "\"luftdruck\"              DOUBLE NOT NULL," +
                            "\"co2\"                    DOUBLE NOT NULL," +
                            "\"sensor_id\"              INTEGER NOT NULL," +
                            "\"messung_id\"             INTEGER NOT NULL," +
                            "FOREIGN KEY(sensor_id) REFERENCES sensoren(id) " +
                                "ON DELETE CASCADE " +
                                "ON UPDATE CASCADE," +
                            "FOREIGN KEY(messung_id) REFERENCES messungen(id) " +
                                "ON DELETE CASCADE " +
                                "ON UPDATE CASCADE " +
                            ")");
            stmt.close();
        }catch (SQLException ex){
            System.out.println("Wetterdaten exception: " + ex.getMessage());
        }
    }

    private void valSenoren(){
        try {
            Statement stmt = con.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"sensoren\" (" +
                            "\"id\"         INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "\"name\"       NVARCHAR(64) NOT NULL," +
                            "\"gpsLat\"     DOUBLE NOT NULL," +
                            "\"gpsLong\"    DOUBLE NOT NULL" +
                            ")");
            stmt.close();
        }catch (SQLException ex){
            System.out.println("Sensoren exception: " + ex.getMessage());
        }
    }

    private void valSettings(){
        try {
            Statement stmt = con.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"settings\" (" +
                            "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "\"selectedSenor\" INTEGER NOT NULL" +
                            ")");
            stmt.close();
        }catch (SQLException ex){
            System.out.println("Setting exception: " + ex.getMessage());
        }
    }

    private void valInvalidWetterdaten(){
        try {
            Statement stmt = con.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"invalid_wetterdaten\" (" +
                            "\"id\"                 INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "\"wetterdaten_id\"     INTEGER NOT NULL," +
                            "FOREIGN KEY(wetterdaten_id) REFERENCES wetterdaten(id) " +
                                "ON DELETE CASCADE " +
                                "ON UPDATE CASCADE " +
                            ")");
            stmt.close();
        }catch (SQLException ex){
            System.out.println("Invalid wetterdaten excpetion: " + ex.getMessage());
        }
    }

    private void valMessungen(){
        try {
            Statement stmt = con.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"messungen\" (" +
                            "\"id\"         INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "\"unixTime\"   INTEGER NOT NULL" +
                            ")");
            stmt.close();
        }catch (SQLException ex){
            System.out.println("messungen table exception: " + ex.getMessage());
        }
    }
}
