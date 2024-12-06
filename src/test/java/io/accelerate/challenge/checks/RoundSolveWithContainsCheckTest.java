package io.accelerate.challenge.checks;

import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundSolveWithContainsCheckTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = helloChallenge(List.of("John"), "John");

        RoundChecks.assertRoundCanBeSolvedWith(helloReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = helloChallenge(List.of("John"), "Wrong");

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundCanBeSolvedWith(helloReferenceSolution(), challengeRound);
        });
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound helloChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("hello",
                List.of(String.class), String.class);
        return new ChallengeRound("HLO", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("HLO_R1_01",
                        new MethodCall("hello", args),
                        new RoundTestAssertion(RoundTestAssertionType.CONTAINS_STRING, expectedValue))
                )
        );
    }

    private static ReferenceSolution helloReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("hello", params ->
                        "Hello, " + params[0].getAsString());
                return implementationMap;
            }
        };
    }
}