package com.github.NNRIN.Participants;

import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.utils.FacevalueCalculator;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;
import com.github.NNRIN.Components.Hand;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Helper.ParticipantStates;
import com.github.NNRIN.Helper.PayoutCalculator;
import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;
import com.github.NNRIN.Participants.interfaces.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player implements IPlayer {
    private List<IHand> hands = new ArrayList<>();
    private String name;
    private double credit;
    private boolean isSurrenderAvailable = true;
    private boolean isInsuranceBought = false;
    private double insuranecBet = 0;
    private boolean insuranceWon = false;

    public Player(String name, double credit,
            IFacevalueCalculator facevalueCalculator,
            IPayoutCalculator payoutCalculator
    ) {
        this.name = name;
        this.credit = credit;
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
            throw new IllegalStateException();

        return onTurn.get();
    }

    @Override
    public boolean isSurrenderAvailable() {
        return isSurrenderAvailable;
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
        return insuranecBet;
    }

    @Override
    public boolean isInsuranceWon() {
        return insuranceWon;
    }

    @Override
    public void setBet(double bet) {
        try{
            IHand onTurnHand = getHandOnTurn();
            if(bet > credit || bet < 0)
                throw new Exception("");

            onTurnHand.setBet(bet);
        } catch (Exception e){
            throw new RuntimeException("no hand on turn or wrong credit amount");
        }
    }

    @Override
    public IHand split() {
        return null;
    }

    @Override
    public void doubleDown() {

    }

    @Override
    public void surrender() {

    }

    @Override
    public void insure() {

    }

    @Override
    public void payoutWinnings() {

    }

    @Override
    public void triggerInsuranceWin() {

    }

    @Override
    public void reset() {

    }
}
