package com.example.shine;

import de.nickhansen.spacedrepetition.api.algorithm.FSRSAlgorithm;
import de.nickhansen.spacedrepetition.api.algorithm.fsrs.FSRSRating;
import de.nickhansen.spacedrepetition.api.algorithm.fsrs.FSRSState;
import de.nickhansen.spacedrepetition.api.algorithm.result.FSRSAlgorithmResult;

public class SpacedRepetitionAPI {
    private static SpacedRepetitionAPI instance;

    protected SpacedRepetitionAPI() {
        instance = this;
    }

    public FSRSAlgorithmResult basicFSRS(FSRSRating rating, float stability, float difficulty, int elapsedDays, int scheduledDays, int repetitions, FSRSState state, long lastReview) {
        FSRSAlgorithm fsrs = FSRSAlgorithm.builder()
                .rating(rating)
                .stability(stability)
                .difficulty(difficulty)
                .elapsedDays(elapsedDays)
                .scheduledDays(scheduledDays)
                .repetitions(repetitions)
                .state(state)
                .lastReview(lastReview)
                .build();

        return fsrs.calc();
    }

    public static SpacedRepetitionAPI getInstance() {
        return instance;
    }
}
