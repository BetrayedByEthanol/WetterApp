package org.WetterApp;


import com.google.gson.Gson;
import org.WetterApp.Data.IDbContext;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Mapping.WetterDatenMapper;
import org.WetterApp.Models.DataTransitionalObjects.WetterDatenDTO;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Simulation.RandomWetterSensor;
import org.WetterApp.Models.WetterDatenModel;


public class WetterStation implements IObserver<String>, IObservable {

    private IDbContext context;
    private static WetterStation Instance;
    private IObserver observer;

    private WetterDatenModel wetterDaten;

    private WetterStation()
    {
        this.context = IDbContext.DB_CONTEXT;
    }

    public static WetterStation getInstance()
    {
        if(Instance == null) Instance = new WetterStation();
        return  Instance;
    }

    @Override
    public synchronized void update(String daten)
    {
        Gson gson = new Gson();
        WetterDatenDTO dto =  gson.fromJson(daten, WetterDatenDTO.class);

        try{
            WetterDatenModel model = WetterDatenMapper.MAPPER.map(dto);

            wetterDaten = WetterDatenValidation.validate(model);

            wetterDaten = context.getWetterdatenContext().speichereWetterdaten(wetterDaten);

            notifyObservers();

        }catch (Exception ex)
        {
            System.out.println("Programm still loading...");
            return;
        }
        catch(Error error){
            System.out.println("Programm still loading...");
            return;
        }

    }


    @Override
    public void notifyObservers() {

       if(observer != null  )observer.update(wetterDaten);

    }

    @Override
    public void registerObserver(IObserver Observer) {
        observer = Observer;
    }


}
