package org.WetterApp.Models.DataTransitionalObjects;

import com.google.gson.annotations.Expose;

public class WetterDatenDTO {

    @Expose
    private double windGeschw;
    @Expose
    private double tempInC;
    @Expose
    private double co2;
    @Expose
    private double luftDruck;
    @Expose
    private double luftFeuchtigkeit;
    @Expose
    private String zeitDesMessens;
    @Expose
    private int gemessenVon;

    public double getWindGeschw() {
        return windGeschw;
    }

    public void setWindGeschw(double windGeschw) {
        this.windGeschw = windGeschw;
    }

    public double getTempInC() {
        return tempInC;
    }

    public void setTempInC(double tempInC) {
        this.tempInC = tempInC;
    }

    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public double getLuftDruck() {
        return luftDruck;
    }

    public void setLuftDruck(double luftDruck) {
        this.luftDruck = luftDruck;
    }

    public double getLuftFeuchtigkeit() {
        return luftFeuchtigkeit;
    }

    public void setLuftFeuchtigkeit(double luftFeuchtigkeit) {
        this.luftFeuchtigkeit = luftFeuchtigkeit;
    }

    public String getZeitDesMessens() {
        return zeitDesMessens;
    }

    public void setZeitDesMessens(String zeitDesMessens) {
        this.zeitDesMessens = zeitDesMessens;
    }

    public int getGemessenVon() {
        return gemessenVon;
    }

    public void setGemessenVon(int gemessenVon) {
        this.gemessenVon = gemessenVon;
    }
}
