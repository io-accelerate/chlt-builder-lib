package io.accelerate.challenge.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by julianghionoiu on 02/08/2015.
 */
public class ReferenceClient {

    public Response respondToRequest(Request request, ReferenceSolution referenceSolution) {
        String requestId = request.requestId();
        String methodName = request.methodCall().methodName();
        ParamAccessor[] serializedParams = serializeAndDeserializeArgs(request);

        ImplementationMap implementations = referenceSolution.participantUpdatesImplementationMap();
        UserImplementation userImplementation = implementations.getImplementationFor(methodName);
        Object actualReturnedValue = userImplementation.process(serializedParams);

        return new Response(requestId, actualReturnedValue);
    }

    //~~ Process
    private ParamAccessor[] serializeAndDeserializeArgs(Request request) {
        try {
            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the original parameters to JSON
            Object[] originalArgs = request.methodCall().args().toArray();
            String serializedArgs = objectMapper.writeValueAsString(originalArgs);

            // Deserialize the JSON string back into a list of JsonNode
            List<JsonNode> elements = objectMapper.readValue(serializedArgs, new TypeReference<List<JsonNode>>() {});

            // Convert JsonNode elements to ParamAccessor instances
            ParamAccessor[] paramAccessors = new ParamAccessor[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                paramAccessors[i] = new ParamAccessor(elements.get(i));
            }

            return paramAccessors;
        } catch (Exception e) {
            throw new RuntimeException("Error during serialization and deserialization of params", e);
        }
    }
}
