package com.github.NNRIN.Helper.interfaces;

import com.github.NNRIN.Helper.ParticipantStates;

/**
 * Interface for calculating payout multipliers based on the participant's state.
 */
public interface IPayoutCalculator {
    /**
     * Determines the payout multiplier for a given participant state.
     * The multiplier is applied to the bet amount to calculate the win or loss.
     *
     * @param states The {@link ParticipantStates} representing the outcome of the hand.
     * @return The payout multiplier (e.g., 1.5 for Blackjack, -1.0 for a loss).
     */
    double getPayoutMultiplier(ParticipantStates states);
}