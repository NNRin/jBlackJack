package com.github.NNRIN.Helper;

public enum ParticipantStates {
    Preparing,
    Betting,
    Insuring,
    WaitingForTurn,
    OnTurn,
    FinishedTurn,
    Winner, // Pays back Bet + 1x Bet, also applied for unnatural BlackJack
    BlackJack, // Pays back Bet + 1.5x Bet
    Push, // Tie, pays back the Bet
    Loser, // Pays back nothing
    Surrendered, // Pays back 0.5x Bet
}
