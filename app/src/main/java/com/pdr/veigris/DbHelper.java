package com.pdr.veigris;

import android.content.Context;

import androidx.room.Room;

public class DbHelper {
    @SuppressWarnings("SpellCheckingInspection")
    private static String DbName = "veigris-db";

    private Context currentContext;
    public DbHelper(Context currentContext){
        this.currentContext = currentContext;
    }
    public TripDao GetTripDao(){
        return Room.databaseBuilder(this.currentContext, AppDatabase.class, DbName).build().tripDao();
    }
}
