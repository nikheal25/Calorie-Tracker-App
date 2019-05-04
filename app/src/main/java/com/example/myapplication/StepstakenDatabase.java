package com.example.myapplication;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Stepstaken.class}, version = 2, exportSchema = false)
public abstract class StepstakenDatabase extends RoomDatabase {
    public abstract StepstakenDao stepsTakenDao();

    private static volatile StepstakenDatabase INSTANCE;
    static StepstakenDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (StepstakenDatabase.class){
                if (INSTANCE==null){
                    Room.databaseBuilder(context.getApplicationContext(), StepstakenDatabase.class, "steps_taken_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
