package io.accelerate.challenge.checks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.accelerate.challenge.checker.round.DefaultRoundChecker;
import io.accelerate.challenge.checker.round.FailedRoundTest;
import io.accelerate.challenge.checker.round.RoundChecker;
import io.accelerate.challenge.checker.round.RoundResponseToCheck;
import io.accelerate.challenge.client.ReferenceClient;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.client.RequestFromServer;
import io.accelerate.challenge.client.ResponseToServer;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

public final class RoundChecks {

    //~~~~ Well defined

    public static void assertRoundIsWellDefined(ChallengeRound challengeRound) {
        assertRequestsAreConsistentWithMethodDefinition(challengeRound);
        assertResponsesAreConsistentWithMethodDefinition(challengeRound);
    }

    private static void assertRequestsAreConsistentWithMethodDefinition(ChallengeRound challengeRound) {
        MethodDefinitions methods = challengeRound.getMethods();

        challengeRound.getTests().forEach(roundTest -> {
            String id = roundTest.id();
            MethodCall methodCall = roundTest.methodCall();

            //Expected
            MethodDefinition methodDefinition = methods.getByName(methodCall.methodName());
            List<ParamDefinition> paramDefinitions = methodDefinition.parameterDefinitions();

            //Actual
            List<?> params = methodCall.args();
            
            //Check equal num of params
            if (params.size() != paramDefinitions.size()) {
                throw new AssertionError("Request " + id + " does not have the same number of parameters as the method definition");
            }

            for (int i = 0; i < paramDefinitions.size(); i++) {
                TypeDefinition typeDefinition = paramDefinitions.get(i).typeDefinition();
                Object paramValue = params.get(i);
                JsonNode paramJsonNode = asJsonNode(paramValue);

                if (!typeDefinition.isCompatible(paramJsonNode)) {
                    throw new AssertionError("Request " + id + " should have consistent param types: "+"Cannot cast " + paramValue +
                            " to " + typeDefinition.getDisplayName());
                }
            }
        });
    }

    private static void assertResponsesAreConsistentWithMethodDefinition(ChallengeRound challengeRound) {
        MethodDefinitions methods = challengeRound.getMethods();

        for (RoundTest roundTest : challengeRound.getTests()) {
            String methodName = roundTest.methodCall().methodName();

            //Expected
            MethodDefinition methodDefinition = methods.getByName(methodName);
            ReturnDefinition returnDefinition = methodDefinition.returnDefinition();

            //Actual
            String id = roundTest.id();
            RoundTestAssertion roundTestAssertion = roundTest.roundTestAssertion();
            TypeConstraint typeConstraints = roundTestAssertion.type().getTypeConstraint();
            switch (typeConstraints) {
                case MATCHING_RETURN_TYPE -> {
                    if (!returnDefinition.typeDefinition().isCompatible(asJsonNode(roundTestAssertion.value()))) {
                        throw new AssertionError("Response " + id + " should have consistent return type: "+"Cannot cast " + roundTestAssertion.value() +
                                " to " + returnDefinition.typeDefinition().getDisplayName());
                    }
                }
                case STRING -> {
                    if (returnDefinition.typeDefinition() != PrimitiveType.STRING) {
                        throw new AssertionError("Response " + id + " needs to be a string");
                    }
                }
                case ANY -> {
                    return;
                }
            }
        }
    }

    //~~~~ Solvable

    public static void assertRoundCanBeSolvedWith(ReferenceSolution referenceSolution, ChallengeRound challengeRound) {
        ReferenceClient referenceClient = new ReferenceClient();
        List<RoundTest> roundTests = challengeRound.getTests();

        String roundName = challengeRound.getClass().getSimpleName();
        System.out.printf("~~~~~~~ Read description %s ~~~~~~~%n", roundName);
        String description = challengeRound.getDescription();
        System.out.println(description);
        System.out.println();
        System.out.println(challengeRound.getMethods().getDisplayDescription());
        referenceSolution.participantReceivesRoundDescription(description);

        System.out.printf("~~~~~~~ Solve round  %s ~~~~~~~%n", challengeRound.getClass().getSimpleName());

        List<ResponseToServer> receivedResponses = new ArrayList<>();
        for (RoundTest roundTest : roundTests) {
            MethodCall methodCall = roundTest.methodCall();
            RequestFromServer request = new RequestFromServer(roundTest.id(), methodCall.methodName(), methodCall.args());
            RoundTestAssertion roundTestAssertion = roundTest.roundTestAssertion();
            ResponseToServer response = referenceClient.respondToRequest(request, referenceSolution);
            receivedResponses.add(response);
            String auditTrial = formatAuditLine(request, roundTestAssertion, response);
            System.out.println(auditTrial);
        }

        RoundChecker roundResponseChecker = new DefaultRoundChecker();
        List<RoundResponseToCheck> responsesToCheck = receivedResponses.stream().map(response -> new RoundResponseToCheck(response.requestId(), response.value())).toList();
        List<FailedRoundTest> failedRoundTests = roundResponseChecker.checkResponses(roundTests, responsesToCheck);
        if (failedRoundTests.isEmpty()) {
            System.out.println("~~~~~~~ Round passed ~~~~~~~");
        } else {
            System.out.println("~~~~~~~ Failed trials ~~~~~~~");
            failedRoundTests.forEach(System.out::println);
            throw new AssertionError("The implementation has failed one or more trials. Please check above.");
        }
    }

    // ~~~~~~ Utility methods ~~~~~~

    private static JsonNode asJsonNode(Object value) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(value);
    }

    private static String formatAuditLine(RequestFromServer request, RoundTestAssertion roundTestAssertion, ResponseToServer response) {
        // Stringify params
        StringBuilder sb = new StringBuilder();
        for (Object param : request.args()) {
            String paramRepresentation = param.toString().replaceAll("\n","\n\\\\ ");
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            if (param instanceof String) {
                sb.append("\"").append(paramRepresentation).append("\"");
            } else {
                sb.append(paramRepresentation);
            }
        }
        String paramsString = sb.toString();

        return String.format("%s(%s), expected: %s %s, got: %s",
                request.methodName(), paramsString, roundTestAssertion.type().toDisplayName(), roundTestAssertion.value(), response.value());
    }
}
