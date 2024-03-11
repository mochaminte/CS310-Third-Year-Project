package com.example.shine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shine.Util.Model;

import java.util.List;


/**
 * Used to load card information to the view
 * i.e. the category cards in the learning section of the bottom navigation bar
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    //create a list to pass our Model class
    List<Model> modelList;
    Context context;
    public CardAdapter(List<Model> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate our custom view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.learning_card_template,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //bind all custom views by its position
        //to get the positions we call our Model class
        final Model model = modelList.get(position);
        holder.name.setText(model.getName());
        holder.tag.setText(model.getTag());
        holder.imageView.setImageDrawable(context.getResources().getDrawable(model.getImage()));
        //click listener
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LearningActivity.class);
                intent.putExtra("image",model.getImage());
                intent.putExtra("name",model.getName());
                intent.putExtra("tag",model.getTag());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
    //all the custom view will be hold here or initialize here inside MyViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name, tag;
        RelativeLayout relativeLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            tag = itemView.findViewById(R.id.tag);
            relativeLayout = itemView.findViewById(R.id.item);
        }
    }
}