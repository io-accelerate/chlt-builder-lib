package io.accelerate.challenge;

import io.accelerate.challenge.builders.ReusableRoundTestBuilder;
import io.accelerate.challenge.builders.SequentialIdGenerator;
import io.accelerate.challenge.checks.ChallengeChecks;
import io.accelerate.challenge.checks.RoundChecks;
import io.accelerate.challenge.client.ImplementationMap;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.definition.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BuildAndCheckChallengeE2ETest {

    private ChallengeRound challengeRound1;
    private ChallengeRound challengeRound2;
    private Challenge challenge;
    private ReferenceSolution referenceSolution;

    @BeforeEach
    void setUp() {
        MethodDefinition someTestMethod =
                new MethodDefinition("concat", List.of(String.class, String.class), String.class);

        challengeRound1 = round1(someTestMethod, "CNC_R1");

        challengeRound2 = round2(someTestMethod, "CNC_R2");

        challenge = new Challenge("CNC", 1, "Test Challenge",
                List.of(challengeRound1, challengeRound2));

        referenceSolution = new ReferenceSolution() {
            @Override
            public void participantReceivesRoundDescription(String description) {
                //no-op
            }

            @Override
            public ImplementationMap participantUpdatesImplementationMap() {
                ImplementationMap implementationMap = new ImplementationMap();
                implementationMap.register(someTestMethod.name(), params ->
                        params[0].getAsString() + params[1].getAsString());
                return implementationMap;
            }
        };
    }

    private static ChallengeRound round1(MethodDefinition someTestMethod, String roundId) {
        SequentialIdGenerator idGenerator = new SequentialIdGenerator(roundId);
        ReusableRoundTestBuilder reusableRoundTestBuilder =
                new ReusableRoundTestBuilder(someTestMethod, idGenerator);

        return new ChallengeRound(
                roundId,
                "Concat Round1",
                MethodDefinitions.of(someTestMethod),
                List.of(reusableRoundTestBuilder.call("a", "b").eq("ab")));
    }

    private static ChallengeRound round2(MethodDefinition someTestMethod, String roundId) {
        SequentialIdGenerator idGenerator = new SequentialIdGenerator(roundId);
        ReusableRoundTestBuilder reusableRoundTestBuilder =
                new ReusableRoundTestBuilder(someTestMethod, idGenerator);

        return new ChallengeRound(
                roundId,
                "Concat Round2",
                MethodDefinitions.of(someTestMethod),
                List.of(reusableRoundTestBuilder.call("abc", "123").eq("abc123")));
    }


    @Test
    void challengeIsWellDefined() {
        ChallengeChecks.assertChallengeIsWellDefined(challenge);
    }

    @Test
    void roundOneIsWellDefined() {
        RoundChecks.assertRoundIsWellDefined(challengeRound1);
    }

    @Test
    void roundOneCanBeSolved() {
        RoundChecks.assertRoundCanBeSolvedWith(referenceSolution, challengeRound1);
    }

    @Test
    void roundTwoIsWellDefined() {
        RoundChecks.assertRoundIsWellDefined(challengeRound2);
    }

    @Test
    void roundTwoCanBeSolved() {
        RoundChecks.assertRoundCanBeSolvedWith(referenceSolution, challengeRound2);
    }
}
