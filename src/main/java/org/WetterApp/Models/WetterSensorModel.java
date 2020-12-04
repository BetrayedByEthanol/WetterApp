package org.WetterApp.Models;

import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;

import java.time.OffsetDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class WetterSensorModel implements IObservable
{
    private int id;
    private double gpsXCoord;
    private double gpsYCoord;
    private String name;

    public WetterSensorModel()
    {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                WetterDatenModel daten = messeDaten();
                OffsetDateTime now = OffsetDateTime.now();
                daten.setZeitDerLetztenAederung(now);
                daten.setZeitDesMessens(now);
                notifyObservers(daten);
            }
        };
        timer.scheduleAtFixedRate(task,500, 900000);

    }

    public WetterDatenModel messeDaten()
    {
        return new WetterDatenModel();
    }

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

    @Override
    public void notifyObservers(WetterDatenModel daten) {
        for(IObserver obs : observer)
        {
            obs.update(id, daten);
        }
    }
}
