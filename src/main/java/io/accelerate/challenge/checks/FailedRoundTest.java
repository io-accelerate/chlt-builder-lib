package io.accelerate.challenge.checks;

import io.accelerate.challenge.definition.schema.RoundTestAssertion;

public record FailedRoundTest(String requestId, RoundTestAssertion failedAssertion, Object actualResult) {}
