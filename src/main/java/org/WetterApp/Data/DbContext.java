package org.WetterApp.Data;


import org.WetterApp.Data.Interfaces.IDbContext;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DbContext implements IDbContext
{
    private String driver = "jdbc:sqlite:";
    private String connection = "";
    protected Connection con;

    private static boolean sqliteLock;

    public DbContext() throws SQLException
    {
        File sqlite = new File(System.getProperty("user.dir") + "/sqlite.db");
        try{
            if(!sqlite.exists()) sqlite.createNewFile();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        connection = driver + sqlite.getAbsolutePath();
        con = DriverManager.getConnection(connection);
        con.setAutoCommit(false);
        sqliteLock = true;
    }

    public void saveChanges(){
        try{
            con.commit();
        }catch (SQLException ex){
            System.out.println("Konnte nicht speichern" + ex.getMessage());
        }
    }

    @Override
    public void close() throws Exception{
        try{
            con.rollback();
            con.close();
            sqliteLock = false;
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static boolean isBusy() {return sqliteLock;}
}
