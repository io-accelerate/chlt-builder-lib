package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ArrayOfIntegers;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.ListType;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ArrayOfIntSolveTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = mergeChallenge(
                List.of(ArrayOfIntegers.of(1, 2), ArrayOfIntegers.of(3, 4)),
                ArrayOfIntegers.of(1, 2, 3, 4));

        RoundChecks.assertRoundCanBeSolvedWith(getMergeReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = mergeChallenge(
                List.of(ArrayOfIntegers.of(1, 2), ArrayOfIntegers.of(3, 4)),
                ArrayOfIntegers.of(1, 2));

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundCanBeSolvedWith(getMergeReferenceSolution(), challengeRound);
        });
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound mergeChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("merge",
                List.of(new ParamDefinition("desc1", new ListType(PrimitiveType.INTEGER)),
                        new ParamDefinition("desc2", new ListType(PrimitiveType.INTEGER))), 
                new ReturnDefinition("desc3", new ListType(PrimitiveType.INTEGER)));
        return new ChallengeRound("MRG", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("MRG_R1_01",
                        new MethodCall("merge", args),
                        new RoundTestAssertion(RoundTestAssertionType.EQUALS, expectedValue))
                )
        );
    }

    private static ReferenceSolution getMergeReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("merge", params -> {
                            ArrayOfIntegers integers = new ArrayOfIntegers();
                            integers.addAll(params[0].getAsArrayOfIntegers());
                            integers.addAll(params[1].getAsArrayOfIntegers());
                            return integers;
                        }
                );
                return implementationMap;
            }
        };
    }
}