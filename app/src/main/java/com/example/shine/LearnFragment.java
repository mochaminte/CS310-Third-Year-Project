package com.example.shine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shine.Util.Model;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class LearnFragment extends Fragment {
    RecyclerView recycler;
    List<Model> modelList;
    int[] imgs;
    RecyclerView.Adapter adapter;
    RelativeLayout relativeLayout;

    public LearnFragment(){
        // require a empty public constructor
    }

    public String readJSON(){
        String json = null;
        try {
            InputStream is = requireContext().getAssets().open("categories.json");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        if(getArguments() != null && Objects.requireNonNull(requireArguments().getString("finished")).equalsIgnoreCase("true")){
            // Create snackbar if user has finished cards
            relativeLayout = rootView.findViewById(R.id.learning_fragment);
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Finished learning cards for this category.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        modelList = new ArrayList<>();
        // array of all category card images in order, making it easier to add or remove
        imgs = new int[]{
                R.drawable.verbs,
                R.drawable.objects,
                R.drawable.numerals,
                R.drawable.adjectives_and_adverbs,
                R.drawable.places,
                R.drawable.animals,
                R.drawable.people,
                R.drawable.food,
                R.drawable.modal_verbs,
                R.drawable.questions,
                R.drawable.clothing,
                R.drawable.calendar_time,
                R.drawable.sports,
                R.drawable.school_terminology,
                R.drawable.function_terms,
                R.drawable.time_related_terms,
                R.drawable.colours,
                R.drawable.abstract_concepts,
                R.drawable.deaf_related_terms,
                R.drawable.equality,
                R.drawable.name_signs,
                R.drawable.pandemic
        };

        recycler = rootView.findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        JSONObject obj;
        try {
            obj = new JSONObject(readJSON());
            // JSONArray wordArray = obj.getJSONObject("word");
            Iterator<String> it = obj.keys();

            for(int i=0, max=obj.length(); i < max; i++){
                String category = it.next();
                String description = "Signs: " + obj.getJSONArray(category).toString().replace(",","").replace("\""," ").replace("[","").replace("]","");
                modelList.add(new Model(imgs[i], category, description));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        adapter = new CardAdapter(modelList, getActivity());
        recycler.setAdapter(adapter);

        return rootView;
    }

}