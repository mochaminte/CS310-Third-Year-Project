package com.example.shine.Database;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
            Card.class,
            Fsrs.class
        },
        version = 2,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class CardDatabase extends RoomDatabase {
    public abstract CardDao cardDao();
    public abstract FsrsDao fsrsDao();
    private static volatile CardDatabase INSTANCE;
    private static Context sContext;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    // static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();


    public static CardDatabase getDatabase(final Context context) {
        sContext = context;
        synchronized (CardDatabase.class) {
            if (INSTANCE == null) {
                Log.d("DATABASE", "Getting database");
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                CardDatabase.class, "card_database")
                        .addCallback(sRoomDatabaseCallback) // populates the database
                        .fallbackToDestructiveMigration()
                        .build();
                INSTANCE.cardDao().loadAllCards();
                Log.d("DATABASE", "is this being called HERE ?");
            }
        }
        return INSTANCE;
    }

    public static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
         @Override
         public void onCreate(@NonNull SupportSQLiteDatabase db) {
             Log.d("DATABASE", "onCreate is being called");
             super.onCreate(db);
             Log.d("DATABASE", "onCreate callback triggered.");
             Pair<List<Card>, List<Fsrs>> pair = populateDatabase();
             List<Card> cards = pair.first;
             List<Fsrs> fsrs = pair.second;
             CardDao cardDao = CardDatabase.getDatabase(sContext.getApplicationContext()).cardDao();
             FsrsDao fsrsDao = CardDatabase.getDatabase(sContext.getApplicationContext()).fsrsDao();
             databaseWriteExecutor.execute(() -> {
                 // Populate the database in the background.
                 cardDao.insertAll(cards);
                 Log.d("DATABASE", "Added cards");
             });
             databaseWriteExecutor.execute(() -> {
                 // Populate the database in the background.
                 fsrsDao.insertAll(fsrs);
                 Log.d("DATABASE", "Added fsrs");
             });
         }
    };

    public static Pair<List<Card>, List<Fsrs>> populateDatabase(){
        List<Card> cards = new ArrayList<>();
        List<Fsrs> fsrs = new ArrayList<>();
        try {
            Log.d("DATABASE", "Something happening here ?");

            // cardDao.insertAll(Card.populateData(context.getApplicationContext()));

            final String TAG = "DATABASE";

            JSONObject categ_obj = new JSONObject(Objects.requireNonNull(readJSON("categories.json", sContext.getApplicationContext())));
            JSONObject bsldict_obj = new JSONObject(Objects.requireNonNull(readJSON("bsldict.json", sContext.getApplicationContext())));

            JSONArray wordArray = bsldict_obj.getJSONArray("word");
            JSONArray videoArray = bsldict_obj.getJSONArray("video_link_db");
            JSONArray methodArray = bsldict_obj.getJSONArray("download_method_db");

            Iterator<String> categories = categ_obj.keys();

            Log.d(TAG, "Searching for words");

            // TODO just do this once to create another JSON file to populate the database
            // for each category, add a video url for each word in the category only if the method is wget
            for (int j = 0, max_cat = categ_obj.length(); j < max_cat; j++) {
                String cur_cat_word = categories.next();
                JSONArray cur_cat = categ_obj.getJSONArray(cur_cat_word);
                for (int i = 0, max = cur_cat.length(); i < max; i++) {
                    for (int k = 0, max_words = wordArray.length(); k < max_words; k++) {
                        if (wordArray.getString(k).equalsIgnoreCase(cur_cat.getString(i).replace(" ", "-"))) {
                            if (methodArray.getString(k).equalsIgnoreCase("wget")) {
                                // Log.d(TAG, "video url " + "#" + k + ":" + videoArray.getString(i));
                                Log.d("DATABASE", "Added word " + wordArray.getString(k));
                                // Card with the video url as the front / "test"
                                UUID uuid1 = UUID.randomUUID();
                                UUID uuid2 = UUID.randomUUID();
                                cards.add(new Card(
                                        uuid1,                      // uuid
                                        videoArray.getString(k),    // front
                                        wordArray.getString(k),     // back
                                        cur_cat_word,               // category
                                        true                        // sign question & word answer
                                ));
                                // Card with the word as the front / "test"
                                cards.add(new Card(
                                        uuid2,                      // uuid
                                        wordArray.getString(k),     // front
                                        videoArray.getString(k),    // back
                                        cur_cat_word,               // category
                                        false                       // word question & sign answer
                                ));

                                fsrs.add(new Fsrs(uuid1, cur_cat_word));
                                fsrs.add(new Fsrs(uuid2, cur_cat_word));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(cards, fsrs);
    }

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
