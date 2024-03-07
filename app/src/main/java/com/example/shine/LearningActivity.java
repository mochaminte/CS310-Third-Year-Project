package com.example.shine;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;

public class LearningActivity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private CardScheduler cardScheduler;
    VideoView videoView, videoAnswer;
    TextView name, wordAnswer, word;
    Boolean signPrompt;
    String category, front, back;
    Button again, hard, good, easy, reveal;
    ProgressBar progressBar;
    LinearLayout ratings;
    private Handler handler;
    private int progressStatus = 0;

    public void deleteDatabaseFile(String databaseName) {
        File databases =
                new File(getApplicationInfo().dataDir + "/databases");
        File db = new File(databases, databaseName);

        if (db.delete())
            Log.d("DATABASE", "Database deleted successfully");
        else
            Log.d("DATABASE", "Failed to delete database");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        //  Start python from chaquopy
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        deleteDatabaseFile("card_database");

        // mCardViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(CardViewModel.class);
        progressBar = findViewById(R.id.progress);
        videoView = findViewById(R.id.video);
        word = findViewById(R.id.word);
        wordAnswer = findViewById(R.id.wordAnswer);
        videoAnswer = findViewById(R.id.videoAnswer);
        again = findViewById(R.id.again);
        hard = findViewById(R.id.hard);
        good = findViewById(R.id.good);
        easy = findViewById(R.id.easy);
        ratings = findViewById(R.id.ratings);
        reveal = findViewById(R.id.reveal);


        // TODO make a header and add the category name to the header
        // get the intent
        category = getIntent().getStringExtra("name");
        cardScheduler = new CardScheduler(category, getApplicationContext());

        cardScheduler.queueDueCards(category, getApplicationContext());

        front = cardScheduler.getCardFront(); // front of card
        back = cardScheduler.getCardBack(); // back of card (answer)
        signPrompt = cardScheduler.getSignPrompt(); // check whether front is a sign or a word

        // if sign is the front, set the videoView and set answer to the word meaning
        if(signPrompt){
            Uri uri = Uri.parse(front);
            videoView.setVideoURI(uri);
            videoView.start();
            // Automatically sets video to loop until user presses stop
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            wordAnswer.setText(back); // set answer
        }
        else{
            word.setText(front);
            Uri uri = Uri.parse(back);
            videoAnswer.setVideoURI(uri);
        }

        // set invisibility to ratings
        ratings.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        wordAnswer.setVisibility(View.INVISIBLE);

        // listen for when user presses reveal button
        reveal.setOnClickListener(view -> {
            wordAnswer.setVisibility(View.VISIBLE);
            // let user choose file reveal ratings and make reveal button disappear
            // openFileChooser(); // opens video selector to upload
            reveal.setVisibility(View.GONE);
            ratings.setVisibility(View.VISIBLE);
        });

        // listen for when the user presses rating buttons
        again.setOnClickListener(view -> {
            cardScheduler.onRating(0, category, getApplicationContext());
            loadNext();

        });
        hard.setOnClickListener(view -> {
            cardScheduler.onRating(1, category, getApplicationContext());
            // play next video & set new answer & reset visibility for buttons (gone = no space)
        });
        good.setOnClickListener(view -> {
            cardScheduler.onRating(2, category, getApplicationContext());
        });
        easy.setOnClickListener(view -> {
            cardScheduler.onRating(3, category, getApplicationContext());
        });

        /* TODO sort out progress bar dynamically changing
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            // textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

         */
    }


    public void loadNext(){
        // check if there are cards left to study
        if(cardScheduler.queueEmpty()){
            // TODO add new activity intent to category page w/ snackbar saying completed cards
        }

        front = cardScheduler.getCardFront(); // front of card
        back = cardScheduler.getCardBack(); // back of card (answer)
        signPrompt = cardScheduler.getSignPrompt(); // check whether front is a sign or a word

        // if sign is the front, set the videoview and set answer to the word meaning
        if(signPrompt){
            Uri uri = Uri.parse(front);
            videoView.setVideoURI(uri);
            videoView.start();
            // Automatically sets video to loop until user presses stop
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            wordAnswer.setText(back); // set answer
        }
        else{
            word.setText(front);
            Uri uri = Uri.parse(back);
            videoAnswer.setVideoURI(uri);
        }
    }

    // Code below required for calling inference on model hosted on Hugging Face Spaces
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    private float runModel(String keyword, String video){
        Python py = Python.getInstance();
        PyObject model = py.getModule("model");
        return model.callAttr("model", keyword, video).toFloat();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // passing video TODO make it limit file size
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            Uri videoUri = data.getData();
            String videoPath = getFilePathFromUri(videoUri);
            // Pass videoUri to model for processing
            Toast.makeText(this, "Video selected: " + videoUri.toString(), Toast.LENGTH_SHORT).show();
            float result = runModel(front, videoPath);
            if( result > 0.7 ){
                wordAnswer.setText("Correct"); // TODO change to correct or not
            }
            else{
                wordAnswer.setText("Incorrect");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String getFilePathFromUri(Uri uri) {
        ContentResolver resolver = getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA};
        try (Cursor cursor = resolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
