package com.myplant.history;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DataReading {
    @PrimaryKey
    private long id;

    private String readingTime;
    private int airHumidity;
    private int airTemperature;
    private int soilHumidity;

    public DataReading(long id, String readingTime, int airHumidity, int airTemperature, int soilHumidity) {
        this.id = id;
        this.readingTime = readingTime;
        this.airHumidity = airHumidity;
        this.airTemperature = airTemperature;
        this.soilHumidity = soilHumidity;
    }

    public long getId() {
        return id;
    }
    public String getReadingTime() {
        return readingTime;
    }
    public int getAirHumidity() {
        return airHumidity;
    }
    public int getAirTemperature() {
        return airTemperature;
    }
    public int getSoilHumidity() {
        return soilHumidity;
    }
}
