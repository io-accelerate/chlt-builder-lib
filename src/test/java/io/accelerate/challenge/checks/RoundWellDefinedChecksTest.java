package io.accelerate.challenge.checks;

import io.accelerate.challenge.definition.schema.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundWellDefinedChecksTest {

    @Test
    void shouldPassIfRoundWellDefined() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(String.class, String.class), String.class,
                List.of("a", "b"), "some_value");

        RoundChecks.assertRoundIsWellDefined(challengeRound);
    }

    @Test
    void shouldErrorIfParamsNotConsistentWithMethodDefinition() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(String.class, String.class), String.class,
                List.of(1, 2), "some_value");

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundIsWellDefined(challengeRound);
        });
    }

    @Test
    void shouldErrorIfReturnTypeNotConsistentWithMethodDefinition() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(String.class, String.class), String.class,
                List.of("a", "b"), 1);

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundIsWellDefined(challengeRound);
        });
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound getChallengeRound(List<Class<?>> parameterTypes, Class<String> returnType, List<?> someArgs, Object someReturnValue) {
        MethodDefinition methodDefinition = new MethodDefinition("concat",
                parameterTypes, returnType);
        return new ChallengeRound("R", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("CNC_R1_01",
                        new MethodCall("concat", someArgs),
                        new RoundTestAssertion(RoundTestAssertionType.EQUALS, someReturnValue))
                )
        );
    }
}