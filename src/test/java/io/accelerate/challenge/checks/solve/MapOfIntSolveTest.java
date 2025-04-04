package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.MapType;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MapOfIntSolveTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = mergeChallenge(
                List.of(Map.of("key1", 1), Map.of("key2", 2)),
                Map.of("key2", 2, "key1", 1));

        RoundChecks.assertRoundCanBeSolvedWith(getMergeReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = mergeChallenge(
                List.of(Map.of("key1", 1), Map.of("key2", 2)),
                Map.of("key1", 2));

        assertThrows(AssertionError.class, () -> RoundChecks.assertRoundCanBeSolvedWith(getMergeReferenceSolution(), challengeRound));
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound mergeChallenge(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("merge",
                List.of(new ParamDefinition("desc1", new MapType(PrimitiveType.INTEGER)),
                        new ParamDefinition("desc2", new MapType(PrimitiveType.INTEGER))), 
                new ReturnDefinition("desc3", new MapType(PrimitiveType.INTEGER)));
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
                            Map<String, Integer> integers = new HashMap<>();
                            integers.putAll(params[0].getAsMapOf(Integer.class));
                            integers.putAll(params[1].getAsMapOf(Integer.class));
                            return integers;
                        }
                );
                return implementationMap;
            }
        };
    }
}