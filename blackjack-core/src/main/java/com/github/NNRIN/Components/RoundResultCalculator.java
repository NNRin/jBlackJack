package com.github.NNRIN.Components;

import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Components.interfaces.IRoundResultCalculator;
import com.github.NNRIN.Helper.ParticipantStates;

public class RoundResultCalculator implements IRoundResultCalculator {
    @Override
    public ParticipantStates calculateResult(IHand playerHand, IHand dealerHand) {
        if (playerHand.isBusted()) {
            return ParticipantStates.Loser;
        }else if (dealerHand.isNaturalBlackJack()) {
            return handleDealerNaturalBlackJack(playerHand);
        } else if (playerHand.isNaturalBlackJack()) {
            return ParticipantStates.BlackJack;
        } else if (dealerHand.isBusted()){
            return ParticipantStates.Winner;
        }else if (playerHand.getHandValue() > dealerHand.getHandValue()) {
            return ParticipantStates.Winner;
        } else if (playerHand.getHandValue() == dealerHand.getHandValue()) {
            return ParticipantStates.Push;
        }else{
            return ParticipantStates.Loser;
        }
    }

    private ParticipantStates handleDealerNaturalBlackJack(IHand playerHand) {
        if (playerHand.isNaturalBlackJack()) {
            return ParticipantStates.Push;
        }
        return ParticipantStates.Loser;
    }
}
