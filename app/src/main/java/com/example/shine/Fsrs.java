package com.example.shine;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Fsrs {
    @PrimaryKey
    public String carduuid;
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
}
