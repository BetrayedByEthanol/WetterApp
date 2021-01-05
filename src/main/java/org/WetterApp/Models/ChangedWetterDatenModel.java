package org.WetterApp.Models;

public class ChangedWetterDatenModel extends WetterDatenModel{

    private int idOfPreviousWetterdaten;

    public int getIdOfPreviousWetterdaten() {
        return idOfPreviousWetterdaten;
    }

    public void setIdOfPreviousWetterdaten(int idOfPreviousWetterdaten) {
        this.idOfPreviousWetterdaten = idOfPreviousWetterdaten;
    }
}
