package org.WetterApp.Data.RandomlyGeneratedModelContext;

import org.WetterApp.Data.Interfaces.IDbContext;

public class SimulatedDbContext implements IDbContext {
    public void saveChanges(){}

    @Override
    public void close() throws Exception {

    }
}
