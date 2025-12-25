package com.github.NNRIN.Components.interfaces;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Helper.ParticipantStates;

import java.util.List;

public interface IHand {
    /**
     * Returns the Status for this hand.
     * @return ParticipantStates enum signalling current state.
     */
    ParticipantStates getStatus();

    /**
     * Sets the state of this Hand.
     * @param status The state to set the hand to.
     */
    void setStatus(ParticipantStates status);

    /**
     * Set the bet for the current hand.
     * @param bet The value to set the bet to.
     */
    void setBet(double bet);

    /**
     * Returns the bet of this hand.
     * @return The double value of the bet set for this hand. 
     */
    double getBet();

    /**
     * Get amount of Cards held by this hand.
     * @return The amount of cards in the hand.
     */
    int getCardAmount();

    /**
     * Returns the list of cards currently in the hand.
     * @return A list of Card objects.
     */
    List<Card> getCards();

    /**
     * Calculates and returns the total value of the hand according to Blackjack rules.
     * @return The integer value of the hand.
     */
    int getHandValue();

    /**
     * Checks if the hand value exceeds 21.
     * @return true if the hand is busted, false otherwise.
     */
    boolean isBusted();

    /**
     * Checks if the hand is a Natural Blackjack (an Ace and a 10-value card as the first two cards).
     * @return true if the hand is a Natural Blackjack, false otherwise.
     */
    boolean isNaturalBlackJack();

    /**
     * Checks if the hand can be split (usually if the first two cards have the same face value).
     * @return true if the hand is splittable, false otherwise.
     */
    boolean isSplittable();

    /**
     * Checks if the Double Down option is available for this hand.
     * @return true if Double Down is allowed, false otherwise.
     */
    boolean isDoubleDownAvailable();

    /**
     * Adds a card to the hand.
     * @param card The Card object to be added.
     */
    void addCard(Card card);

    /**
     * Calculates the payout for the round based on the hand's result and bet.
     * @return The payout amount as a double.
     */
    double getRoundPayout();

    /**
     * Splits the current hand into two separate hands.
     * @return The new IHand created from the split.
     */
    IHand split();

    /**
     * Signals that the player chooses to stand with the current hand.
     */
    void stand();

}
