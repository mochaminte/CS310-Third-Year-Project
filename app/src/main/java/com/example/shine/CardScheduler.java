package com.example.shine;

import android.content.Context;
import android.util.Log;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.shine.Database.Card;
import com.example.shine.Database.CardDatabase;
import com.example.shine.Database.Fsrs;
import com.example.shine.Util.Queue;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.nickhansen.spacedrepetition.api.algorithm.FSRSAlgorithm;
import de.nickhansen.spacedrepetition.api.algorithm.fsrs.FSRSRating;
import de.nickhansen.spacedrepetition.api.algorithm.fsrs.FSRSState;
import de.nickhansen.spacedrepetition.api.algorithm.result.FSRSAlgorithmResult;

public class CardScheduler {
    private static final String TAG = "DATABASE";
    private Queue<Card> queue;
    private CardDatabase cardDatabase;
    private static final Executor executor = Executors.newSingleThreadExecutor();


    public CardScheduler(String category, Context context) {
        this.queue = new Queue<>();
        this.queueDueCards(category, context);
    }

    public static void startDatabase(Context context){
        executor.execute(() -> {
            CardDatabase.getDatabase(context.getApplicationContext()).cardDao().loadAllCards();
        });

    }

    public void queueDueCards(String category, Context context) {
        while (!this.queue.isEmpty()) {
            this.queue.dequeue();
        }

        List<Fsrs> fsrs = null;
        try {
            fsrs = Executors.newSingleThreadExecutor().submit(() ->
                    CardDatabase.getDatabase(context.getApplicationContext())
                            .fsrsDao()
                            .getDueCards(new SimpleSQLiteQuery("SELECT * FROM fsrs WHERE next_repetition < " + System.currentTimeMillis() / 1000 + " AND category LIKE '" + category + "'"))
            ).get();
        }
        catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for(Fsrs fsrs_card : fsrs){
            executor.execute(() -> {
            Card dueCard = CardDatabase.getDatabase(context.getApplicationContext())
                    .cardDao()
                    .findCardWithId(fsrs_card.getCarduuid());
            queue.enqueue(dueCard);
            });
        }

    }

    public void onRating(int rating, String category, Context context) {
        Card ratedCard = this.queue.front();
        Fsrs fsrs = null;
        try {
            fsrs = Executors.newSingleThreadExecutor().submit(() ->
            // Find the fsrs matching the carduuid of the rated card
            CardDatabase.getDatabase(context.getApplicationContext())
                    .fsrsDao()
                    .getFsrsByCardId(ratedCard.getCarduuid())).get();
        }
        catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        FSRSAlgorithm algorithm = FSRSAlgorithm.builder()
                .rating(FSRSRating.values()[rating])
                .stability(fsrs.getStability())
                .difficulty(fsrs.getDifficulty())
                .elapsedDays(fsrs.getElapsedDays())
                .repetitions(fsrs.getRepetitions())
                .state(FSRSState.valueOf(fsrs.getState()))
                .lastReview(fsrs.getLastReview())
                .build();

        FSRSAlgorithmResult result = algorithm.calc();

        // update fsrs with new attributes from algorithm calculation
        fsrs.setRepetitions(result.getRepetitions());
        fsrs.setDifficulty(result.getDifficulty());
        fsrs.setStability(result.getStability());
        fsrs.setElapsedDays(result.getElapsedDays());
        fsrs.setState(result.getState().toString());
        fsrs.setNextRepetitionTime(result.getNextRepetitionTime());
        fsrs.setLastReview(result.getLastReview());

        Fsrs finalFsrs = fsrs;
        executor.execute(() -> {
            CardDatabase.getDatabase(context.getApplicationContext())
                    .fsrsDao()
                    .update(finalFsrs);
        });

        Log.d(TAG, "Card " + ratedCard.getCarduuid() + " rated with " + rating);
        this.queue.dequeue();

        if (this.queue.isEmpty()) {
            this.queueDueCards(category, context);
        }
    }

    public static int wordsLearnt(Context context){
        int learntWords = 0;
        try {
            learntWords = Executors.newSingleThreadExecutor().submit(() ->
                    // Find the fsrs matching the carduuid of the rated card
                    CardDatabase.getDatabase(context.getApplicationContext())
                            .fsrsDao()
                            .learntWords()).get();
        }
        catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return learntWords;
    }

    public static int wordsToLearn(Context context){
        int words = 0;
        try {
            words = Executors.newSingleThreadExecutor().submit(() ->
                    CardDatabase.getDatabase(context.getApplicationContext())
                            .fsrsDao()
                            .getDueCardNumber(new SimpleSQLiteQuery("SELECT COUNT(*) AS words FROM fsrs WHERE next_repetition <=  CAST(strftime('%s', 'now') AS INTEGER) * 1000 AND state <> 'NEW'"))
            ).get();
        }
        catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return words;
    }

    public static int newCards(Context context){
        int words = 0;
        try {
            words = Executors.newSingleThreadExecutor().submit(() ->
                    CardDatabase.getDatabase(context.getApplicationContext())
                            .fsrsDao()
                            .newWords()
            ).get();
        }
        catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return words;
    }


    public String getCardFront(){
        return this.queue.front().getCardFront();
    }

    public String getCardBack(){
        return this.queue.front().getCardBack();
    }

    public Boolean getSignPrompt(){
        return this.queue.front().getSignPrompt();
    }

    public Boolean queueEmpty(){
        return this.queue.isEmpty();
    }
}
