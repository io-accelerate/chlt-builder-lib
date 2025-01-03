package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

class NullSolveTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = fetchWithNullChallenge(List.of("good"), false);

        RoundChecks.assertRoundCanBeSolvedWith(getPingReferenceSolution(), challengeRound);
    }

    @Test
    void shouldPassIfRoundCanBeSolvedWithNull() {
        ChallengeRound challengeRound = fetchWithNullChallenge(List.of("bad"), true);

        RoundChecks.assertRoundCanBeSolvedWith(getPingReferenceSolution(), challengeRound);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound fetchWithNullChallenge(List<?> args, boolean isNull) {
        MethodDefinition methodDefinition = new MethodDefinition("check",
                List.of(), new ReturnDefinition("desc1", PrimitiveType.STRING));
        return new ChallengeRound("PNG", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("PNG_R1_01",
                        new MethodCall("check", args),
                        new RoundTestAssertion(RoundTestAssertionType.IS_NULL, isNull))
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