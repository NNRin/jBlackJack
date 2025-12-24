package com.github.NNRIN.Helper;

public enum ParticipantStates {
    Preparing,
    Betting,
    WaitingForTurn,
    OnTurn,
    FinishedTurn,
    Winner, // Pays back Bet + 1x Bet
    BlackJack, // Pays back Bet + 1.5x Bet
    UnnaturalBlackJack, // Pays Bet + 1x Bet, occurs when the Hand has BlackJack and originated as a Split Hand
    Push, // Tie, pays back the Bet
    Loser, // Pays back nothing
    Surrendered, // Pays back 0.5x Bet
}
