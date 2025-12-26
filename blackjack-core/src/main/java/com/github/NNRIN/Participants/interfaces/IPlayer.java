package com.github.NNRIN.Participants.interfaces;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Components.interfaces.IHand;

import java.util.List;

public interface IPlayer {
    List<IHand> getHand();

    void setHand(List<IHand> hand);

    String getName();

    double getCredit();

    boolean isGameOver();

    boolean isSplitAvailable();

    boolean isDoubleDownAvailable();

    boolean isSurrenderAvailable();

    boolean isInsuranceAvailable();

    boolean isInsuranceBought();

    double getInsuranceBet();

    boolean isInsuranceWon();

    // set on hand-on-turn's bet
    void setBet(double bet);

    void setIsSurrenderAvailable(boolean isSurrenderAvailable);

    /*todo: method doc below describes a specific status, but this interface does not define such a contract. Refine
    that to be a general statement that True indicates all Hands are dealth with and False means we still have to deal
    with some*/
    /**
     * Applies Split action on current hand (onTurn status). And sets the next Hand to be on Turn of this player, but
     * only if there is a Hand that has not been played yet.
     * @param c1
     * @param c2
     * @return True if all Hands of this Player are dealt with and of Status FinishedTurn, false if there is still at
     * least one Hand that needs to be dealth with and has now been set to OnTurn.
     */
    boolean split(Card c1, Card c2);

    /**
     * Applies DoubleDown action on current hand (onTurn status). And sets the next Hand to be on Turn of this player, but
     * only if there is a Hand that has not been played yet.
     * @return True if all Hands of this Player are dealt with and of Status FinishedTurn, false if there is still at
     * least one Hand that needs to be dealth with and has now been set to OnTurn.
     */
    boolean doubleDown(Card c);

    /**
     * Applies Hit action on current hand (onTurn status). And sets the next Hand to be on Turn of this player, but
     * only if there is a Hand that has not been played yet.
     * @return True if all Hands of this Player are dealt with and of Status FinishedTurn, false if there is still at
     * least one Hand that needs to be dealth with and has now been set to OnTurn.
     */
    boolean hit(Card card);

    void surrender();

    void insure();

    void payoutWinnings();

    void triggerInsuranceWin();

    void reset();

    /**
     * Applies Stand action on current hand (onTurn status). And sets the next Hand to be on Turn of this player, but
     * only if there is a Hand that has not been played yet.
     * @return True if all Hands of this Player are dealt with and of Status FinishedTurn, false if there is still at
     * least one Hand that needs to be dealth with and has now been set to OnTurn.
     */
    boolean stand();

}