package com.example.shine;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

/**
 * SentenceActivity takes in an input sentence from the user and
 * separates the sentence into signs which are then stitched together
 * to give a "sentence translation".
 */
public class SentenceActivity extends AppCompatActivity {
    private static final String TAG = "SENTENCE STITCH";
    private TextInputLayout input;
    private VideoView videoView;
    private Button submit;
    private String videoUrl;
    private int currentIndex, stringIndex = 0;
    private ConstraintLayout layout;
    private TextView output;
    ArrayList<Integer> indexes = new ArrayList<Integer>();
    ArrayList<String> videoUrls = new ArrayList<String>();
    ArrayList<String> stitchedWords = new ArrayList<String>();
    String sentence;
    String[] words;
    int colour = Color.parseColor("#8692f7");

    public String readJSON(){
        String json = null;
        try {
            InputStream is = getAssets().open("bsldict.json");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        input = (TextInputLayout) findViewById(R.id.input);
        submit = (Button) findViewById(R.id.button);
        videoView = (VideoView) findViewById(R.id.videoView);
        output = (TextView) findViewById(R.id.output);
        layout = findViewById(R.id.layout);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(input.getEditText()).getText().toString().isEmpty()){
                    Snackbar snackbar = Snackbar
                            .make(layout, "Enter a sentence.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else {
                    stitchedWords.clear();
                    indexes.clear();
                    videoUrls.clear();

                    sentence = input.getEditText().getText().toString();
                    words = sentence.split("\\s+");
                    /* TODO make it remove words like "the" as no sign for that
                     * or make it skip over those words and show what words were found in displayed sentence
                    */

                    // Find words from output.txt and add them to videoUrl array
                    try {
                        JSONObject obj = new JSONObject(readJSON());
                        // set text to be the word
                        JSONArray wordArray = obj.getJSONArray("word");
                        // for each word, find a singular url that matches word
                        for (String word : words) {
                            for (int i = 0, max = wordArray.length(); i < max; i++) {
                                if (wordArray.getString(i).equalsIgnoreCase(word)) {
                                    indexes.add(i);
                                    stitchedWords.add(word);
                                    Log.d(TAG, "Added word: " + word + " to stitchedWords");
                                    break;
                                }
                            }
                        }
                        // Set video link (make sure it's a mp4 link only (method will be `wget`)
                        JSONArray methodArray = obj.getJSONArray("download_method_db");
                        JSONArray videoLinkArray = obj.getJSONArray("video_link_db");
                        for (int i = 0, max = indexes.size(); i < max; i++) {
                            if (methodArray.getString(indexes.get(i)).equalsIgnoreCase("wget")) {
                                videoUrls.add(videoLinkArray.getString(indexes.get(i)));
                                Log.d(TAG, "video url " + "#" + i + ":" + videoLinkArray.getString(indexes.get(i)));
                            }
                        }
                        playNextVideo();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                        Log.d(TAG, "JSON file could not be read.");
                    }
                }
            }
        });
    }

    private void playNextVideo(){
        if (currentIndex < videoUrls.size()) {
            // Set video URL
            videoView.setVideoURI(Uri.parse(videoUrls.get(currentIndex)));

            // Handle video completion
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // Play the next video
                    currentIndex++;
                    playNextVideo();
                }
            });

            // Start playing video
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // Highlight the corresponding word in the sentence
                    highlightWord(stringIndex);

                    // Start playing the video
                    videoView.start();
                }
            });
        } else {
            // Signify end of sentence stitched
            Snackbar snackbar = Snackbar
                    .make(layout, "Sentence finished playing", Snackbar.LENGTH_LONG)
                            .setAction(
                            "REPLAY",
                            // if `REPLAY` button
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    currentIndex = 0;
                                    stringIndex = 0;
                                    playNextVideo();
                                }
                            });
            snackbar.show();
        }
    }

    private void highlightWord(int index) {
        // Update text to highlight current word playing
        Spannable spannable = new SpannableString(TextUtils.join(" ", stitchedWords));
        spannable.setSpan(new ForegroundColorSpan(colour), index, index + stitchedWords.get(currentIndex).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        output.setText(spannable);
        Log.d(TAG, "Current index from highlightWord: "+currentIndex);
        stringIndex += stitchedWords.get(currentIndex).length() + 1;
        Log.d(TAG, "Word: " + stitchedWords.get(currentIndex) + " with length: " + stitchedWords.get(currentIndex).length());
        Log.d(TAG, "String index: "+stringIndex);
    }
}
