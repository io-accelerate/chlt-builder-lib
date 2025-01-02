package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.ObjectType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectSolveTest {

    record Item(int id, String name) { }
    
    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = objectStoreAndRetrieve(
                List.of(new Item(1, "name")), new Item(1, "name"));

        RoundChecks.assertRoundCanBeSolvedWith(getReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = objectStoreAndRetrieve(
                List.of(new Item(1, "name")), new Item(999, "wrong"));
        
        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundCanBeSolvedWith(getReferenceSolution(), challengeRound);
        });
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound objectStoreAndRetrieve(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("fetch",
                List.of(new ParamDefinition("desc1", ObjectType.from(Item.class))), 
                new ReturnDefinition("desc2", ObjectType.from(Item.class)));
        return new ChallengeRound("FETCH", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("FTC_R1_01",
                        new MethodCall("fetch", args),
                        new RoundTestAssertion(RoundTestAssertionType.EQUALS, expectedValue))
                )
        );
    }

    private static ReferenceSolution getReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("fetch", params -> 
                        params[0].getAsObject(Item.class)
                );
                return implementationMap;
            }
        };
    }
}