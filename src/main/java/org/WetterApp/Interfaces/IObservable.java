package org.WetterApp.Interfaces;

import org.WetterApp.Models.WetterDatenModel;

import java.util.ArrayList;
import java.util.List;

public interface IObservable {
    void notifyObservers();
    void registerObserver(IObserver Observer);
}
