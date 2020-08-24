package com.myplant.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Query("SELECT * FROM DataReading")
    List<DataReading> getAll();

    @Insert
    void insert(DataReading dataReading);

    @Delete
    void delete(DataReading dataReading);
}
