package io.accelerate.challenge.checks;

import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class RoundSolveNullCheckTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = fetchWithNullChallenge(List.of("good"), "good");

        RoundChecks.assertRoundCanBeSolvedWith(getPingReferenceSolution(), challengeRound);
    }

    @Test
    void shouldPassIfRoundCanBeSolvedWithNull() {
        ChallengeRound challengeRound = fetchWithNullChallenge(List.of("bad"), null);

        RoundChecks.assertRoundCanBeSolvedWith(getPingReferenceSolution(), challengeRound);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound fetchWithNullChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("check",
                List.of(), String.class);
        return new ChallengeRound("PNG", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("PNG_R1_01",
                        new MethodCall("check", args),
                        new RoundTestAssertion(RoundTestAssertionType.EQUALS, expectedValue))
                )
        );
    }

    private static ReferenceSolution getPingReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("check", params ->
                {
                    if (params[0].getAsString().equals("good")) return "good";
                    else return null;
                });
                return implementationMap;
            }
        };
    }
}