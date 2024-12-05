package io.accelerate.challenge.checks;


import io.accelerate.challenge.definition.schema.Challenge;
import io.accelerate.challenge.definition.schema.RoundTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by julianghionoiu on 02/05/2015.
 */
public final class ChallengeChecks {

    //~~~~ Well defined

    public static void assertChallengeIsWellDefined(Challenge challenge) {
        assertIdsAreUnique(challenge);
    }

    private static void assertIdsAreUnique(Challenge challenge) {
        List<String> ids = new ArrayList<>();

        challenge.getRounds().stream()
                .flatMap(challengeRound ->
                        challengeRound.getTests().stream().map(RoundTest::id)
                )
                .forEach(ids::add);

        Set<String> duplicates = findDuplicates(ids);
        if (!duplicates.isEmpty()) {
            throw new AssertionError("Duplicate ids found: " + duplicates);
        }
    }

    //~~~ helpers

    private static <T> Set<T> findDuplicates(List<T> listContainingDuplicates) {
        final Set<T> allValuesAsSet = new HashSet<>();

        return listContainingDuplicates.stream()
                .filter(yourInt -> !allValuesAsSet.add(yourInt))
                .collect(Collectors.toSet());
    }

}
