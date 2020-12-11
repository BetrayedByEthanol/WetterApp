package org.WetterApp;


import org.WetterApp.Data.IDbContext;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.MockWetterSensor;
import org.WetterApp.Models.WetterDatenModel;


public class WetterStation implements IObserver<WetterDatenModel>, IObservable {

    private IDbContext context;
    private static WetterStation Instance;
    private IObserver observer;

    private WetterDatenModel wetterDaten;

    private WetterStation()
    {
        this.context = IDbContext.DB_CONTEXT;
        init();
    }

    public static WetterStation getInstance()
    {
        if(Instance == null) Instance = new WetterStation();
        return  Instance;
    }

    @Override
    public synchronized void update(WetterDatenModel daten)
    {
		wetterDaten = daten;
		
		//Store in DB
        
		notifyObservers();
    }

    public void init()
    {
        //Ueberpruefe, ob die Datenbank exsistiert.

        //Lade bzw. registiere Sensoren
        MockWetterSensor sensor1 = new MockWetterSensor();
        sensor1.registerObserver(this);
        sensor1.setId(1);
        sensor1.start();
        MockWetterSensor sensor2 = new MockWetterSensor();
        sensor2.registerObserver(this);
        sensor2.setId(2);
        sensor2.start();
        MockWetterSensor sensor3 = new MockWetterSensor();
        sensor3.registerObserver(this);
        sensor3.setId(3);
        sensor3.start();
        MockWetterSensor sensor4 = new MockWetterSensor();
        sensor4.setId(4);
        sensor4.start();
        sensor4.registerObserver(this);
    }

    @Override
    public void notifyObservers() {

       if(observer != null)observer.update(wetterDaten);

    }

    @Override
    public void registerObserver(IObserver Observer) {
        observer = Observer;
    }


}
