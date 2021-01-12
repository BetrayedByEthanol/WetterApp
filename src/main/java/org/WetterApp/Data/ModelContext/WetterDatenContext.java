package org.WetterApp.Data.ModelContext;

import org.WetterApp.Data.DbContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Models.InvalidWetterDatenModel;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;

public class WetterDatenContext extends DbContext implements IWetterDatenContext {

    public WetterDatenContext()throws SQLException {
        super();
    }

    public WetterDatenModel getWetterdaten(int sensorId, long messZeitpunktInUnixTime){
        WetterDatenModel model = null;
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT wetterdaten.id as id, temperatur, windgeschwindigkeit, luftfeuchtigkeit, luftdruck, co2,sensor_id, name, gpsLat, gpsLong, messungen.unixTime as epochS, aenderungen.unixTime as modTime " +
                            "FROM wetterdaten " +
                            "INNER JOIN messungen ON wetterdaten.messung_id = messungen.id " +
                            "INNER JOIN sensoren ON wetterdaten.sensor_id = sensoren.id " +
                            "LEFT JOIN aenderungen ON wetterdaten.id = aenderungen.wetterdaten_id " +
                            "WHERE messung_id = (SELECT id FROM messungen WHERE unixTime = ?) " +
                                "AND sensor_id = ?;"
            );
            stmt.setLong(1,messZeitpunktInUnixTime);
            stmt.setInt(2,sensorId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                model = createModel(rs);
                model.setGemessenVon(createSensorModel(rs));
            }
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
                    "SELECT wetterdaten.id as id, temperatur, windgeschwindigkeit, luftfeuchtigkeit, luftdruck, co2,sensor_id, name, gpsLat, gpsLong, messungen.unixTime as epochS, aenderungen.unixTime as modTime " +
                            "FROM wetterdaten " +
                            "INNER JOIN messungen ON wetterdaten.messung_id = messungen.id " +
                            "INNER JOIN sensoren ON wetterdaten.sensor_id = sensoren.id " +
                            "LEFT JOIN aenderungen ON wetterdaten.id = aenderungen.wetterdaten_id " +
                            "WHERE sensor_id = ? " +
                            "   AND messungen.unixTime BETWEEN ? AND ? " +
                            "   AND wetterdaten.id != (SELECT wetterdaten_id FROM invalid_wetterdaten UNION " +
                            "       SELECT vorherige_wetterdaten_id FROM aenderungen);"
            );
            stmt.setInt(1,sensorId);
            stmt.setLong(2,startZeitpunkt);
            stmt.setLong(3,endZeitpunkt);

            ResultSet rs = stmt.executeQuery();
            WetterSensorModel sensor = null;
            if(rs.next()){
                WetterDatenModel model = createModel(rs);
                sensor = createSensorModel(rs);
                model.setGemessenVon(sensor);
                models.add(model);
            }
            while(rs.next()){
                WetterDatenModel model = createModel(rs);
                model.setGemessenVon(sensor);
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
                    "SELECT wetterdaten.id as id, temperatur, windgeschwindigkeit, luftfeuchtigkeit, luftdruck, co2,sensor_id, name, gpsLat, gpsLong, messungen.unixTime as epochS, aenderungen.unixTime as modTime " +
                            "FROM wetterdaten " +
                            "INNER JOIN messungen ON wetterdaten.messung_id = messungen.id " +
                            "INNER JOIN sensoren ON wetterdaten.sensor_id = sensoren.id " +
                            "LEFT JOIN aenderungen ON wetterdaten.id = aenderungen.wetterdaten_id " +
                            "WHERE messung_id = (SELECT id FROM messungen WHERE unixTime = (SELECT MAX(unixTime) FROM messungen)) " +
                                "AND sensor_id = ?;"
            );
            stmt.setInt(1,sensorId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                model = createModel(rs);
                model.setGemessenVon(createSensorModel(rs));
            }
            stmt.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    private WetterDatenModel createModel(ResultSet rs){
        WetterDatenModel model = null;
        try{
            model = new WetterDatenModel();
            model.setId(rs.getInt(1));
            model.setTempInC(rs.getDouble("temperatur"));
            model.setWindGeschw(rs.getDouble("windgeschwindigkeit"));
            model.setLuftFeuchtigkeit(rs.getDouble("luftfeuchtigkeit"));
            model.setLuftDruck(rs.getDouble("luftdruck"));
            model.setCo2(rs.getDouble("co2"));

            OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt("epochS")), ZoneId.systemDefault());

            model.setZeitDesMessens(dateTime);
            if(rs.getInt("modTime") == 0)
                model.setZeitDerLetztenAederung(dateTime);
            else
                model.setZeitDerLetztenAederung( OffsetDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(10)), ZoneId.systemDefault()));

            model = WetterDatenValidation.validate(model);
        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    private WetterSensorModel createSensorModel(ResultSet rs){
        WetterSensorModel model = null;
        try {
            model = new WetterSensorModel();
            model.setName(rs.getString("name"));
            model.setGpsXCoord(rs.getDouble("gpsLat"));
            model.setGpsYCoord(rs.getDouble("gpsLong"));
            model.setId(rs.getInt("sensor_id"));
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

    public WetterDatenModel speichereWetterdaten(WetterDatenModel model){
        PreparedInsertingStatements statements = new PreparedInsertingStatements(con);
        model = speichereWetterdaten(model,statements);
        statements.endInserion();
        return model;
    }

    public WetterDatenModel speichereWetterdaten(WetterDatenModel model, PreparedInsertingStatements statements){
        try{
            statements.selectMessungsStmt.setLong(1,model.getZeitDesMessens().toEpochSecond());

            ResultSet messungsRs = statements.selectMessungsStmt.executeQuery();
            int messungsId;

            if(messungsRs.next()){
                messungsId = messungsRs.getInt(1);
            }else {
                statements.neueMessungStmt.setLong(1,model.getZeitDesMessens().toEpochSecond());
                statements.neueMessungStmt.executeUpdate();
                ResultSet neueMessungRs = statements.neueMessungStmt.getGeneratedKeys();
                if(neueMessungRs.next()){
                    messungsId = neueMessungRs.getInt(1);
                }else{
                    return null;
                }
            }
            statements.insertStmt.setDouble(1,model.getTempInC());
            statements.insertStmt.setDouble(2,model.getWindGeschw());
            statements.insertStmt.setDouble(3,model.getLuftFeuchtigkeit());
            statements.insertStmt.setDouble(4,model.getLuftDruck());
            statements.insertStmt.setDouble(5,model.getCo2());
            statements.insertStmt.setInt(6,model.getGemessenVon().getId());
            statements.insertStmt.setInt(7,messungsId);
            statements.insertStmt.executeUpdate();

            ResultSet rs = statements.insertStmt.getGeneratedKeys();
            if(rs.next()){
                model.setId(rs.getInt(1));
                if(model instanceof InvalidWetterDatenModel){
                    statements.invalidStmt.setInt(1,model.getId());
                    statements.invalidStmt.executeUpdate();
                }
                return model;
            }else{
                return null;
            }

        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public Iterable<WetterDatenModel> speichereWetterdaten(Iterable<WetterDatenModel> models){
        PreparedInsertingStatements statements = new PreparedInsertingStatements(con);
        models = speichereWetterdaten(models,statements);
        statements.endInserion();
        return models;
    }
    public Iterable<WetterDatenModel> speichereWetterdaten(Iterable<WetterDatenModel> models, PreparedInsertingStatements statements){
        for(WetterDatenModel model : models){
            model = speichereWetterdaten(model, statements);
        }
        return models;
    }

    public PreparedInsertingStatements getPreparedInsertingStatements(){
        return new PreparedInsertingStatements(con);
    }

    public class PreparedInsertingStatements{
        PreparedStatement selectMessungsStmt;
        PreparedStatement neueMessungStmt;
        PreparedStatement insertStmt;
        PreparedStatement invalidStmt;

        private PreparedInsertingStatements(Connection con){
            try{
                selectMessungsStmt = con.prepareStatement(
                        "SELECT * FROM messungen " +
                                "WHERE unixTime = ?;"
                );
                neueMessungStmt = con.prepareStatement(
                        "INSERT INTO messungen (unixTime) " +
                                "VALUES (?);"
                );
                insertStmt = con.prepareStatement(
                        "INSERT INTO wetterdaten (temperatur,windgeschwindigkeit,luftfeuchtigkeit,luftdruck,co2,sensor_id,messung_id) " +
                                "VALUES (?,?,?,?,?,?,?);"
                );
                invalidStmt = con.prepareStatement(
                        "INSERT INTO invalid_wetterdaten (wetterdaten_id)" +
                                "VALUES (?);"
                );
            }catch (SQLException ex){
                System.out.println(ex.getMessage());
            }
        }

        public void endInserion(){
            try{
                selectMessungsStmt.close();
                neueMessungStmt.close();
                insertStmt.close();
                invalidStmt.close();
            }catch (SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public boolean aendereWetterdaten(WetterDatenModel model){
        int alteID = model.getId();
        model = speichereWetterdaten(model);
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO aenderungen (unixTime,wetterdaten_id,vorherige_wetterdaten_id) " +
                            "VALUES (?,?,?);"
            );
            stmt.setLong(1, OffsetDateTime.now().toEpochSecond());
            stmt.setInt(2,model.getId());
            stmt.setInt(3,alteID);
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
                    "SELECT * FROM aenderungen " +
                            "WHERE wetterdaten_id = ?;"
            );
            stmt.setInt(1,model.getId());
            ResultSet rs = stmt.executeQuery();
            int id = 0;
            if(rs.next()) id = rs.getInt("vorherige_wetterdaten_id");
            stmt.close();
            PreparedStatement deleteStmt = con.prepareStatement(
                    "DELETE FROM wetterdaten " +
                            "WHERE id = ?;"
            );
            deleteStmt.setInt(1,model.getId());
            deleteStmt.executeUpdate();
            deleteStmt.close();

            PreparedStatement getModel = con.prepareStatement(
                    "SELECT *,messungen.unixTime as epochS, aenderungen.unixTime as modTime FROM wetterdaten,messungen " +
                            "LEFT JOIN aenderungen ON wetterdaten.id = aenderungen.wetterdaten_id " +
                            "WHERE wetterdaten.id = ?;"
            );
            getModel.setInt(1,id);
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
                    "SELECT *,messungen.unixTime as epochS, aenderungen.unixTime as modTime FROM wetterdaten,messungen " +
                            "LEFT JOIN aenderungen ON wetterdaten.id = aenderungen.wetterdaten_id " +
                            "WHERE wetterdaten.id = (SELECT wetterdaten_id FROM invalid_wetterdaten) " +
                            "AND sensor_id = ?;"
            );
            stmt.setInt(1,sensorId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) model = createModel(rs);
            stmt.close();

        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return model;
    }

}
