package io.accelerate.challenge.client;

import io.accelerate.challenge.definition.schema.MethodCall;

/**
 * Created by julianghionoiu on 07/03/2015.
 */
public record Request(String requestId, MethodCall methodCall) {
}
