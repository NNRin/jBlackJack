package com.github.NNRIN.Components;

import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Components.interfaces.IRoundResultCalculator;
import com.github.NNRIN.Helper.ParticipantStates;

public class RoundResultCalculator implements IRoundResultCalculator {
    @Override
    public ParticipantStates calculateResult(IHand playerHand, IHand dealerHand) {
        if (dealerHand.isNaturalBlackJack()) {
            return handleDealerNaturalBlackJack(playerHand);
        }
        return null;
    }

    private ParticipantStates handleDealerNaturalBlackJack(IHand playerHand) {
        if (playerHand.isNaturalBlackJack()) {
            return ParticipantStates.Push;
        }
        return ParticipantStates.Loser;
    }
}
