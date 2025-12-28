package com.github.NNRIN.Components;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.utils.FacevalueCalculator;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Helper.ParticipantStates;
import com.github.NNRIN.Helper.PayoutCalculator;
import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hand of cards in a Blackjack game.
 * Manages the cards, bet, and status of the hand.
 */
public class Hand implements IHand {
    private List<Card> cards = new ArrayList<>();
    private double bet;
    private ParticipantStates status;
    private IFacevalueCalculator facevalueCalculator;
    private IPayoutCalculator payoutCalculator;
    private boolean isSplitHand = false;

    /**
     * Constructs a new Hand with the specified calculators.
     *
     * @param facevalueCalculator The calculator used to determine card values.
     * @param payoutCalculator    The calculator used to determine payouts.
     */
    public Hand(IFacevalueCalculator facevalueCalculator, IPayoutCalculator payoutCalculator) {
        this.facevalueCalculator = facevalueCalculator;
        this.payoutCalculator = payoutCalculator;
        status = ParticipantStates.Preparing;
    }

    /**
     * Gets the current status of the hand.
     *
     * @return The current {@link ParticipantStates} of the hand.
     */
    @Override
    public ParticipantStates getStatus() {
        return status;
    }

    /**
     * Sets the status of the hand.
     *
     * @param status The new {@link ParticipantStates} to set.
     */
    @Override
    public void setStatus(ParticipantStates status) {
        this.status = status;
    }

    /**
     * Sets the bet amount for this hand.
     *
     * @param bet The bet amount. Must be greater than 0.
     * @throws RuntimeException if the bet is less than or equal to 0.
     */
    @Override
    public void setBet(double bet) {
        if(bet <= 0)
            throw new RuntimeException("Bet can't be below or 0");
        this.bet = bet;
    }

    /**
     * Gets the current bet amount.
     *
     * @return The bet amount.
     */
    @Override
    public double getBet() {
        return bet;
    }

    /**
     * Gets the number of cards in the hand.
     *
     * @return The number of cards.
     */
    @Override
    public int getCardAmount() {
        return cards.size();
    }

    /**
     * Gets the list of cards in the hand.
     *
     * @return A list of {@link Card} objects.
     */
    @Override
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Calculates and returns the current value of the hand.
     *
     * @return The hand value.
     */
    @Override
    public int getHandValue() {
        return calcHandValue();
    }

    /**
     * Calculate the optimal value possible for the cards held in this Hand. The optimal value is the maximum number 
     * possible by adding together the values of the cards in this hand whilst staying under 21. What allows this to be
     * variable is that aces can be counted as 1 or 11, thus we calculate the values of the cards together with the ace 
     * counting as 11, and if over 21 we subtract 10 if any aces were counted to be present, this will be repeated for 
     * every ace.
     * @return Optimal hand value as int.
     */
    private int calcHandValue() {
        int aceCount = (int)cards.stream().filter(card -> card.facevalue() == Facevalues.Ace).count();
        int handValue = cards.stream().map(facevalueCalculator::getCardValue).
                reduce(0,  (a, b) -> a+b);
        
        // loop as long as needed to get below or to 21 whilst still having aces to subtract. 
        while (aceCount > 0 && handValue > 21) {
            aceCount--;
            handValue-=10;
        }
        return handValue;
    }

    /**
     * Checks if the hand is busted (value > 21).
     *
     * @return true if the hand value exceeds 21, false otherwise.
     */
    @Override
    public boolean isBusted() {
        return calcHandValue() > 21;
    }

    /**
     * Checks if the hand is a natural Blackjack (2 cards totaling 21).
     *
     * @return true if the hand is a natural Blackjack, false otherwise.
     */
    @Override
    public boolean isNaturalBlackJack() {
        return cards.size() == 2 && calcHandValue() == 21 && !isSplitHand;
    }

    /**
     * Checks if the hand can be split.
     * A hand is splittable if it has exactly 2 cards of the same face value and the participant is on turn.
     *
     * @return true if the hand can be split, false otherwise.
     */
    @Override
    public boolean isSplittable() {
        return cards.size() == 2 && cards.get(0).facevalue().equals(cards.get(1).facevalue()) && 
                status == ParticipantStates.OnTurn;
    }

    /**
     * Checks if Double Down is available for this hand.
     * Double Down is available if the hand has exactly 2 cards and the participant is on turn.
     *
     * @return true if Double Down is available, false otherwise.
     */
    @Override
    public boolean isDoubleDownAvailable() {
        return cards.size() == 2 && status == ParticipantStates.OnTurn;
    }

    /**
     * Adds a card to the hand.
     * If the hand busts after adding the card, the status is updated to FinishedTurn.
     *
     * @param card The card to add.
     */
    @Override
    public void addCard(Card card) { //todo: also set finished turn if value = 21
        cards.add(card);
        if(isBusted())
            status = ParticipantStates.FinishedTurn;
    }

    /**
     * Calculates the payout for the round based on the bet and the hand status.
     *
     * @return The calculated payout amount.
     */
    @Override
    public double getRoundPayout() {
        return bet + bet * payoutCalculator.getPayoutMultiplier(status);
    }

    /**
     * Splits the current hand into two hands.
     * Creates a new hand with one of the cards from the current hand and the same bet.
     *
     * @return The new {@link IHand} created from the split.
     * @throws IllegalStateException if the hand is not splittable.
     */
    @Override
    public IHand split() {
        if(!isSplittable())
            throw new IllegalStateException("This Hand is not splittable");

        Hand splitHand = new Hand(facevalueCalculator, payoutCalculator);
        splitHand.setStatus(ParticipantStates.WaitingForTurn);
        splitHand.addCard(cards.remove(1));
        splitHand.setBet(bet);
        return splitHand;
    }

    /**
     * Ends the turn for the current hand.
     * Sets the status to FinishedTurn.
     */
    @Override
    public void stand() {
        status = ParticipantStates.FinishedTurn;
    }

    @Override
    public void reset() {
        cards.clear();
        bet = 0;
        status = ParticipantStates.Preparing;
    }

    @Override
    public void setIsSplitHand(boolean isSplitHand) {
        this.isSplitHand = isSplitHand;
    }

    @Override
    public IHand getDealersPublicHand() {
        Hand toReturn = new Hand(facevalueCalculator, payoutCalculator);
        toReturn.isSplitHand = this.isSplitHand;
        toReturn.status = this.status;
        toReturn.bet = this.bet;
        if(this.cards.size() != 0){
            Card upCard = this.cards.get(0);
            toReturn.cards.add(new Card(upCard.suit(), upCard.facevalue()));

        }
        return toReturn;
    }
}