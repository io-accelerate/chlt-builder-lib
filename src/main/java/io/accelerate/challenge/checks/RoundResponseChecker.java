package io.accelerate.challenge.checks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.accelerate.challenge.client.ResponseToServer;
import io.accelerate.challenge.definition.schema.RoundTest;
import io.accelerate.challenge.definition.schema.RoundTestAssertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RoundResponseChecker {

    private final ObjectMapper objectMapper;

    public RoundResponseChecker() {
        objectMapper = new ObjectMapper();
    }

    public List<FailedRoundTest> checkResponses(List<RoundTest> roundTests, List<ResponseToServer> receivedResponses) {
        List<FailedRoundTest> failedRoundTests = new ArrayList<>();

        for (RoundTest roundTest : roundTests) {
            String requestId = roundTest.id();
            boolean assertionPassed = false;
            
            //Get first response
            Optional<ResponseToServer> actualResponse = getFirstResponseForId(receivedResponses, requestId);
            Object actualResult = null;
            if (actualResponse.isPresent()) {
                actualResult = actualResponse.get().value();
            }

            //Compare as Json nodes
            RoundTestAssertion roundTestAssertion = roundTest.roundTestAssertion();
            JsonNode responseJsonNode = asJsonNode(actualResult);
            
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
                failedRoundTests.add(new FailedRoundTest(requestId, roundTestAssertion, actualResult));
            }
        }
        return failedRoundTests;
    }

    private static Optional<ResponseToServer> getFirstResponseForId(List<ResponseToServer> receivedResponses, String requestId) {
        return receivedResponses.stream().filter(response ->
                Objects.equals(response.requestId(), requestId)).findFirst();
    }

    private JsonNode asJsonNode(Object value) {
        return objectMapper.valueToTree(value);
    }
}
