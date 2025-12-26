package com.github.NNRIN.Participants;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;
import com.github.NNRIN.Components.Hand;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Helper.ParticipantStates;
import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;
import com.github.NNRIN.Participants.interfaces.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player implements IPlayer {
    private List<IHand> hands = new ArrayList<>();
    private String name;
    private double credit;
    private boolean isSurrenderAvailable = false;
    private boolean isInsuranceBought = false;
    private double insuranceBet = 0;
    private boolean insuranceWon = false;
    private IPayoutCalculator payoutCalculator;
    private IFacevalueCalculator facevalueCalculator;

    public Player(String name, double credit,
            IFacevalueCalculator facevalueCalculator,
            IPayoutCalculator payoutCalculator
    ) {
        this.name = name;
        this.credit = credit;
        this.facevalueCalculator = facevalueCalculator;
        this.payoutCalculator = payoutCalculator;
        initHand();
    }

    private void initHand() {
        hands.add(new Hand(facevalueCalculator, payoutCalculator));
        hands.getFirst().setStatus(ParticipantStates.Preparing);
    }

    @Override
    public List<IHand> getHand() {
        return hands;
    }

    @Override
    public void setHand(List<IHand> hand) {
        hands = hand;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getCredit() {
        return credit;
    }

    @Override
    public boolean isGameOver() {
        return credit == 0 && hands.stream().map(h -> h.getBet()).reduce(0.0, (a, b) -> a + b) == 0;
    }

    @Override
    public boolean isSplitAvailable() {
        // enough credit needs to be there to split and ace's can only be split once
        try{
            IHand onTurnHand = getHandOnTurn();
            return credit >= onTurnHand.getBet() && onTurnHand.isSplittable() &&
                    ((!onTurnHand.getCards().get(0).facevalue().equals(Facevalues.Ace)) || hands.size() == 1);
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean isDoubleDownAvailable() {
        try{
            IHand onTurnHand = getHandOnTurn();
            return onTurnHand.getBet() <= credit && onTurnHand.isDoubleDownAvailable();
        } catch (Exception e){
            return false;
        }
    }

    private IHand getHandOnTurn() throws  Exception{
        Optional<IHand> onTurn = hands.stream()
                .filter(h -> h.getStatus() == ParticipantStates.OnTurn).findFirst();

        if(!onTurn.isPresent())
            throw new IllegalStateException("No Hand is onTurn");

        return onTurn.get();
    }

    /**
     * Sets the first hand with Status WaitingForTurn to be on Turn.
     * @return False if there are no more Hands that have not finished their actions (all FinishedTurn) and True
     * if a hand has been set to Status OnTurn.
     */
    private boolean setNextHandToBeOnTurn() {
        Optional<IHand> hand = hands.stream().
                filter(h -> h.getStatus() == ParticipantStates.WaitingForTurn).findFirst();

        if(hand.isEmpty())
            return false;

        hand.get().setStatus(ParticipantStates.OnTurn);
        return true;
    }

    @Override
    public boolean isSurrenderAvailable() {
        return isSurrenderAvailable;
    }

    @Override
    public void setIsSurrenderAvailable(boolean isSurrenderAvailable) {
        this.isSurrenderAvailable = isSurrenderAvailable;
    }

    @Override
    public boolean isInsuranceAvailable() {
        if (hands.size() > 1 || hands.get(0).getCards().size() > 2) {
            return false;
        }
        return credit >= hands.get(0).getBet() / 2;
    }

    @Override
    public boolean isInsuranceBought() {
        return isInsuranceBought;
    }

    @Override
    public double getInsuranceBet() {
        return insuranceBet;
    }

    @Override
    public boolean isInsuranceWon() {
        return insuranceWon;
    }

    @Override
    public void setBet(double bet) {
        if(bet > credit || bet < 0)
            throw new RuntimeException("bet amount is not smaller than credit or above 0");

        hands.get(0).setBet(bet); // guaranteed to be only one hand at betting stage
    }

    /**
     * Return True if all Hands are dealt with, false if still a Hand is OnTurn
     */
    @Override
    public boolean split(Card c1, Card c2) {
        try {
            if (!isSplitAvailable())
                throw new Exception("Not splittable");

            IHand onTurnHand = getHandOnTurn();

            boolean aceSplit = onTurnHand.getCards().get(0).facevalue().equals(Facevalues.Ace);

            credit -= onTurnHand.getBet();
            IHand newHand = onTurnHand.split();
            hands.add(newHand);

            onTurnHand.addCard(c1);
            onTurnHand.setIsSplitHand(true);
            newHand.addCard(c2);
            newHand.setIsSplitHand(true);

            if (aceSplit) {
                onTurnHand.setStatus(ParticipantStates.FinishedTurn);
                newHand.setStatus(ParticipantStates.FinishedTurn);
            }
            return !setNextHandToBeOnTurn();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * Return True if all Hands are dealt with, false if still a Hand is OnTurn
     */
    @Override
    public boolean doubleDown(Card c) {
        try {
            IHand handOnTurn = getHandOnTurn();
            if(!isDoubleDownAvailable())
                throw new Exception("DoubleDown not available");
            handOnTurn.addCard(c);
            credit -= handOnTurn.getBet();
            handOnTurn.setBet(handOnTurn.getBet()*2);
            handOnTurn.setStatus(ParticipantStates.FinishedTurn);
            return !setNextHandToBeOnTurn();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Return True if all Hands are dealt with, false if still a Hand is OnTurn
     * @param card
     * @return
     */
    @Override
    public boolean hit(Card card) {
        try {
            IHand hand = getHandOnTurn();
            hand.addCard(card);
            if (hand.getStatus() == ParticipantStates.OnTurn) {
                return false;
            }else{
                return !setNextHandToBeOnTurn();
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't hit, no Hand on Turn");
        }

    }

    @Override
    public void surrender() {
        try {
            IHand handOnTurn = getHandOnTurn();
            if(!isSurrenderAvailable())
                throw new Exception("Surrender not available");

            handOnTurn.setStatus(ParticipantStates.Surrendered);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void insure() {
            if(!isInsuranceAvailable())
                throw new RuntimeException("Insurance not available");

            insuranceBet = hands.get(0).getBet() / 2;
            credit -= insuranceBet;
            isInsuranceBought = true;
    }

    @Override
    public void payoutWinnings() {
        for (IHand h : hands) {
            credit += h.getRoundPayout();
        }
    }

    @Override
    public void triggerInsuranceWin() {
        if (!isInsuranceBought())
            throw new RuntimeException("No Insurance Bought");

        credit += insuranceBet + insuranceBet * 2;
        insuranceWon = true;
    }

    @Override
    public void reset() {
        insuranceWon = false;
        insuranceBet = 0;
        isInsuranceBought = false;
        isSurrenderAvailable = false;


        hands.clear();
        initHand();
    }

    /**
     * Applies Stand action on current hand (onTurn status). And sets the next Hand to be on TUrn of this player, if
     * there is a Hand that has not been played yet.
     * @return True if no more Hand of Player is onTurn, False if Player still has a HandOnTurn
     */
    @Override
    public boolean stand() {
        try{
            IHand h = getHandOnTurn();
            h.stand();
            return !setNextHandToBeOnTurn();
        }catch (Exception e){
            throw new RuntimeException("Can't stand, no hand on Turn");
        }


    }
}