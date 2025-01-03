package io.accelerate.challenge.checks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.accelerate.challenge.client.ReferenceClient;
import io.accelerate.challenge.client.ReferenceSolution;
import io.accelerate.challenge.client.Request;
import io.accelerate.challenge.client.Response;
import io.accelerate.challenge.definition.schema.*;
import io.accelerate.challenge.definition.schema.types.PrimitiveType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        //Debt: We should provide an execution report
        boolean allTrialsPassed = true;
        List<String> failedLines = new ArrayList<>();

        String roundName = challengeRound.getClass().getSimpleName();
        System.out.printf("~~~~~~~ Read description %s ~~~~~~~%n", roundName);
        String description = challengeRound.getDescription();
        System.out.println(description);
        System.out.println(renderMethodsDocumentation(challengeRound.getMethods()));
        referenceSolution.participantReceivesRoundDescription(description);

        System.out.printf("~~~~~~~ Solve round  %s ~~~~~~~%n", challengeRound.getClass().getSimpleName());
        for (RoundTest roundTest : roundTests) {
            MethodCall methodCall = roundTest.methodCall();
            Request request = new Request(roundTest.id(), methodCall);
            Response response = referenceClient.respondToRequest(request, referenceSolution);

            RoundTestAssertion roundTestAssertion = roundTest.roundTestAssertion();
            String auditTrial = formatAuditLine(request, roundTestAssertion, response);

            boolean assertionPassed = false;
            // Compare as jsonNodes
            JsonNode responseJsonNode = asJsonNode(response.value());
            switch (roundTestAssertion.type()) {
                case EQUALS -> assertionPassed = Objects.equals(asJsonNode(roundTestAssertion.value()), responseJsonNode);
                case CONTAINS_STRING -> assertionPassed = responseJsonNode.asText().contains((String)roundTestAssertion.value());
                case CONTAINS_STRING_IGNORING_CASE -> {
                    String expectedContainsToLower = ((String) roundTestAssertion.value()).toLowerCase();
                    assertionPassed = responseJsonNode.asText().toLowerCase().contains(expectedContainsToLower);
                }
                case IS_NULL -> assertionPassed = responseJsonNode.isNull() == ((Boolean) roundTestAssertion.value());
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
    
    // ~~~~~~ Utility methods ~~~~~~

    private static String renderMethodsDocumentation(MethodDefinitions methodDefinitions) {
        StringBuilder sb = new StringBuilder();
        String maybePluralSuffix = methodDefinitions.size() > 1 ? "s" : "";
        sb.append("In order to complete the round you need to implement the following method").append(maybePluralSuffix).append(":").append("\n\n");
        String methods = methodDefinitions.stream().map(RoundChecks::renderMethodDocumentation).collect(Collectors.joining("\n\n"));
        sb.append(methods);
        return sb.toString();
    }

    //OBS: The meaning of the various parameters could be part of the method definition
    public static String renderMethodDocumentation(MethodDefinition method) {
        StringBuilder sb = new StringBuilder();
        sb.append(renderMethodDefinition(method)).append("\n");
        for (int i = 0; i < method.parameterDefinitions().size(); i++) {
            ParamDefinition paramDefinition = method.parameterDefinitions().get(i);
            sb.append(" - param[").append(i).append("] = ").append(paramDefinition.description()).append("\n");
        }
        sb.append(" - @return = ").append(method.returnDefinition().description());
        return sb.toString();
    }

    private static String renderMethodDefinition(MethodDefinition method) {
        String parameters = method.parameterDefinitions().stream()
                .map(ParamDefinition::typeDefinition)
                .map(TypeDefinition::getDisplayName)
                .collect(Collectors.joining(", "));
        return method.name() + "(" + parameters + ") -> " + method.returnDefinition().typeDefinition().getDisplayName();
    }

    private static JsonNode asJsonNode(Object value) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(value);
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
                methodCall.methodName(), paramsString, roundTestAssertion.type().toDisplayName(), roundTestAssertion.value(), response.value());
    }
}
