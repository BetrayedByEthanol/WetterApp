package org.WetterApp.Interfaces;

import org.WetterApp.Models.WetterDatenModel;

import java.util.ArrayList;
import java.util.List;

public interface IObservable {
    List<IObserver> observer = new ArrayList<IObserver>();
    public void notifyObservers(WetterDatenModel daten);
}
