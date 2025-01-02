package io.accelerate.challenge.checks.defined;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.ListType;
import io.accelerate.challenge.definition.schema.types.PrimitiveTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundListTypeDefTest {

    @Test
    void shouldPassIfRoundWellDefined() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", new ListType(PrimitiveTypes.INTEGER))), 
                new ReturnDefinition("desc2", new ListType(PrimitiveTypes.INTEGER)),
                List.of(List.of(1, 2, 3)), List.of(5, 6));

        RoundChecks.assertRoundIsWellDefined(challengeRound);
    }

    @Test
    void shouldErrorIfParamTypeDoesNotMatch() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", new ListType(PrimitiveTypes.INTEGER))),
                new ReturnDefinition("desc2", new ListType(PrimitiveTypes.INTEGER)),
                List.of(List.of("1", "2", "3")), List.of(5, 6));

        assertThrows(AssertionError.class, () -> {
            RoundChecks.assertRoundIsWellDefined(challengeRound);
        });
    }

    @Test
    void shouldErrorIfReturnTypeDoesNotMatch() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", new ListType(PrimitiveTypes.INTEGER))),
                new ReturnDefinition("desc2", new ListType(PrimitiveTypes.INTEGER)),
                List.of(List.of(1, 2, 3)), List.of("5", "6"));

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