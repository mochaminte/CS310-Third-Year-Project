package com.example.shine.Database;

import android.app.Application;

import java.util.List;

class CardRepository {
    /*
    private CardDao mCardDao;
    private List<Card> mAllCards;

    CardRepository(Application application) {
        CardDatabase db = CardDatabase.getDatabase(application);
        mCardDao = db.cardDao();
        mAllCards = mCardDao.loadAllCards();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    Card[] loadAllCards() {
        return mAllCards;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Card card) {
        CardDatabase.databaseWriteExecutor.execute(() -> {
            mCardDao.insert(card);
        });
    }

    */
}

