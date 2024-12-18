package io.accelerate.challenge.checks;

import io.accelerate.challenge.client.ReferenceClient;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.client.Request;
import io.accelerate.challenge.client.Response;
import io.accelerate.challenge.definition.schema.*;

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
            List<Class<?>> expectedParameterTypes = methodDefinition.parameterTypes();

            //Actual
            List<?> params = methodCall.args();
            List<Class<?>> paramsTypes = new ArrayList<>();
            for (Object param : params) {
                paramsTypes.add(param.getClass());
            }

            if (!paramsTypes.equals(expectedParameterTypes)) {
                throw new AssertionError("Request " + id + " should have consistent param types");
            }
        });
    }

    private static void assertResponsesAreConsistentWithMethodDefinition(ChallengeRound challengeRound) {
        MethodDefinitions methods = challengeRound.getMethods();

        for (RoundTest roundTest : challengeRound.getTests()) {
            String methodName = roundTest.methodCall().methodName();

            //Expected
            MethodDefinition methodDefinition = methods.getByName(methodName);
            Class<?> expectedReturnType = methodDefinition.returnType();

            //Actual
            String id = roundTest.id();
            RoundTestAssertion roundTestAssertion = roundTest.roundTestAssertion();

            Object assertionValue = roundTestAssertion.value();

            // Check if the assertion value is an instance of the expected return type
            if (expectedReturnType.isInstance(assertionValue)) {
                expectedReturnType.cast(assertionValue);
            } else {
                throw new AssertionError("Response " + id + " should have consistent return type: Cannot cast " + assertionValue.getClass().getName() +
                        " to " + expectedReturnType.getName());
            }
        }
    }

    //~~~~ Solvable

    public static void assertRoundCanBeSolvedWith(ReferenceSolution referenceSolution, ChallengeRound challengeRound) {
        ReferenceClient referenceClient = new ReferenceClient();
        List<RoundTest> roundTests = challengeRound.getTests();
        MethodDefinitions methodDefinitions = challengeRound.getMethods();

        //Debt: We should provide an execution report
        boolean allTrialsPassed = true;
        List<String> failedLines = new ArrayList<>();

        String roundName = challengeRound.getClass().getSimpleName();
        System.out.printf("~~~~~~~ Read description %s ~~~~~~~%n", roundName);
        String description = challengeRound.getDescription();
        System.out.print(description);
        referenceSolution.participantReceivesRoundDescription(description);

        System.out.printf("~~~~~~~ Solve round  %s ~~~~~~~%n", challengeRound.getClass().getSimpleName());
        for (RoundTest roundTest : roundTests) {
            MethodCall methodCall = roundTest.methodCall();
            Request request = new Request(roundTest.id(), methodCall);
            Response response = referenceClient.respondToRequest(request, referenceSolution);
            Object result = response.result();

            RoundTestAssertion roundTestAssertion = roundTest.roundTestAssertion();
            String auditTrial = formatAuditLine(request, roundTestAssertion, response);

            boolean assertionPassed = false;
            switch (roundTestAssertion.type()) {
                case EQUALS -> {
                    Class<?> expectedReturnType = methodDefinitions.getByName(methodCall.methodName()).returnType();
                    Object castedValue = null;
                    if (expectedReturnType.isInstance(result)) {
                        castedValue = expectedReturnType.cast(result);
                    }
                    assertionPassed = roundTestAssertion.value().equals(castedValue);
                }
                case CONTAINS_STRING -> {
                    if (result instanceof String) {
                        assertionPassed = ((String) result).contains((String)roundTestAssertion.value());
                    }
                }
                case CONTAINS_STRING_IGNORING_CASE -> {
                    if (result instanceof String) {
                        String sourceResultToLower = ((String) result).toLowerCase();
                        String expectedContainsToLower = ((String) roundTestAssertion.value()).toLowerCase();
                        assertionPassed = sourceResultToLower.contains(expectedContainsToLower);
                    }
                }
            }

            if (!assertionPassed){
                allTrialsPassed = false;
                failedLines.add(auditTrial);
            }

            System.out.println(auditTrial);
        }

        if (!allTrialsPassed) {
            System.out.println("~~~~~~~ Failed trials ~~~~~~~");
            failedLines.forEach(System.out::println);
            throw new AssertionError("The implementation has failed one or more trials. Please check above.");
        }
    }

    private static String formatAuditLine(Request request, RoundTestAssertion roundTestAssertion, Response response) {
        MethodCall methodCall = request.methodCall();

        // Stringify params
        StringBuilder sb = new StringBuilder();
        for (Object param : methodCall.args()) {
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
                methodCall.methodName(), paramsString, roundTestAssertion.type().toPrintableName(), roundTestAssertion.value(), response.result());
    }
}
