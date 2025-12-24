package com.github.NNRIN.Components;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Helper.ParticipantStates;

import java.util.List;

public class Hand implements IHand {

    @Override
    public ParticipantStates getStatus() {
        return null;
    }

    @Override
    public void setStatus(ParticipantStates status) {

    }

    @Override
    public void setBet(double bet) {

    }

    @Override
    public double getBet() {
        return 0;
    }

    @Override
    public int getCardAmount() {
        return 0;
    }

    @Override
    public List<Card> getCards() {
        return List.of();
    }

    @Override
    public int getHandValue() {
        return 0;
    }

    @Override
    public boolean isBusted() {
        return false;
    }

    @Override
    public boolean isNaturalBlackJack() {
        return false;
    }

    @Override
    public boolean isSplittable() {
        return false;
    }

    @Override
    public boolean isDoubleDownAvailable() {
        return false;
    }

    @Override
    public void AddCard(Card card) {

    }

    @Override
    public double getRoundPayout() {
        return 0;
    }

    @Override
    public IHand split() {
        return null;
    }

    @Override
    public void stand() {

    }
}
