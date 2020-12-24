package org.WetterApp.Simulation;

import java.util.Random;

public class RandomWetterDaten extends WetterDaten
{
    public RandomWetterDaten() {
        super();
        Random rng = new Random();
        this.setCo2(rng.nextDouble()/10);
        this.setLuftDruck(rng.nextInt(100)+950);
        this.setLuftFeuchtigkeit(rng.nextDouble()*0.8);
        this.setTempInC(rng.nextInt(45) - 10);
        this.setWindGeschw(rng.nextInt(15)+5);
    }


}
