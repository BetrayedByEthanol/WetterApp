package org.WetterApp.Models;

import java.time.OffsetDateTime;

public class WetterDatenModel
{
    private int id;
    private double windGeschw;
    private double tempInC;
    private double co2;
    private double luftDruck;
    private double luftFeuchtigkeit;
    private OffsetDateTime zeitDesMessens;
    private OffsetDateTime zeitDerLetztenAederung;
    private int gemessenVon;

    public int getGemessenVon() {
        return gemessenVon;
    }

    public void setGemessenVon(int gemessenVon) {
        this.gemessenVon = gemessenVon;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public OffsetDateTime getZeitDesMessens() {
        return zeitDesMessens;
    }

    public void setZeitDesMessens(OffsetDateTime zeitDesMessens) {
        this.zeitDesMessens = zeitDesMessens;
    }

    public OffsetDateTime getZeitDerLetztenAederung() {
        return zeitDerLetztenAederung;
    }

    public void setZeitDerLetztenAederung(OffsetDateTime zeitDerLetztenAederung) {
        this.zeitDerLetztenAederung = zeitDerLetztenAederung;
    }
}
