package com.example.shine.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Card {
    @PrimaryKey
    @NonNull
    public UUID carduuid;
    @ColumnInfo(defaultValue = "")
    public String front;
    @ColumnInfo(defaultValue = "")
    public String back;
    /* TODO delete
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
     */
    public long created;
    @ColumnInfo(defaultValue = "RANDOM")
    public String category;
    public Boolean signPrompt; // true if sign is front and word is back (answer)


    public Card(){
        this.carduuid = UUID.randomUUID();
    }
    public Card(@NonNull UUID carduuid, String front, String back, String category, Boolean signPrompt){
        this.carduuid = carduuid;
        this.front = front;
        this.back = back;
        this.category = category;
        this.signPrompt = signPrompt;
    }

    @NonNull
    public UUID getCarduuid(){
        return carduuid;
    }
    public void setCarduuid(@NonNull UUID carduuid) { this.carduuid = carduuid; }
    public String getCardFront(){
        return front;
    }
    public void setCardFront(String front){ this.front = front; }
    public String getCardBack(){
        return back;
    }
    public void setCardBack(String back){ this.back = back; }
    /*
    public void setCardCreated(long created) { this.created = created; }
     */
    public String getCardCategory() { return category; }
    public void setCardCategory(String category) { this.category = category; }
    public Boolean getSignPrompt() { return signPrompt; }
    public void setSignPrompt(Boolean signPrompt) { this.signPrompt = signPrompt; }

    // Pre-populate database
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

    public static ArrayList<Card> populateData(Context context) throws JSONException {
        final String TAG = "DATABASE";

        JSONObject categ_obj = new JSONObject(Objects.requireNonNull(readJSON("categories.json", context)));
        JSONObject bsldict_obj = new JSONObject(Objects.requireNonNull(readJSON("bsldict.json", context)));

        JSONArray wordArray = bsldict_obj.getJSONArray("word");
        JSONArray videoArray = bsldict_obj.getJSONArray("video_link_db");
        JSONArray methodArray = bsldict_obj.getJSONArray("download_method_db");

        ArrayList<String> videoUrls = new ArrayList<String>();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        ArrayList<Card> cards = new ArrayList<Card>();

        Iterator<String> categories = categ_obj.keys();

        Log.d(TAG, "Searching for words");

        // for each category, add a video url for each word in the category only if the method is wget
        for (int j=0, max_cat = categ_obj.length(); j < max_cat; j++ ) {
            String cur_cat_word = categories.next();
            JSONArray cur_cat = categ_obj.getJSONArray(cur_cat_word);
            for (int i = 0, max = cur_cat.length(); i < max; i++) {
                if (wordArray.getString(i).equalsIgnoreCase(cur_cat.getString(i))) {
                    if (methodArray.getString(i).equalsIgnoreCase("wget")) {
                        Log.d(TAG, "video url " + "#" + i + ":" + videoArray.getString(i));
                        // Card with the video url as the front / "test"
                        UUID uuid = UUID.randomUUID();
                        cards.add(new Card(
                                uuid,                       // uuid
                                videoArray.getString(i),    // front
                                wordArray.getString(i),     // back
                                cur_cat_word,               // category
                                true                        // sign question & word answer
                        ));
                        // Card with the word as the front / "test"
                        cards.add(new Card(
                                uuid,
                                wordArray.getString(i),     // front
                                videoArray.getString(i),    // back
                                cur_cat_word,               // category
                                false                       // word question & sign answer
                        ));
                        break;
                    }

                }
            }
        }


        return cards;
    }


}

