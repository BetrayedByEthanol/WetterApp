package org.WetterApp.Data.ModelContext;

import org.WetterApp.Data.DbContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Models.WetterSensorModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WetterSensorContext extends DbContext implements IWetterSensorContext {
    public WetterSensorContext() throws SQLException { super(); }

    public ArrayList<WetterSensorModel> ladeWetterSensoren(){
        ArrayList<WetterSensorModel> sensorModels =new ArrayList<WetterSensorModel>();
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT * FROM \"sensoren\";"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                WetterSensorModel model =  new WetterSensorModel();
                model.setId(rs.getInt(1));
                model.setName(rs.getString(2));
                model.setGpsYCoord(rs.getDouble(3));
                model.setGpsXCoord(rs.getDouble(4));
                sensorModels.add(model);
            }
            stmt.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return sensorModels;
    }

    public WetterSensorModel ladeWetterSensor(int id){
        WetterSensorModel model =  new WetterSensorModel();
        try{
            PreparedStatement stmt = con.prepareStatement(
              "SELECT * FROM \"sensoren\" " +
                      "WHERE \"id\" = ?;"
            );
            stmt.setInt(1,id);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                model.setId(rs.getInt(1));
                model.setName(rs.getString(2));
                model.setGpsYCoord(rs.getDouble(3));
                model.setGpsXCoord(rs.getDouble(4));
                stmt.close();
                return model;
            }else{
                stmt.close();
                return null;
            }
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            return null;
        }catch (NullPointerException ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public WetterSensorModel neuerWettersensor(WetterSensorModel sensor){
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO \"sensoren\" (\"name\",\"gpsLat\",\"gpsLong\") " +
                            "VALUES (?,?,?);"
            );
            stmt.setString(1,sensor.getName());
            stmt.setDouble(2,sensor.getGpsYCoord());
            stmt.setDouble(3,sensor.getGpsXCoord());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                sensor.setId(rs.getInt(1));
                stmt.close();
                return sensor;
            } else {
                stmt.close();
                return null;
            }
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            return  null;
        }
    }

    public boolean deleteWettersensor(int id){
        boolean result = false;
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "DELETE FROM \"sensoren\" " +
                            "WHERER \"id\" = ?;"
            );
            stmt.setInt(1,id);
            result = stmt.executeUpdate() > 0;
            stmt.close();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
