package com.example.shine;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class DictionaryActivity extends AppCompatActivity {
    private static final String TAG = "DICTIONARY";
    private VideoView mVideoView;
    private Button mSlow;
    private String videoUrl;
    private int currentIndex = 0;
    ArrayList<Integer> indexes = new ArrayList<Integer>();
    ArrayList<String> videoUrls = new ArrayList<String>();
    private Boolean slowPlayback = false;




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
        setContentView(R.layout.dictionary);

        // Get the intent, verify the action, and get the query.
        Intent intent = getIntent();
        String searchedWord = Objects.requireNonNull(intent.getStringExtra("searched_word")).replace(' ', '-');
        String originalWord = intent.getStringExtra("searched_word");

        Button mPlay = findViewById(R.id.playButton);
        Button mLeft = findViewById(R.id.leftButton);
        Button mRight = findViewById(R.id.rightButton);
        mSlow = findViewById(R.id.slowButton);

        try {
            JSONObject obj = new JSONObject(readJSON());
            // set text to be the word
            TextView mWord = (TextView) findViewById(R.id.word);
            JSONArray wordArray = obj.getJSONArray("word");
            for(int i = 0, max = wordArray.length(); i<max; i++){
                if(wordArray.getString(i).equalsIgnoreCase(searchedWord)) {
                    indexes.add(i);
                }
            }

            mWord.setText(originalWord);
            // set video link (make sure it's a mp4 link only (method will be `wget`)
            JSONArray methodArray = obj.getJSONArray("download_method_db");
            JSONArray videoLinkArray = obj.getJSONArray("video_link_db");
            for(int i=0, max=indexes.size(); i<max; i++){
                if(methodArray.getString(indexes.get(i)).equalsIgnoreCase("wget")){
                    videoUrls.add(videoLinkArray.getString(indexes.get(i)));
                    Log.d(TAG, "video url " + "#" + i + ":" + videoLinkArray.getString(indexes.get(i)));

                }

            }
            videoUrl = videoUrls.get(currentIndex);

            mVideoView = (VideoView) findViewById(R.id.video);
            Uri uri = Uri.parse(videoUrl);
            mVideoView.setVideoURI(uri);
            mVideoView.start();


            // Automatically sets video to loop until user presses stop
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            mPlay.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    playVideo();
                }
            });
            mLeft.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    /**
                     if (mVideoView != null) {
                     mVideoView.pause();
                     }
                     */
                    if(currentIndex > 0) {
                        currentIndex--;
                        Uri uri = Uri.parse(videoUrls.get(currentIndex));
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    }
                    else{
                        // Replace Toast with carousel indicators
                        Toast.makeText(getApplicationContext(),"No more previous videos.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mRight.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    /*
                     if (mVideoView != null) {
                     mVideoView.seekTo(0);
                     }
                     */
                    if(currentIndex < indexes.size() - 2) {
                        currentIndex++;
                        Uri uri = Uri.parse(videoUrls.get(currentIndex));
                        mVideoView.setVideoURI(uri);
                        mVideoView.start();
                    } else{
                        // Replace Toast with carousel indicators
                        Toast.makeText(getApplicationContext(),"No more videos to load.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mSlow.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    /*
                     if (mVideoView != null) {
                     videoUrl = null;
                     mVideoView.stopPlayback();
                     }
                     */
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // TODO fix playback not working
                            PlaybackParams myPlayBackParams = new PlaybackParams();
                            if(!slowPlayback) {
                                myPlayBackParams.setSpeed(0.5f); //here set speed eg. 0.5 for slow 2 for fast mode
                                slowPlayback = true;
                                mSlow.setText("Normal Speed");
                            } else{
                                myPlayBackParams.setSpeed(1.0f);
                                slowPlayback = false;
                                mSlow.setText("0.5x Speed");
                            }
                            mp.setPlaybackParams(myPlayBackParams);
                            mVideoView.start();//start your video.
                        }
                    });
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    playVideo();
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSON file could not be read.");
        }
    }

    // TODO need to fix
    private void playVideo() {
        try {
            /*
            final String path = mPath.getText().toString();
            Log.v(TAG, "path: " + path);
            if (path == null || path.length() == 0) {
                Toast.makeText(MainActivity.this, "File URL/path is empty",
                        Toast.LENGTH_LONG).show();

            } else {
                // If the path has not changed, just start the media player
                if (path.equals(current) && mVideoView != null) {
                    mVideoView.start();
                    mVideoView.requestFocus();
                    return;
                }
                current = path;
                mVideoView.setVideoPath(getDataSource(path));

             */
            Uri uri = Uri.parse(videoUrl);
            mVideoView.setVideoURI(uri);
            mVideoView.start();
            mVideoView.requestFocus();

            //}
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            if (mVideoView != null) {
                mVideoView.stopPlayback();
            }
        }
    }

    private String getDataSource(String path) throws IOException {
        if (!URLUtil.isNetworkUrl(path)) {
            return path;
        } else {
            URL url = new URL(path);
            URLConnection cn = url.openConnection();
            cn.connect();
            InputStream stream = cn.getInputStream();
            if (stream == null)
                throw new RuntimeException("stream is null");
            File temp = File.createTempFile("mediaplayertmp", "dat");
            temp.deleteOnExit();
            String tempPath = temp.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(temp);
            byte buf[] = new byte[128];
            do {
                int numread = stream.read(buf);
                if (numread <= 0)
                    break;
                out.write(buf, 0, numread);
            } while (true);
            try {
                stream.close();
            } catch (IOException ex) {
                Log.e(TAG, "error: " + ex.getMessage(), ex);
            }
            return tempPath;
        }
    }
}
