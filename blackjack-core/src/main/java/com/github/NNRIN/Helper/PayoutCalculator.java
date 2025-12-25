package com.github.NNRIN.Helper;

import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;

/**
 * Implementation of {@link IPayoutCalculator} that provides standard Blackjack payout multipliers.
 */
public class PayoutCalculator implements IPayoutCalculator {

    /**
     * Returns the payout multiplier corresponding to the participant's state.
     * <ul>
     *     <li>{@link ParticipantStates#Winner}, {@link ParticipantStates#UnnaturalBlackJack}: 1.0 (1:1 payout)</li>
     *     <li>{@link ParticipantStates#BlackJack}: 1.5 (3:2 payout)</li>
     *     <li>{@link ParticipantStates#Loser}: -1.0 (Loss of bet)</li>
     *     <li>{@link ParticipantStates#Surrendered}: -0.5 (Loss of half bet)</li>
     *     <li>Other states: 0.0 (Push or no payout change)</li>
     * </ul>
     *
     * @param states The {@link ParticipantStates} representing the outcome.
     * @return The multiplier to be applied to the bet.
     */
    @Override
    public double getPayoutMultiplier(ParticipantStates states) {
        return switch (states) {
            case Winner, UnnaturalBlackJack -> 1.0;
            case BlackJack -> 1.5;
            case Loser -> -1.0;
            case Surrendered -> -0.5;
            default -> 0.0;
        };
    }
}