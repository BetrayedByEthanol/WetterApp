package org.WetterApp.Data;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class ADbContext
{
    private String driver = "jdbc:sqlite:";
    private String connection = "";
    protected Connection con;


    public ADbContext()
    {
        File sqlite = new File(System.getProperty("user.dir") + "/sqlite.db");
        try{
            if(!sqlite.exists()) sqlite.createNewFile();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        connection = driver + sqlite.getAbsolutePath();
        try{
            con = DriverManager.getConnection(connection);
            con.setAutoCommit(false);
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void saveChanges(){
        try{
            con.commit();
        }catch (SQLException ex){
            System.out.println("Konnte nicht speichern" + ex.getMessage());
        }
    }

}
