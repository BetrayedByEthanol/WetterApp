package org.WetterApp.Models;

import javafx.util.Pair;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WetterSensorModel implements IObservable
{
    private int id;
    private double gpsXCoord;
    private double gpsYCoord;
    private String name;
    private WetterDatenModel daten;
    private ArrayList<IObserver> observers = new ArrayList<>();


    public WetterSensorModel()
    {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                daten = messeDaten();
				daten.setGemessenVon(id);
                OffsetDateTime now = OffsetDateTime.now();
                daten.setZeitDerLetztenAederung(now);
                daten.setZeitDesMessens(now);
                notifyObservers();
            }
        };
        timer.scheduleAtFixedRate(task,500, 9000);

    }

    public WetterDatenModel messeDaten()
    {
        return new WetterDatenModel();;
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
    public void notifyObservers() {
        for(IObserver obs : observers)
        {
            Pair<Integer,WetterDatenModel> update = new Pair<Integer, WetterDatenModel>(id,daten);
            obs.update(update);
        }
    }

    @Override
    public void registerObserver(IObserver obs)
    {
        observers.add(obs);
    }
}
