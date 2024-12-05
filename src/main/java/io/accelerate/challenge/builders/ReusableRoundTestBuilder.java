package io.accelerate.challenge.builders;

import com.google.common.collect.ImmutableList;
import io.accelerate.challenge.definition.schema.*;

/**
 * Created by julianghionoiu on 29/07/2015.
 *
 * Warning ! This class is mutable and is meant to be reused.
 */
// TODO: Move to builder toolkit and publish to Maven Central
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

        MethodCall methodCall = new MethodCall(methodDefinition.name(), ImmutableList.copyOf(params));
        return new RoundTest(id, methodCall, new RoundTestAssertion(RoundTestAssertionType.EQUALS, expectedResult));
    }
}
