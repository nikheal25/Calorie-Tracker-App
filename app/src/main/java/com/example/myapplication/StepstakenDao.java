package com.example.myapplication;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface StepstakenDao {
    @Query("SELECT * FROM Stepstaken")
    List<Stepstaken> getAll();

    @Insert
    void insertAll(Stepstaken... stepstaken);

    @Insert
    long insert(Stepstaken stepsTaken);

    @Update
    void updatesteps (Stepstaken stepstaken);

    @Delete
    void deleteMovie (Stepstaken stepstaken);

    @Query("DELETE FROM Stepstaken")
    void deleteAll();
}
