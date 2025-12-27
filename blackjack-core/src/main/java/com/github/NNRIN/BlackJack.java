package com.github.NNRIN;

import com.github.NNRIN.Cards.utils.FacevalueCalculator;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;
import com.github.NNRIN.Components.PlayingDeck;
import com.github.NNRIN.Components.RoundResultCalculator;
import com.github.NNRIN.Components.SinglePlayerGameManager;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Helper.PayoutCalculator;
import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;
import com.github.NNRIN.Participants.Dealer;
import com.github.NNRIN.Participants.Player;

public class BlackJack {
    public static ISingePlayerGameManager getSingleplayerGame() {
        return getSingleplayerGame("");
    }

    public static ISingePlayerGameManager getSingleplayerGame(String id) {
        IFacevalueCalculator facevalueCalculator = new FacevalueCalculator();
        IPayoutCalculator payoutCalculator = new PayoutCalculator();

        return new SinglePlayerGameManager(new Dealer(facevalueCalculator, payoutCalculator),
                new Player("You", 10_000, facevalueCalculator, payoutCalculator),
                new PlayingDeck(6),
                new RoundResultCalculator(),
                id
        );

    }
}
