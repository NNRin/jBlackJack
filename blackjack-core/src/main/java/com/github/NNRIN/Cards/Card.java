package com.github.NNRIN.Cards;

public record Card(Suits suit, Facevalues facevalue) {
    public Card{
        if (suit == null || facevalue == null) {
            throw new IllegalArgumentException("Suits or Facevalue can't be null");
        }
    } // assignment of fields is automatic
}
