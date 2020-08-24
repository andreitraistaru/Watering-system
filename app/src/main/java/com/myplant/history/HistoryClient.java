package com.myplant.history;

import android.content.Context;

import androidx.room.Room;

public class HistoryClient {
    private static HistoryClient instance = null;
    private Context context;
    private History database;

    private HistoryClient(Context context) {
        this.context = context;
        this.database = Room.databaseBuilder(context, History.class, "PlantHistory").build();
    }

    public History getDatabase() {
        return database;
    }

    public static HistoryClient getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryClient(context);
        }

        return instance;
    }
}
