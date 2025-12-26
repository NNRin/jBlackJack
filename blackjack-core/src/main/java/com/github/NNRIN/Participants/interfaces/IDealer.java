package com.github.NNRIN.Participants.interfaces;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Components.interfaces.IHand;

/**
 * Represents the Dealer in a Blackjack game.
 * The Dealer manages their own hand and game state specific to the dealer's role,
 * such as hiding cards and offering insurance.
 */
public interface IDealer {
    /**
     * Retrieves the Dealer's current hand.
     *
     * @return The {@link IHand} belonging to the Dealer.
     */
    IHand getHand();

    /**
     * Sets the Dealer's hand for the round.
     *
     * @param hand The {@link IHand} to be assigned to the Dealer.
     */
    void setHand(IHand hand);

    /**
     * Checks if the Dealer's hand is currently partially hidden.
     * In Blackjack, one of the Dealer's cards (the "hole card") is typically hidden until the players finish their turns.
     *
     * @return true if the hand is hidden, false otherwise.
     */
    boolean isHiddenHand();

    /**
     * Sets the visibility of the Dealer's hand.
     * Typically used to reveal the hole card or hide it at the start of a round.
     *
     * @param isHidden true to hide the hand (or specific cards), false to reveal it.
     */
    void setHiddenHand(boolean isHidden);

    /**
     * Checks if insurance is currently being offered to players.
     * Insurance is typically offered when the Dealer's visible card is an Ace.
     *
     * @return true if insurance is offered, false otherwise.
     */
    boolean isInsuranceOffered();

    /**
     * Resets the Dealer's state for a new round.
     * This should clear the hand and reset any round-specific flags.
     */
    void reset();

    /**
     * Adds a card to the Dealer's hand.
     * The Dealer typically draws cards according to specific rules (e.g., hit on soft 17).
     * Note: The Dealer can only have one hand and cannot split.
     *
     * @param card The {@link Card} to add to the hand.
     */
    void addCardToHand(Card card);

    /**
     * Signals the Dealer has a BlackJack and their upcard is an ace. This causes the round to be over instantly.
     * @return
     */
    boolean isPrematureBlackJack();
    
}