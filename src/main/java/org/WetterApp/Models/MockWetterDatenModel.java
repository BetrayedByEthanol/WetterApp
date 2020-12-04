package org.WetterApp.Models;

import java.util.Random;

public class MockWetterDatenModel extends WetterDatenModel
{
    public MockWetterDatenModel() {
        super();
        Random rng = new Random();
        this.setCo2(rng.nextDouble()/10);
        this.setLuftDruck(rng.nextInt(100)+950);
        this.setLuftFeuchtigkeit(rng.nextDouble()*0.8);
        this.setTempInC(rng.nextInt(35));
        this.setWindGeschw(rng.nextInt(15)+5);
    }


}
