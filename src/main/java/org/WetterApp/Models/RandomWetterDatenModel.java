package org.WetterApp.Models;

import java.util.Random;

public class RandomWetterDatenModel extends WetterDatenModel
{
    public RandomWetterDatenModel() {
        super();
        Random rng = new Random();
        this.setCo2(rng.nextDouble()/10);
        this.setLuftDruck(rng.nextInt(100)+950);
        this.setLuftFeuchtigkeit(rng.nextDouble()*0.8);
        this.setTempInC(rng.nextInt(45) - 10);
        this.setWindGeschw(rng.nextInt(15)+5);
    }


}
