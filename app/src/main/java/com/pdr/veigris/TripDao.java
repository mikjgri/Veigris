package com.pdr.veigris;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TripDao {
    @Query("SELECT * FROM trip")
    List<Trip> getAll();

    @Insert
    void insertAll(Trip... trips);

    @Query("DELETE FROM trip")
    void nukeTrips();
}
