package com.example.shine.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Card card);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Card> order);

    @Query("DELETE FROM card")
    void deleteAll();

    @Query("DELETE FROM card WHERE carduuid = :carduuid")
    void deleteWithId(UUID carduuid);

    @Query("SELECT * FROM card")
    public List<Card> loadAllCards();

    @Query("SELECT * FROM card WHERE front LIKE :search")
    public List<Card> findCardWithFront(String search);

    @Query("SELECT * FROM card WHERE carduuid = :carduuid")
    public Card findCardWithId(UUID carduuid);

    @Query("SELECT * FROM card WHERE category = :category")
    public List<Card> findCardsWithCategory(String category);
}
