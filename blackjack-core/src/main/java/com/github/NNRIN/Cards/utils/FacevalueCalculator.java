package com.github.NNRIN.Cards.utils;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;

public class FacevalueCalculator implements IFacevalueCalculator {
    @Override
    public int[] getCardValue(Card card) {
        return switch (card.facevalue()) {
            case Facevalues.Cut -> throw new IllegalStateException("CUT cards need to be handled explicitly");
            case Facevalues.Ace -> new int[]{1, 11};
            case Facevalues.Two -> new int[]{2, 2};
            case Facevalues.Three -> new int[]{3, 3};
            case Facevalues.Four -> new int[]{4, 4};
            case Facevalues.Five -> new int[]{5, 5};
            case Facevalues.Six -> new int[]{6, 6};
            case Facevalues.Seven -> new int[]{7, 7};
            case Facevalues.Eight -> new int[]{8, 8};
            case Facevalues.Nine -> new int[]{9, 9};
            case Facevalues.Ten, Facevalues.Jack, Facevalues.Queen, Facevalues.King -> new int[]{10, 10};
            default -> throw new IllegalArgumentException("Unknown face value: " + card.facevalue());
        };
    }
}
