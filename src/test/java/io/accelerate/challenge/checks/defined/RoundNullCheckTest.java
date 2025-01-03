package io.accelerate.challenge.checks.defined;

import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

class RoundNullCheckTest {

    @SuppressWarnings("unused")
    record SomeItem(String id, int value) {}

    @Test
    void shouldPassIfRoundWellDefined() {
        ChallengeRound challengeRound = getChallengeRound(
                List.of(new ParamDefinition("desc1", PrimitiveType.STRING)),
                new ReturnDefinition("desc3", PrimitiveType.STRING),
                List.of("a"));

        RoundChecks.assertRoundIsWellDefined(challengeRound);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static ChallengeRound getChallengeRound(List<ParamDefinition> parameterTypes, ReturnDefinition returnType, List<?> someArgs) {
        MethodDefinition methodDefinition = new MethodDefinition("concat",
                parameterTypes, returnType);
        return new ChallengeRound("R", "desc",
                MethodDefinitions.of(methodDefinition),
                List.of(new RoundTest("CNC_R1_01",
                        new MethodCall("concat", someArgs),
                        new RoundTestAssertion(RoundTestAssertionType.IS_NULL, true))
                )
        );
    }
}