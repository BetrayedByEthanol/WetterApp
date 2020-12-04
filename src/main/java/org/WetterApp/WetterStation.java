package org.WetterApp;

import javafx.util.Pair;
import org.WetterApp.Data.DbContext;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.MockWetterSensor;
import org.WetterApp.Models.WetterDatenModel;

import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;

public class WetterStation implements IObserver, IObservable {

    private DbContext context;
    private static WetterStation Instance;
    private ArrayList<IObserver> observers = new ArrayList<>();

    public ArrayList<WetterDatenModel> getWetterDaten() {
        return wetterDaten;
    }

    private ArrayList<WetterDatenModel> wetterDaten = new ArrayList<>();

    private WetterStation()
    {
        context = new DbContext();
        init();
    }

    public static WetterStation getInstance()
    {
        if(Instance == null) Instance = new WetterStation();
        return  Instance;
    }

    @Override
    public void update(Object daten)
    {
        WetterDatenModel wetterdaten = ((Pair<Integer,WetterDatenModel>) daten).getValue();
        wetterDaten.add(wetterdaten);
        notifyObservers();
    }

    public void init()
    {
        //Ueberpruefe, ob die Datenbank exsistiert.

        //Lade bzw. registiere Sensoren
        MockWetterSensor sensor1 = new MockWetterSensor();
        sensor1.registerObserver(this);
        sensor1.setId(1);
        MockWetterSensor sensor2 = new MockWetterSensor();
        sensor2.registerObserver(this);
        sensor2.setId(2);
        MockWetterSensor sensor3 = new MockWetterSensor();
        sensor3.registerObserver(this);
        sensor3.setId(3);
        MockWetterSensor sensor4 = new MockWetterSensor();
        sensor4.setId(4);
        sensor4.registerObserver(this);
    }

    @Override
    public void notifyObservers() {
        for(IObserver obs : observers){
            obs.update(wetterDaten);
        }
    }

    @Override
    public void registerObserver(IObserver Observer) {
        observers.add(Observer);
    }

    public void removeObserver(IObserver obs){
        observers.remove(obs);
    }
}
