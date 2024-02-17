package com.example.shine;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
            Card.class,
            Fsrs.class
        },
        version = 1, exportSchema = false)
public abstract class CardDatabase extends RoomDatabase {
    public abstract CardDao cardDao();
    public abstract FsrsDao fsrsDao();
    private static volatile CardDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CardDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CardDatabase.class, "card_database")
                            .addCallback(sRoomDatabaseCallback) // populates the database
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                CardDao cardDao = INSTANCE.cardDao();
                cardDao.deleteAll();
                // TODO add cards (i.e. links + words)


                FsrsDao fsrsDao = INSTANCE.fsrsDao();
                fsrsDao.deleteAll();
                // TODO add linked learning algorithm for each card (same cardId)
            });
        }
    };
}
