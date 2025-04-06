package io.accelerate.challenge.checks.solve;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MultilineSolveTest {

    @Test
    void shouldPassIfRoundCanBeSolved() {
        ChallengeRound challengeRound = echo(List.of("""
                line1 
                
                line2
                
                
                """), "line1\nline2");

        RoundChecks.assertRoundCanBeSolvedWith(echoReferenceSolution(), challengeRound);
    }

    @Test
    void shouldErrorRoundDoesNotSolveCorrectly() {
        ChallengeRound challengeRound = echo(List.of("""
                line1 
                
                    line2
                
                
                """), "line1\nline2");

        assertThrows(AssertionError.class, () -> RoundChecks.assertRoundCanBeSolvedWith(echoReferenceSolution(), challengeRound));
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound echo(List<?> args, Object expectedValue) {
        MethodDefinition methodDefinition = new MethodDefinition("echo",
                List.of(), new ReturnDefinition("desc3", PrimitiveType.STRING));
        return new ChallengeRound("SNT", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("SNT_R1_01",
                        new MethodCall("echo", args),
                        new RoundTestAssertion(RoundTestAssertionType.MULTILINE_STRING_EQUALS, expectedValue))
                )
        );
    }

    private static ReferenceSolution echoReferenceSolution() {
        return new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register("echo", params -> params[0].getAsString());
                return implementationMap;
            }
        };
    }
}