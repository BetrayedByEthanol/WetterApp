package org.WetterApp.Data.ModelContext;

import org.WetterApp.Data.ADbContext;
import org.WetterApp.Data.Interfaces.IDbModelContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Models.InvalidWetterDatenModel;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Models.WetterDatenModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;

public class WetterDatenContext extends ADbContext implements IWetterDatenContext {

    public WetterDatenContext() {
        super();
    }

    public WetterDatenModel getWetterdaten(int sensorId, long messZeitpunktInUnixTime){
        WetterDatenModel model = null;
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT *,\"messungen\".\"unixTime\" as \"epochS\",MAX(\"aenderungen\".\"unixTime\") FROM \"wetterdaten\",\"messungen\" " +
                            "LEFT JOIN \"aenderungen\" ON \"wetterdaten\".\"id\" = \"aenderungen\".\"wetterdaten_id\" " +
                            "WHERE \"messung_id\" = (SELECT \"id\" FROM \"messungen\" WHERE \"unixTime\" = " + messZeitpunktInUnixTime + ") " +
                                "AND \"sensor_id\" = " + sensorId + ";"
            );
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) model = createModel(rs);
            stmt.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    public ArrayList<WetterDatenModel> getWetterdaten(int sensorId, long startZeitpunkt, long endZeitpunkt){
        ArrayList<WetterDatenModel> models = new ArrayList<WetterDatenModel>();
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT *,\"messungen\".\"unixTime\" as \"epochS\", \"aenderungen\".\"unixTime\" as \"modTime\" FROM \"wetterdaten\",\"messungen\" " +
                            "LEFT JOIN \"aenderungen\" ON \"wetterdaten\".\"id\" = \"aenderungen\".\"wetterdaten_id\" " +
                            "WHERE \"wetterdaten\".\"id\" != (SELECT \"vorherige_wetterdaten_id\" FROM \"aenderungen\") " +
                                "AND \"sensor_id\" = " + sensorId + " " +
                                "AND '\"epochS\" BETWEEN " + startZeitpunkt + " AND " + endZeitpunkt + " " +
                                "AND \"wetterdaten\".\"id\" != (SELECT wetterdaten_id FROM invalid_wetterdaten) " +
                            "GROUP BY \"wetterdaten\".\"id\";"
            );
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                WetterDatenModel model = createModel(rs);
                models.add(model);
            }
            stmt.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return models;
    }

    public WetterDatenModel getWetterdaten(int sensorId){
        WetterDatenModel model = null;
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT *,\"messungen\".\"unixTime\" as \"epochS\",MAX(\"aenderungen\".\"unixTime\") FROM \"wetterdaten\",\"messungen\" " +
                            "LEFT JOIN \"aenderungen\" ON \"wetterdaten\".\"id\" = \"aenderungen\".\"wetterdaten_id\" " +
                            "WHERE \"messung_id\" = (SELECT \"id\" FROM \"messungen\" WHERE \"unixTime\" = (SELECT MAX(\"unixTime\") FROM \"messungen\")) " +
                                "AND \"sensor_id\" = " + sensorId + ";"
            );
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) model = createModel(rs);
            stmt.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    public WetterDatenModel createModel(ResultSet rs){
        WetterDatenModel model = null;
        try{
            model = new WetterDatenModel();
            model.setId(rs.getInt(1));
            model.setTempInC(rs.getDouble("temperatur"));
            model.setWindGeschw(rs.getDouble("windgeschwindigkeit"));
            model.setLuftFeuchtigkeit(rs.getDouble("luftfeuchtigkeit"));
            model.setLuftDruck(rs.getDouble("luftdruck"));
            model.setCo2(rs.getDouble("co2"));
            model.setGemessenVon(IDbModelContext.MODEL_CONTEXT.getWetterSensorContext().ladeWetterSensor(rs.getInt("sensor_id")));

            OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt("epochS")), ZoneId.systemDefault());

            model.setZeitDesMessens(dateTime);
            if(rs.getInt(10) == 0)
                model.setZeitDerLetztenAederung(dateTime);
            else
                model.setZeitDerLetztenAederung( OffsetDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(10)), ZoneId.systemDefault()));

            model = WetterDatenValidation.validate(model);
        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    public WetterDatenModel speichereWetterdaten(WetterDatenModel model){
        try{
            PreparedStatement messungsStmt = con.prepareStatement(
                    "SELECT * FROM \"messungen\" " +
                            "WHERE unixTime = " + model.getZeitDesMessens().toEpochSecond()
            );

            ResultSet messungsRs = messungsStmt.executeQuery();
            int messungsId;

            if(messungsRs.next()){
                messungsId = messungsRs.getInt(1);
                messungsStmt.close();
            }else {
                messungsStmt.close();
                PreparedStatement neueMessungStmt = con.prepareStatement(
                        "INSERT INTO \"messungen\" (unixTime) " +
                                "VALUES (" + model.getZeitDesMessens().toEpochSecond() + ");"
                );
                neueMessungStmt.executeUpdate();
                ResultSet neueMessungRs = neueMessungStmt.getGeneratedKeys();
                if(neueMessungRs.next()){
                    messungsId = neueMessungRs.getInt(1);
                    neueMessungStmt.close();
                }else{
                    neueMessungStmt.close();
                    return null;
                }
            }

            PreparedStatement stmt = con.prepareStatement(
              "INSERT INTO \"wetterdaten\" (\"temperatur\",\"windgeschwindigkeit\",\"luftfeuchtigkeit\",luftdruck\",\"co2\",\"sensor_id\",messung_id) " +
                      "VALUES (" + model.getTempInC() + "," + model.getWindGeschw() + "," + model.getLuftFeuchtigkeit() + "," + model.getLuftDruck() + "," + model.getCo2() + "," + model.getGemessenVon().getId() + "," + messungsId + ");"
            );

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                model.setId(rs.getInt(1));
                stmt.close();
                if(model instanceof InvalidWetterDatenModel){
                    PreparedStatement invalidWetterdatenStmt = con.prepareStatement(
                            "INSERT INTO \"invalid_wetterdaten\" (\"wetterdaten_id\")" +
                                    "VALUES (" + model.getId() + ");"
                    );
                    invalidWetterdatenStmt.executeUpdate();
                    invalidWetterdatenStmt.close();
                }

                return model;
            }else{
                stmt.close();
                return null;
            }

        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public boolean aendereWetterdaten(WetterDatenModel model){
        int alteID = model.getId();
        model = speichereWetterdaten(model);
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO \"aenderungen\" (\"unixTime\",\"wetterdaten_id\",\"vorherige_wetterdaten_id\") " +
                            "VALUES (" + OffsetDateTime.now().toEpochSecond() + "," + model.getId() + "," + alteID + ");"
            );
            stmt.executeUpdate();
            stmt.close();
            return true;
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public WetterDatenModel revertChanges(WetterDatenModel model) {
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT * FROM \"aenderungen\" " +
                            "WHERE \"wetterdaten_id\" = " + model.getId() + ";"
            );
            ResultSet rs = stmt.executeQuery();
            int id = 0;
            if(rs.next()) id = rs.getInt("vorherige_wetterdaten_id");
            stmt.close();
            PreparedStatement deleteStmt = con.prepareStatement(
                    "DELETE FROM \"wetterdaten\" " +
                            "WHERE \"id\" = " + model.getId() + ";"
            );
            deleteStmt.executeUpdate();
            deleteStmt.close();

            PreparedStatement getModel = con.prepareStatement(
                    "SELECT *,\"messungen\".\"unixTime\" as \"epochS\", \"aenderungen\".\"unixTime\" as \"modTime\" FROM \"wetterdaten\",\"messungen\" " +
                            "LEFT JOIN \"aenderungen\" ON \"wetterdaten\".\"id\" = \"aenderungen\".\"wetterdaten_id\" " +
                            "WHERE \"wetterdaten\".\"id\" = " + id + ";"
            );
            ResultSet getRs = getModel.executeQuery();
            if(getRs.next()) model = createModel(rs);
            getModel.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    public WetterDatenModel findInvalid(int sensorId){
        WetterDatenModel model =null;
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT *,\"messungen\".\"unixTime\" as \"epochS\", \"aenderungen\".\"unixTime\" as \"modTime\" FROM \"wetterdaten\",\"messungen\" " +
                            "LEFT JOIN \"aenderungen\" ON \"wetterdaten\".\"id\" = \"aenderungen\".\"wetterdaten_id\" " +
                            "WHERE \"wetterdaten\".\"id\" = (SELECT \"wetterdaten_id\" FROM \"invalid_wetterdaten\") " +
                            "AND \"sensor_id\" = " + sensorId + ";"
            );
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) model = createModel(rs);
            stmt.close();

        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

}
