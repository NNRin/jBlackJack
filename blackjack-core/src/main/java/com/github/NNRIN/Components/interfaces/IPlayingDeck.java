package com.github.NNRIN.Components.interfaces;

import com.github.NNRIN.Cards.Card;

import java.util.Stack;

/**
 * Interface for a PlayingDeck, in Blackjack a Stack of 6 (or more) Decks is used. This interface represents that deck
 * and outlines the contract an implementing class has to fulfil.
 */
public interface IPlayingDeck {
    /**
     * @return An int value signalling the amount of Decks in this PlayingDeck.
     */
    int getDeckAmount();

    /**
     * @return An int value signalling the amount of cards in this Deck ('on the Stack').
     */
    int getStackSize();

    /**
     * This method removes and returns the topmost card on the Stack, if a Cut Card is drawn the Deck will update its
     * state and signal that that Card no longer present.
     * It then is in the using Objects duty to reshuffle (initialize a new PlayingDeck).
     * @return a valid Card (no Cut Card)
     */
    Card Pop();

    /**
     * Signals if the Cut Card is present in this PlayingDecks Card Stack.
     * @return
     */
    boolean isCutCardInStack();
}
