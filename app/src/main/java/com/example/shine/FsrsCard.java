package com.example.shine;

import android.app.Application;

import java.util.UUID;

public class FsrsCard extends Application {
    private static final String TAG = "FSRS_CARD";
    private UUID uuid;
    private String front, back;
    private Long created;

    public FsrsCard(UUID uuid, String front, String back, Long created) {
        this.uuid = uuid;
        this.front = front;
        this.back = back;
        this.created = created;

        // SpacedRepetitionApp.getInstance().getCards().put(uuid, this);
    }

    /* TODO delete ?
    public void create(String front, String back){
        UUID uuid = UUID.randomUUID();
        Long created = System.currentTimeMillis();

        class Create extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids){
                Card card = new Card();
                card.setCarduuid(uuid);
                card.setCardFront(front);
                card.setCardBack(back);
                card.setCardCreated(created);

                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .cardDao()
                        .insert(card);

                return null;
            }
        }
        new FsrsCard(uuid, front, back, created);
        Log.d(TAG, "Successfully created card " + uuid + " locally");

        Create create = new Create();
        create.execute();
    }

     */
}
