package org.WetterApp.Controllers;


import com.google.gson.Gson;
import org.WetterApp.Data.DbContext;
import org.WetterApp.Data.Interfaces.IDbModelContext;
import org.WetterApp.Data.Interfaces.IWetterDatenContext;
import org.WetterApp.Interfaces.IObservable;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Mapping.WetterDatenMapper;
import org.WetterApp.Models.DataTransitionalObjects.WetterDatenDTO;
import org.WetterApp.Models.Validation.WetterDatenValidation;
import org.WetterApp.Models.WetterDatenModel;


public class WetterStationController implements IObserver<String>, IObservable {

    private final IDbModelContext context;
    private static WetterStationController Instance;
    private IObserver observer;

    private WetterDatenModel wetterDaten;

    private WetterStationController()
    {
        this.context = IDbModelContext.MODEL_CONTEXT;
    }

    public static WetterStationController getInstance()
    {
        if(Instance == null) Instance = new WetterStationController();
        return  Instance;
    }

    @Override
    public synchronized void update(String daten)
    {
        Gson gson = new Gson();
        WetterDatenDTO dto =  gson.fromJson(daten, WetterDatenDTO.class);

        while(DbContext.isBusy()) {
            try{
                System.out.println("waiting for sqlite db lock");
                this.wait(10000);
            }catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }

        try(IWetterDatenContext wdContext = context.getWetterdatenContext()){
            WetterDatenModel model = WetterDatenMapper.MAPPER.map(dto);

            wetterDaten = WetterDatenValidation.validate(model);

            wetterDaten = wdContext.speichereWetterdaten(wetterDaten);
            wdContext.saveChanges();

            notifyObservers();

        }catch (Exception ex)
        {
            System.out.println("Programm still loading...");
        }
        catch(Error error){
            System.out.println("Programm still loading...");
        }

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
