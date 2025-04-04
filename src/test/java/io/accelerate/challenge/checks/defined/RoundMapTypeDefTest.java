package io.accelerate.challenge.checks.defined;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.MapType;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundMapTypeDefTest {

    @Test
    void shouldPassIfRoundWellDefined() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", new MapType(PrimitiveType.INTEGER))), 
                new ReturnDefinition("desc2", new MapType(PrimitiveType.INTEGER)),
                List.of(Map.of("key1", 1, "key2", 2)), Map.of("key1", 1));

        RoundChecks.assertRoundIsWellDefined(challengeRound);
    }

    @Test
    void shouldErrorIfParamTypeDoesNotMatch() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", new MapType(PrimitiveType.INTEGER))),
                new ReturnDefinition("desc2", new MapType(PrimitiveType.INTEGER)),
                List.of(Map.of("key1", "text")), Map.of("key1", 1));

        assertThrows(AssertionError.class, () -> RoundChecks.assertRoundIsWellDefined(challengeRound));
    }

    @Test
    void shouldErrorIfReturnTypeDoesNotMatch() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", new MapType(PrimitiveType.INTEGER))),
                new ReturnDefinition("desc2", new MapType(PrimitiveType.INTEGER)),
                List.of(Map.of("key1", 1, "key2", 2)), Map.of("key1", "text"));

        assertThrows(AssertionError.class, () -> RoundChecks.assertRoundIsWellDefined(challengeRound));
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