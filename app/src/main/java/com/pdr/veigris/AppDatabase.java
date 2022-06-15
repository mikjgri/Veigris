package com.pdr.veigris;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Trip.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TripDao tripDao();
}
