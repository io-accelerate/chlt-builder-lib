package io.accelerate.challenge.client;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by julianghionoiu on 07/03/2015.
 */
public record Response(String requestId, Object value) {
}
