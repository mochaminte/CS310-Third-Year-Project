package com.example.shine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SearchableActivity extends Activity {
    private static final String TAG = "SEARCH";
    SearchView searchView;
    ListView listView;

    ArrayList<String> arrayList;
    ArrayAdapter<?> adapter;

    public static ArrayList<String> readLinesFromAssets(Context context, String filename) {
        ArrayList<String> lines = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "file not opened");
        }

        return lines;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchView=findViewById(R.id.searchView);
        listView=findViewById(R.id.listView);

        // ADD DATA HERE
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                arrayList = (ArrayList<String>) Files.readAllLines(Paths.get("src/main/assets/output_words.txt"));
            } catch (IOException e) {
                Log.d(TAG, "file not opened");
                throw new RuntimeException(e);
            }
        }

         */

        arrayList = readLinesFromAssets(this, "output_words.txt");

        adapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String word = ((TextView) view).getText().toString();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("searched_word", word);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }
}
