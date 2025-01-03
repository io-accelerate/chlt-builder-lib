package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NoMethodSolveTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = pingChallenge(List.of(), "pong");

        RoundChecks.assertRoundCanBeSolvedWith(getPingReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = pingChallenge(List.of(), "wrong");

        assertThrows(AssertionError.class, () -> RoundChecks.assertRoundCanBeSolvedWith(getPingReferenceSolution(), challengeRound));
    }

    @Test
    void shouldErrorSolutionDoesNotMapMethod() {
        ChallengeRound challengeRound = pingChallenge(List.of(), "pong");

        assertThrows(NoSuchMethodError.class, () -> RoundChecks.assertRoundCanBeSolvedWith(getNullReferenceSolution(), challengeRound));
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound pingChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("ping",
                List.of(), new ReturnDefinition("desc1", PrimitiveType.STRING));
        return new ChallengeRound("PNG", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("PNG_R1_01",
                        new MethodCall("ping", args),
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
                implementationMap.register("ping", params ->
                        "pong");
                return implementationMap;
            }
        };
    }

    private static ReferenceSolution getNullReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                return new ImplementationMap();
            }
        };
    }
}