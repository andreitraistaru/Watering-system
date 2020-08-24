package com.myplant.history;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataReading.class}, version = 1)
public abstract class History extends RoomDatabase {
    public abstract HistoryDAO getHistoryDAO();
}
