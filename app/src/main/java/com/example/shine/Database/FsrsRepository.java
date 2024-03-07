package com.example.shine.Database;

import android.app.Application;

import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

class FsrsRepository {
    /*
    TODO i should just delete this at this rate

    private FsrsDao FsrsDao;
    private Fsrs[] AllFsrs;
    private Fsrs[] dueFsrs;

    String queryString = "SELECT * FROM fsrs WHERE next_repetition <= ?";
    long currentTimeMillis = System.currentTimeMillis();
    SupportSQLiteQuery query = new SimpleSQLiteQuery(queryString, new Object[]{currentTimeMillis});

    FsrsRepository(Application application) {
        CardDatabase db = CardDatabase.getDatabase(application);
        FsrsDao = db.fsrsDao();
        AllFsrs = FsrsDao.loadAllFsrs();
        dueFsrs = FsrsDao.getDueCards(query);
    }

    Fsrs[] loadAllFsrs() {
        return AllFsrs;
    }

    void insert(Fsrs fsrs) {
        CardDatabase.databaseWriteExecutor.execute(() -> {
            FsrsDao.insert(fsrs);
        });
    }

    Fsrs[] getDueCards() {
        return dueFsrs;
    }
    */
}
