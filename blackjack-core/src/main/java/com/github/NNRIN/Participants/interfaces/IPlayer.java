package com.github.NNRIN.Participants.interfaces;

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

    IHand split();

    void doubleDown();

    void surrender();

    void insure();

    void payoutWinnings();

    void triggerInsuranceWin();

    void reset();

}
