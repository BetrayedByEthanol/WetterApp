package org.WetterApp.Simulation;

import com.google.gson.Gson;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Mapping.WetterDatenMapper;
import org.WetterApp.Models.DataTransitionalObjects.WetterDatenDTO;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.WetterSensorModel;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WetterSensor implements IObservable {

    private int id;
    private ArrayList<IObserver> observers = new ArrayList<>();
    private Timer timer;
    public static ArrayList<WetterSensor> activeSensors = new ArrayList<>();
    private WetterDaten daten;

    public void start(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                daten = messeDaten();
                daten.setGemessenVon(id);
                daten.setZeitDesMessens(OffsetDateTime.now().toString());
                notifyObservers();
            }
        };

        OffsetDateTime rightNow = OffsetDateTime.now();
        long delay = (900 - (rightNow.toEpochSecond() % 900)) * 1000;
        timer.scheduleAtFixedRate(task,delay, 900000);
        activeSensors.add(this);

    }

    public void stop(){
        timer.cancel();
    }


    public String send(WetterDaten daten){
        daten.setCo2(2);
        daten.setGemessenVon(1);
        daten.setTempInC(23);
        daten.setLuftDruck(23);
        daten.setLuftFeuchtigkeit(22);
        daten.setWindGeschw(22);
        daten.setZeitDesMessens(OffsetDateTime.now().toString());
        Gson gson = new Gson();

        return gson.toJson(daten);
    }

    public WetterDaten messeDaten()
    {
        return new WetterDaten();
    }
    @Override
    public void notifyObservers() {
        for(IObserver obs : observers)
        {
            obs.update(send(daten));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<IObserver> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<IObserver> observers) {
        this.observers = observers;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public static ArrayList<WetterSensor> getActiveSensors() {
        return activeSensors;
    }

    public static void setActiveSensors(ArrayList<WetterSensor> activeSensors) {
        WetterSensor.activeSensors = activeSensors;
    }

    public WetterDaten getDaten() {
        return daten;
    }

    public void setDaten(WetterDaten daten) {
        this.daten = daten;
    }

    @Override
    public void registerObserver(IObserver obs)
    {
        observers.add(obs);
    }

}
