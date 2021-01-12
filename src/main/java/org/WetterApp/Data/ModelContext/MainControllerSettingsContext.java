package org.WetterApp.Data.ModelContext;

import org.WetterApp.Data.DbContext;
import org.WetterApp.Data.Interfaces.IMainControllerSettingsContext;
import org.WetterApp.Models.WetterSensorModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainControllerSettingsContext extends DbContext implements IMainControllerSettingsContext {

    public MainControllerSettingsContext()throws SQLException {
        super();
    }

    public WetterSensorModel getSelectedSensorId(){
        WetterSensorModel model = new WetterSensorModel();
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT \"sensoren.id\",\"sensoren.name\",\"sensoren.gpsLat\",\"sensoren.gpsLong\" FROM \"settings\",\"sensoren\" " +
                            "WHERE \"settings.id\" = 1;"
            );

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
        }
        return model;
    }

    public boolean aendere(int id){
        boolean result = false;
        try{
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE settings " +
                            "SET selectedSensor = ? " +
                            "WHERE id = 1;"
            );
            stmt.setInt(1,id);
            result = stmt.executeUpdate() == 1;
            stmt.close();
        }catch (SQLException ex){
            try{
                PreparedStatement insertStmt = con.prepareStatement(
                  "INSERT INTO settings (selectedSensor) " +
                        "VALUES (?);"
                );
                insertStmt.setInt(1,id);
                result = insertStmt.executeUpdate() == 1;
                insertStmt.close();
            }catch (SQLException sqlException){
                System.out.println(ex.getMessage());
            }
        }
        return result;
    }
}
