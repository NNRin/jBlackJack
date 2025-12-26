package com.github.NNRIN.Participants;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.utils.FacevalueCalculator;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;
import com.github.NNRIN.Components.Hand;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Helper.ParticipantStates;
import com.github.NNRIN.Helper.PayoutCalculator;
import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;
import com.github.NNRIN.Participants.interfaces.IDealer;

import java.util.List;

public class Dealer implements IDealer {
    private IHand hand;
    private boolean isHiddenHand = true;
    private IFacevalueCalculator facevalueCalculator;

    public Dealer(IFacevalueCalculator facevalueCalculator, IPayoutCalculator payoutCalculator) {
        this.hand = new Hand(facevalueCalculator, payoutCalculator);
        hand.setStatus(ParticipantStates.WaitingForTurn);
        this.facevalueCalculator = facevalueCalculator;
    }

    @Override
    public IHand getHand() {
        return hand;
    }

    @Override
    public void setHand(IHand hand) {
        this.hand = hand;
    }

    @Override
    public boolean isHiddenHand() {
        return isHiddenHand;
    }

    @Override
    public void setHiddenHand(boolean isHidden) {
        this.isHiddenHand = isHidden;
    }

    @Override
    public boolean isInsuranceOffered() {
        List<Card> tmp = hand.getCards();
        return hand.getCardAmount() == 2 && tmp.get(0).facevalue().equals(Facevalues.Ace) && isHiddenHand;
    }

    @Override
    public void reset() {
        hand.reset();
        hand.setStatus(ParticipantStates.WaitingForTurn);
        isHiddenHand = true;
    }

    @Override
    public void addCardToHand(Card card) {
        hand.addCard(card);
    }

    @Override
    public boolean isPrematureBlackJack() {
        return isInsuranceOffered() && facevalueCalculator.getCardValue(hand.getCards().get(1)) == 10;
    }
}
