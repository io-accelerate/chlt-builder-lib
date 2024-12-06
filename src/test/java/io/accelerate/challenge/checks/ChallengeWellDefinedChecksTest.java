package io.accelerate.challenge.checks;

import io.accelerate.challenge.definition.schema.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ChallengeWellDefinedChecksTest {

    @Test
    void shouldPassIfChallengeWellDefined() {
        ChallengeChecks.assertChallengeIsWellDefined(
                challengeWithTestIds(List.of("CNC_R1_01", "CNC_R1_02", "CNC_R1_03"))
        );
    }

    @Test
    void shouldErrorIfDuplicateIds() {
        assertThrows(AssertionError.class, () -> {
            ChallengeChecks.assertChallengeIsWellDefined(
                    challengeWithTestIds(List.of("CNC_R1_01", "CNC_R1_02", "CNC_R1_01"))
            );
        });
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static Challenge challengeWithTestIds(List<String> testIds) {

        List<RoundTest> tests = testIds.stream().map(ChallengeWellDefinedChecksTest::someRoundTest).toList();

        MethodDefinition methodDefinition = new MethodDefinition(
                "CNC",
                List.of(String.class, String.class),
                String.class);
        ChallengeRound round = new ChallengeRound("R", "desc",
                MethodDefinitions.of(methodDefinition),
                tests);
        return new Challenge("CNC", 1, "Test Challenge",
                List.of(round)
        );
    }

    private static RoundTest someRoundTest(String roundTestId) {
        return new RoundTest(roundTestId,
                new MethodCall("concat", List.of()),
                new RoundTestAssertion(RoundTestAssertionType.EQUALS, "X"));
    }
}