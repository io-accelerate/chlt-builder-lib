package io.accelerate.challenge.checks.defined;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundPrimitiveTypeDefTest {

    @Test
    void shouldPassIfRoundWellDefined() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", PrimitiveTypes.STRING),
                        new ParamDefinition("desc2", PrimitiveTypes.STRING)), 
                new ReturnDefinition("desc3", PrimitiveTypes.STRING),
                List.of("a", "b"), "some_value");

        RoundChecks.assertRoundIsWellDefined(challengeRound);
    }

    @Test
    void shouldErrorIfReturnIsNull() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", PrimitiveTypes.STRING),
                        new ParamDefinition("desc2", PrimitiveTypes.STRING)),
                new ReturnDefinition("desc3", PrimitiveTypes.STRING),
                List.of("a", "b"), null);

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundIsWellDefined(challengeRound);
        });
    }

    @Test
    void shouldErrorIfParamsNotConsistentWithMethodDefinition() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", PrimitiveTypes.STRING),
                        new ParamDefinition("desc2", PrimitiveTypes.STRING)),
                new ReturnDefinition("desc3", PrimitiveTypes.STRING),
                List.of(1, 2), "some_value");

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundIsWellDefined(challengeRound);
        });
    }

    @Test
    void shouldErrorIfReturnTypeNotConsistentWithMethodDefinition() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", PrimitiveTypes.STRING),
                        new ParamDefinition("desc2", PrimitiveTypes.STRING)),
                new ReturnDefinition("desc3", PrimitiveTypes.STRING),
                List.of("a", "b"), 1);

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundIsWellDefined(challengeRound);
        });
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound getChallengeRound(List<ParamDefinition> parameterTypes, ReturnDefinition returnType, List<?> someArgs, Object someReturnValue) {
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