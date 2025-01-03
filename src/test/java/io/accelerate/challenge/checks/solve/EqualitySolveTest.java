package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EqualitySolveTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = sumChallenge(List.of(1, 2), 3);

        RoundChecks.assertRoundCanBeSolvedWith(getSumReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = sumChallenge(List.of(1, 2), 99);

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundCanBeSolvedWith(getSumReferenceSolution(), challengeRound);
        });
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound sumChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("sum",
                List.of(new ParamDefinition("desc1", PrimitiveType.INTEGER), 
                        new ParamDefinition("desc2", PrimitiveType.INTEGER)), 
                new ReturnDefinition("desc3", PrimitiveType.INTEGER));
        return new ChallengeRound("SUM", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("SUM_R1_01",
                        new MethodCall("sum", args),
                        new RoundTestAssertion(RoundTestAssertionType.EQUALS, expectedValue))
                )
        );
    }

    private static ReferenceSolution getSumReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("sum", params ->
                        params[0].getAsInteger() + params[1].getAsInteger());
                return implementationMap;
            }
        };
    }
}