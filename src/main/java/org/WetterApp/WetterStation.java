package org.WetterApp;

import org.WetterApp.Data.DbContext;
import org.WetterApp.Interfaces.IObserver;
import org.WetterApp.Models.WetterDatenModel;

public class WetterStation implements IObserver {

    private DbContext context;
    private static WetterStation Instance;

    private WetterStation()
    {
        context = new DbContext();
    }

    public static WetterStation getInstance()
    {
        if(Instance == null) Instance = new WetterStation();
        return  Instance;
    }

    @Override
    public void update(int id, WetterDatenModel daten)
    {

    }
}
