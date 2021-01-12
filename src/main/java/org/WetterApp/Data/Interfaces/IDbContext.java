package org.WetterApp.Data.Interfaces;

public interface IDbContext extends AutoCloseable{
    void saveChanges();
}
