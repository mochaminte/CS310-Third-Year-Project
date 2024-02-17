package com.example.shine;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

@Dao
public interface FsrsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Fsrs fsrs);

    @Update
    void update(Fsrs fsrs);

    @Query("DELETE FROM fsrs")
    void deleteAll();

    @Query("DELETE FROM fsrs WHERE carduuid = :carduuid")
    void deleteCard(String carduuid);

    @Query("SELECT * FROM fsrs")
    public Fsrs[] loadAllFsrs();

    @Query("SELECT * FROM fsrs WHERE carduuid = :carduuid")
    public Fsrs getFsrsByCardId(String carduuid);

    @Query("SELECT next_repetition FROM fsrs ORDER BY next_repetition ASC LIMIT 1")
    public int checkIfDueCards();


    @RawQuery
    public Fsrs[] getDueCards(SupportSQLiteQuery query);
    // "SELECT * FROM fsrs WHERE next_repetition <= System.currentTimeMillis()"




}
