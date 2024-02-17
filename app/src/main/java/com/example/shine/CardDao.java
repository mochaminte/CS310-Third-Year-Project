package com.example.shine;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Card card);

    @Query("DELETE FROM card")
    void deleteAll();

    @Query("DELETE FROM card WHERE carduuid = :carduuid")
    void deleteWithId(String carduuid);

    @Query("SELECT * FROM card")
    public Card[] loadAllCards();

    @Query("SELECT * FROM card WHERE front LIKE :search")
    public List<Card> findCardWithFront(String search);

    @Query("SELECT * FROM card WHERE carduuid = :carduuid")
    public Card findCardWithId(String carduuid);
}
