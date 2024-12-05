package io.accelerate.challenge.client;

/**
 * Created by julianghionoiu on 17/10/2015.
 */
@FunctionalInterface
public interface UserImplementation {
    Object process(ParamAccessor[] params);
}
