package org.WetterApp.Interfaces;

import org.WetterApp.Models.WetterDatenModel;

public interface IObserver {
    public void update(int id, WetterDatenModel daten);
}
