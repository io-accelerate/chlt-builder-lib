package io.accelerate.challenge.builders;

import io.accelerate.challenge.definition.schema.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ReusableRoundTestBuilderTest {
    public static final MethodDefinition SOME_METHOD = new MethodDefinition("some_method", 
            List.of(String.class, String.class), String.class);
    public static final String SOME_ID = "someID";
    public static final String SOME_ARG = "someArg";
    public static final String SOME_OTHER_ARG = "someOtherArg";
    public static final String SOME_STRING_VALUE = "x";

    @Test
    void buildTest() {
        ReusableRoundTestBuilder equalityTest = new ReusableRoundTestBuilder(SOME_METHOD, () -> SOME_ID);

        RoundTest expected = new RoundTest(SOME_ID,
                new MethodCall(SOME_METHOD.name(), List.of(SOME_ARG, SOME_OTHER_ARG)),
                new RoundTestAssertion(RoundTestAssertionType.EQUALS, SOME_STRING_VALUE));

        RoundTest actual = equalityTest.call(SOME_ARG, SOME_OTHER_ARG).expect(RoundTestAssertionType.EQUALS, SOME_STRING_VALUE);
        
        assertThat(actual, equalTo(expected));
    }
}