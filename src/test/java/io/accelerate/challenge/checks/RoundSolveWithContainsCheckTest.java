package io.accelerate.challenge.checks;

import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundSolveWithContainsCheckTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = letterToSantaChallenge(List.of(""), "Santa");

        RoundChecks.assertRoundCanBeSolvedWith(dearSantaReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = letterToSantaChallenge(List.of(""), "John");

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundCanBeSolvedWith(dearSantaReferenceSolution(), challengeRound);
        });
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound letterToSantaChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("letter_to_santa",
                List.of(), String.class);
        return new ChallengeRound("SNT", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("SNT_R1_01",
                        new MethodCall("letter_to_santa", args),
                        new RoundTestAssertion(RoundTestAssertionType.CONTAINS_STRING, expectedValue))
                )
        );
    }

    private static ReferenceSolution dearSantaReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("letter_to_santa", params ->
                        "From X\n Dear Santa,\n bla bla bla");
                return implementationMap;
            }
        };
    }
}