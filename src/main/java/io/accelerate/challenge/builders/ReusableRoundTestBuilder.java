package io.accelerate.challenge.builders;

import io.accelerate.challenge.definition.schema.*;

import java.util.Arrays;

/**
 * Created by julianghionoiu on 29/07/2015.
 *
 * Warning ! This class is mutable and is meant to be reused.
 */
public class ReusableRoundTestBuilder {
    private final MethodDefinition methodDefinition;
    private final IdGenerator idGenerator;
    private Object[] params;

    public ReusableRoundTestBuilder(MethodDefinition methodDefinition, IdGenerator idGenerator) {
        this.methodDefinition = methodDefinition;
        this.idGenerator = idGenerator;
    }

    //Obs: We should put some checks in place to enforce the proper usage
    public ReusableRoundTestBuilder call(Object... params) {
        this.params = params;
        return this;
    }

    public RoundTest eq(Object expectedResult) {
        String id = idGenerator.next();

        MethodCall methodCall = new MethodCall(methodDefinition.name(), Arrays.asList(params));
        return new RoundTest(id, methodCall, new RoundTestAssertion(RoundTestAssertionType.EQUALS, expectedResult));
    }
}
