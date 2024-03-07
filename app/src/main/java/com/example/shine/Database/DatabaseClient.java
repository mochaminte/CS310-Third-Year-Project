package com.example.shine.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;
    private CardDatabase cardDatabase;

    private DatabaseClient(Context context) {
        this.context = context;

        //creating the app database with Room database builder
        this.cardDatabase = Room.databaseBuilder(context, CardDatabase.class, "card_database")
                .addCallback(CardDatabase.sRoomDatabaseCallback(context))
                .allowMainThreadQueries()
                .build();
        Log.d("DATABASE", "is this being called ?");
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        Log.d("DATABASE", "is this being called 2 ?");
        return instance;
    }

    public CardDatabase getAppDatabase() {
        cardDatabase.getOpenHelper().getWritableDatabase();
        Log.d("DATABASE", "is this not being called ? ");
        return cardDatabase;
    }
}
