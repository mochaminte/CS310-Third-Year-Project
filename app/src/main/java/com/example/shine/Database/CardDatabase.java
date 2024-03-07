package com.example.shine.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
            Card.class,
            Fsrs.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class CardDatabase extends RoomDatabase {
    public abstract CardDao cardDao();
    public abstract FsrsDao fsrsDao();
    private static volatile CardDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static CardDatabase getDatabase(final Context context) {
        synchronized (CardDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                CardDatabase.class, "card_database")
                        .allowMainThreadQueries()
                        .addCallback(sRoomDatabaseCallback(context)) // populates the database
                        .build();
                Log.d("DATABASE", "is this being called HERE ?");
            }
            return INSTANCE;
        }
    }


    public static RoomDatabase.Callback sRoomDatabaseCallback(final Context context){
         Log.d("DATABASE", "Callback being called? ");
         return new RoomDatabase.Callback() {
             @Override
             public void onCreate(@NonNull SupportSQLiteDatabase db) {
                 super.onCreate(db);
                 Log.d("DATABASE", "onCreate callback triggered.");
                 databaseWriteExecutor.execute(() -> {
                     // Populate the database in the background.
                     try {
                         Log.d("DATABASE", "Something happening here ? ");
                         CardDao cardDao = CardDatabase.getDatabase(context).cardDao();
                         FsrsDao fsrsDao = CardDatabase.getDatabase(context).fsrsDao();
                         cardDao.deleteAll();
                         // cardDao.insertAll(Card.populateData(context.getApplicationContext()));

                         final String TAG = "DATABASE";

                         JSONObject categ_obj = new JSONObject(Objects.requireNonNull(readJSON("categories.json", context)));
                         JSONObject bsldict_obj = new JSONObject(Objects.requireNonNull(readJSON("bsldict.json", context)));

                         JSONArray wordArray = bsldict_obj.getJSONArray("word");
                         JSONArray videoArray = bsldict_obj.getJSONArray("video_link_db");
                         JSONArray methodArray = bsldict_obj.getJSONArray("download_method_db");

                         Iterator<String> categories = categ_obj.keys();

                         Log.d(TAG, "Searching for words");

                         // for each category, add a video url for each word in the category only if the method is wget
                         for (int j=0, max_cat = categ_obj.length(); j < max_cat; j++ ) {
                             String cur_cat_word = categories.next();
                             JSONArray cur_cat = categ_obj.getJSONArray(cur_cat_word);
                             for (int i = 0, max = cur_cat.length(); i < max; i++) {
                                 if (wordArray.getString(i).equalsIgnoreCase(cur_cat.getString(i).replace(" ", "-"))) {
                                     if (methodArray.getString(i).equalsIgnoreCase("wget")) {
                                         Log.d(TAG, "video url " + "#" + i + ":" + videoArray.getString(i));
                                         // Card with the video url as the front / "test"
                                         UUID uuid1 = UUID.randomUUID();
                                         UUID uuid2 = UUID.randomUUID();
                                         cardDao.insert(new Card(
                                                 uuid1,                      // uuid
                                                 videoArray.getString(i),    // front
                                                 wordArray.getString(i),     // back
                                                 cur_cat_word,               // category
                                                 true                        // sign question & word answer
                                         ));
                                         // Card with the word as the front / "test"
                                         cardDao.insert(new Card(
                                                 uuid2,                      // uuid
                                                 wordArray.getString(i),     // front
                                                 videoArray.getString(i),    // back
                                                 cur_cat_word,               // category
                                                 false                       // word question & sign answer
                                         ));

                                         fsrsDao.insert(new Fsrs(uuid1));
                                         fsrsDao.insert(new Fsrs(uuid2));
                                         break;
                                     }

                                 }
                             }
                         }
                     } catch (JSONException e) {
                         Log.d("DATABASE", "failed to add to database");
                     }
                     Log.d("DATABASE", "Populated database in background.");
                 });
             }
         };
    };

    public static String readJSON(String file, Context context){
        String json = null;
        try {
            InputStream is = context.getResources().getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

}
