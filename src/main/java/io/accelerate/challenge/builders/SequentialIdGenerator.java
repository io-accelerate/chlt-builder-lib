package io.accelerate.challenge.builders;

/**
 * Created by julianghionoiu on 29/07/2015.
 */
public class SequentialIdGenerator implements IdGenerator {
    private static final int MAXIMUM_INDEX = 999;
    private final String roundId;
    private int index;

    public SequentialIdGenerator(String roundId) {
        this.roundId = roundId;
        this.index = 1;
    }

    @Override
    public String next() {
        int currentIndex = index;
        index ++;

        if (currentIndex > MAXIMUM_INDEX) {
            throw new IllegalArgumentException("You have exceeded the maximum number of IDs for the current generator");
        }

        return String.format("%s_%03d", roundId, currentIndex);
    }
}
