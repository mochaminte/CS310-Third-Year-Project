package com.example.shine;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.ItemViewHolder>{
    private ArrayList<String> items;
    private final Context context;
    private final LayoutInflater layoutInflater;

    public VideoPagerAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.items = items;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.video_carousel, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(items.get(position), context.getString(R.string.count_indicator_text, position + 1, items.size()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewAttachedToWindow(ItemViewHolder holder) {
        holder.onViewAppear();
        super.onViewAttachedToWindow(holder);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final VideoView videoView;
        private final TextView indicatorTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.carouselVideoView);
            indicatorTextView = itemView.findViewById(R.id.imagePositionIndicatorTextView);
        }

        public void bind(String videoUrl, String countIndicatorText) {
            // videoView.setImageResource(imageId);
            Uri uri = Uri.parse(videoUrl);
            videoView.setVideoURI(uri);
            videoView.start();

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            indicatorTextView.setText(countIndicatorText);
        }

        public void onViewAppear() {
            indicatorTextView.setAlpha(1.0f);
            fadeAwayIndicatorTextViewWithDelay();
        }

        private void fadeAwayIndicatorTextViewWithDelay() {
            // Create ObjectAnimator instance
            ObjectAnimator animator = ObjectAnimator.ofFloat(indicatorTextView, "alpha", 1f, 0f);
            animator.setDuration(1000);
            animator.setStartDelay(1000);
            animator.start();
        }
    }
}
