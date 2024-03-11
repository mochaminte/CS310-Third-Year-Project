package com.example.shine.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;
import java.util.UUID;

@Dao
public interface FsrsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Fsrs fsrs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Fsrs> fsrs);

    @Query("DELETE FROM fsrs")
    void deleteAll();

    @Update
    void update(Fsrs fsrs);

    @Query("DELETE FROM fsrs WHERE carduuid = :carduuid")
    void deleteCard(UUID carduuid);

    @Query("SELECT * FROM fsrs")
    public List<Fsrs> loadAllFsrs();

    @Query("SELECT * FROM fsrs WHERE carduuid = :carduuid")
    public Fsrs getFsrsByCardId(UUID carduuid);

    @Query("SELECT next_repetition FROM fsrs ORDER BY next_repetition ASC LIMIT 1")
    public int checkIfDueCards();

    @RawQuery
    public List<Fsrs> getDueCards(SupportSQLiteQuery query);
    // "SELECT * FROM fsrs WHERE next_repetition <= System.currentTimeMillis()"

    @Query("SELECT COUNT(*) AS num_fsrs FROM fsrs WHERE state <> 'NEW'")
    public int learntWords();

    @RawQuery
    public int getDueCardNumber(SupportSQLiteQuery query);

    @Query("SELECT COUNT(*) AS num_fsrs FROM fsrs WHERE state == 'NEW'")
    public int newWords();
}
