package io.accelerate.challenge.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParamAccessor {
    private final JsonNode jsonNode;

    // Constructor to initialize with a JsonNode
    public ParamAccessor(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    // Get the value as a string
    public String getAsString() {
        return jsonNode.isTextual() ? jsonNode.asText() : null;
    }

    // Get the value as an integer
    public Integer getAsInteger() {
        return jsonNode.isInt() ? jsonNode.asInt() : null;
    }

    // Get the value as an ArrayOfIntegers (custom class)
    public ArrayOfIntegers getAsArrayOfIntegers() {
        ArrayOfIntegers arrayOfIntegers = new ArrayOfIntegers();
        if (jsonNode.isArray()) {
            for (JsonNode element : jsonNode) {
                if (element.isInt()) {
                    arrayOfIntegers.add(element.asInt());
                }
            }
        }
        return arrayOfIntegers;
    }

    public <T> T getAsObject(Class<T> itemClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode, itemClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize jsonNode to " + itemClass.getName(), e);
        }
    }
}
