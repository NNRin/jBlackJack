package com.github.NNRIN.Components.interfaces;

import com.github.NNRIN.Helper.ParticipantStates;

public interface IRoundResultCalculator {
    ParticipantStates calculateResult(IHand playerHand, IHand dealerHand);
}
