package com.example.shine;

import android.content.Context;
import android.util.Log;

import com.example.shine.Database.Card;
import com.example.shine.Database.CardDatabase;
import com.example.shine.Database.Fsrs;
import com.example.shine.Util.Queue;

import java.util.List;
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
    private final Executor executor = Executors.newSingleThreadExecutor();


    public CardScheduler(String category, Context context) {
        this.queue = new Queue<>();
        this.queueDueCards(category, context);
    }

    public void queueDueCards(String category, Context context) {
        while (!this.queue.isEmpty()) {
            this.queue.dequeue();
        }

        executor.execute(() -> {
            Log.d(TAG,"About to call the database");
            /* List<Fsrs> fsrs = CardDatabase.getDatabase(context)
                    .fsrsDao()
                    .getDueCards(new SimpleSQLiteQuery("SELECT * FROM fsrs WHERE next_repetition <= " + String.format(Locale.getDefault(), "%.3f",  System.currentTimeMillis() / 1000.0) + " AND category LIKE " + category));
            */
            List<Fsrs> fsrs = CardDatabase.getDatabase(context.getApplicationContext())
                    .fsrsDao()
                    .loadAllFsrs();
            fsrs = CardDatabase.getDatabase(context.getApplicationContext())
                    .fsrsDao()
                    .loadAllFsrs();
            for(Fsrs fsrs_card : fsrs){
                Card dueCard = CardDatabase.getDatabase(context.getApplicationContext())
                        .cardDao()
                        .findCardWithId(fsrs_card.getCarduuid());
                queue.enqueue(dueCard);
                Log.d(TAG, "Enqueued a card");
            }
        });
        /*
        try{
            executor.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

         */
        Log.d(TAG, "Finding cards and filling them");

    }

    public void onRating(int rating, String category, Context context) {
        Card ratedCard = this.queue.front();
        executor.execute(() -> {
            // Find all fsrs matching the carduuid of the rated card
            Fsrs fsrs = CardDatabase.getDatabase(context.getApplicationContext())
                    .fsrsDao()
                    .getFsrsByCardId(ratedCard.getCarduuid());

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

            CardDatabase.getDatabase(context.getApplicationContext())
                    .fsrsDao()
                    .update(fsrs);

        });
        Log.d(TAG, "Card " + ratedCard.getCarduuid() + " rated with " + rating);
        this.queue.dequeue();
        if (this.queue.isEmpty()) {
            this.queueDueCards(category, context);
        }
    }

    public String getCardFront(){
        if (!queue.isEmpty()) {
            return this.queue.front().getCardFront();
        }
        else{
            return null;
        }
    }

    public String getCardBack(){
        if (!queue.isEmpty()) {
            return this.queue.front().getCardBack();
        }
        else{
            return null;
        }
    }

    public Boolean getSignPrompt(){
        if (!queue.isEmpty()) {
            return this.queue.front().getSignPrompt();
        }
        else{
            return null;
        }
    }

    public Boolean queueEmpty(){
        return this.queue.isEmpty();
    }
}
