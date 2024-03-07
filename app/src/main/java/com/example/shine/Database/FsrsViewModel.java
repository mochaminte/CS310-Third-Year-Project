package com.example.shine.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

public class FsrsViewModel extends AndroidViewModel {
    public FsrsViewModel(@NonNull Application application) {
        super(application);
    }
    /*
    TODO to delete
    private FsrsRepository repository;
    private final Fsrs[] allFsrs;
    private final Fsrs[] dueFsrs;

    public FsrsViewModel (Application application) {
        super(application);
        repository = new FsrsRepository(application);
        allFsrs = repository.loadAllFsrs();
        dueFsrs = repository.getDueCards();
    }

    public Fsrs[] loadAllFsrs() { return allFsrs; }


    public Fsrs[] getDueCards() { return dueFsrs; };


    public void insert(Fsrs fsrs) { repository.insert(fsrs); }

    */
}
