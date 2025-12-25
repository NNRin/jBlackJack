package com.github.NNRIN.Cards.utils;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;

public class FacevalueCalculator implements IFacevalueCalculator {
    @Override
    public int getCardValue(Card card) {
        return switch (card.facevalue()) {
            case Facevalues.Cut -> throw new IllegalStateException("CUT cards need to be handled explicitly");
            case Facevalues.Ace -> 11;
            case Facevalues.Two -> 2;
            case Facevalues.Three -> 3;
            case Facevalues.Four -> 4;
            case Facevalues.Five -> 5;
            case Facevalues.Six -> 6;
            case Facevalues.Seven -> 7;
            case Facevalues.Eight -> 8;
            case Facevalues.Nine -> 9;
            case Facevalues.Ten, Facevalues.Jack, Facevalues.Queen, Facevalues.King -> 10;
            default -> throw new IllegalArgumentException("Unknown face value: " + card.facevalue());
        };
    }
}
