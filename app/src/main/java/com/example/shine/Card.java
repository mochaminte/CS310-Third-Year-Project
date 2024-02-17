package com.example.shine;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Card {
    @PrimaryKey
    public String carduuid;
    @ColumnInfo(defaultValue = "")
    public String front;
    @ColumnInfo(defaultValue = "")
    public String back;
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public String created;
}

