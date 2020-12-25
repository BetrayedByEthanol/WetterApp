package org.WetterApp.Models;


public class WetterSensorModel
{
    private int id;
    private double gpsXCoord;
    private double gpsYCoord;
    private String name = "default Berlin";

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public double getGpsXCoord() {
        return gpsXCoord;
    }

    public void setGpsXCoord(double gpsXCoord) {
        this.gpsXCoord = gpsXCoord;
    }

    public double getGpsYCoord() {
        return gpsYCoord;
    }

    public void setGpsYCoord(double gpsYCoord) {
        this.gpsYCoord = gpsYCoord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
