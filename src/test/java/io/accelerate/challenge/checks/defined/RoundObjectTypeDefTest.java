package io.accelerate.challenge.checks.defined;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.ObjectType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundObjectTypeDefTest {

    record SomeItem(String id, int value) {}

    @Test
    void shouldPassIfRoundWellDefined() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", ObjectType.from(SomeItem.class))),
                new ReturnDefinition("desc2", ObjectType.from(SomeItem.class)),
                List.of(new SomeItem("x", 3)), new SomeItem("y", 9));

        RoundChecks.assertRoundIsWellDefined(challengeRound);
    }

    @Test
    void shouldErrorIfParamTypeDoesNotMatch() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", ObjectType.from(SomeItem.class))),
                new ReturnDefinition("desc2", ObjectType.from(SomeItem.class)),
                List.of(Map.of("x", 1)), new SomeItem("y", 9));

        assertThrows(AssertionError.class, () -> RoundChecks.assertRoundIsWellDefined(challengeRound));
    }

    @Test
    void shouldErrorIfReturnTypeDoesNotMatch() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", ObjectType.from(SomeItem.class))),
                new ReturnDefinition("desc2", ObjectType.from(SomeItem.class)),
                List.of(new SomeItem("x", 3)), Map.of("x", 1));

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