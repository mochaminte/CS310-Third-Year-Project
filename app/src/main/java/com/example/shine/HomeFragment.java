package com.example.shine;

import static android.content.Context.MODE_PRIVATE;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment {
    CardView share, notifs;
    TextView number;
    int streak_number;

    public HomeFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        share = view.findViewById(R.id.socialMedia);
        number = view.findViewById(R.id.streakNumber);
        notifs = view.findViewById(R.id.notifications);

        // Share streak using social media
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "I got a streak score of: " + streak_number + " on Shine !";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share Shine streak using"));
            }
        });

        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager supportFragmentManager = requireActivity().getSupportFragmentManager();
                new TimePickerFragment().show(supportFragmentManager, "timePicker");
            }
        }); 
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fetching the stored data from the SharedPreference
        Context context = requireActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF KEY", MODE_PRIVATE);
        Calendar c = Calendar.getInstance();

        int thisDay = c.get(Calendar.DAY_OF_YEAR); // GET THE CURRENT DAY OF THE YEAR
        int lastDay = sharedPreferences.getInt("YOUR DATE PREF KEY", 0);
        int counterOfConsecutiveDays = sharedPreferences.getInt("YOUR COUNTER PREF KEY", 0);

        if(lastDay == thisDay -1){
            // CONSECUTIVE DAYS
            counterOfConsecutiveDays = counterOfConsecutiveDays + 1;

            sharedPreferences.edit().putInt("YOUR DATE PREF KEY", thisDay).apply();
            sharedPreferences.edit().putInt("YOUR COUNTER PREF KEY", counterOfConsecutiveDays).apply();
        } else {
            sharedPreferences.edit().putInt("YOUR DATE PREF KEY", thisDay).apply();
            sharedPreferences.edit().putInt("YOUR COUNTER PREF KEY", 1).apply();
        }
        // set streak number to number of consecutive days
        number.setText(String.format(String.valueOf(counterOfConsecutiveDays)));

        streak_number = counterOfConsecutiveDays;
    }
}