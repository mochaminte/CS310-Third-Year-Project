package com.example.shine.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


public class CardViewModel extends AndroidViewModel {
    public CardViewModel(@NonNull Application application) {
        super(application);
    }
    /*

    private final Card[] mAllCards;
    public CardViewModel (Application application) {
        super(application);
        mAllCards = mRepository.loadAllCards();
    }

    /*
    public Card[] loadAllCards() { return mAllCards; }

    public void insert(Card card) { mRepository.insert(card); }
    */

}
