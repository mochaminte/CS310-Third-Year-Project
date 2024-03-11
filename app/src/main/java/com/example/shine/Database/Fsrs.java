package com.example.shine.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity
public class Fsrs {
    @PrimaryKey
    @NonNull
    public UUID carduuid;
    @ColumnInfo(defaultValue = "0")
    public float stability;
    @ColumnInfo(defaultValue = "0")
    public float difficulty;
    @ColumnInfo(defaultValue = "0")
    public int elapsed_days;
    @ColumnInfo(defaultValue = "0")
    public int repetitions;
    @ColumnInfo(defaultValue = "NEW")
    public String state;
    @ColumnInfo(defaultValue = "0")
    public int day_interval;
    @ColumnInfo(defaultValue = "0")
    public long next_repetition;
    @ColumnInfo(defaultValue = "0")
    public long last_review;
    public String category;

    @Ignore
    public Fsrs(){
        this.carduuid = UUID.randomUUID();
    }

    public Fsrs(@NonNull UUID carduuid, String category){
        this.carduuid = carduuid;
        this.category = category;
        this.state = "NEW";
    }

    @NonNull
    public UUID getCarduuid(){
        return carduuid;
    }
    public void setCarduuid(UUID carduuid) { this.carduuid = carduuid; }
    public float getStability() { return stability; }
    public void setStability(float stability) { this.stability = stability; }
    public float getDifficulty() { return difficulty; }
    public void setDifficulty(float difficulty) { this.difficulty = difficulty; }
    public int getElapsedDays() { return elapsed_days; }
    public void setElapsedDays(int elapsed_days) { this.elapsed_days = elapsed_days; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions){ this.repetitions = repetitions; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public void setNextRepetitionTime(long next_repetition) { this.next_repetition = next_repetition; }
    public long getLastReview() { return last_review; }
    public void setLastReview(long last_review) { this.last_review = last_review; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

}
