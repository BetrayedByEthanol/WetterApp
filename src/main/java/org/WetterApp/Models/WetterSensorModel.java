package org.WetterApp.Models;

import javafx.util.Pair;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WetterSensorModel
{
    private int id;
    private double gpsXCoord;
    private double gpsYCoord;
    private String name = "";

    public int getId() {
        return id;
    }

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
