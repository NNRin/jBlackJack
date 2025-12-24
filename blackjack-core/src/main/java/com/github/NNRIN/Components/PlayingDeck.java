package com.github.NNRIN.Components;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.Suits;
import com.github.NNRIN.Components.interfaces.IPlayingDeck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class PlayingDeck implements IPlayingDeck {
    private final Stack<Card> stack = new Stack<>();
    private int decks;
    private boolean isCutCardInStack = true;

    /**
     * Creates a new PlayingDeck with the specified number of decks.
     * The deck is automatically populated, shuffled, and a cut card is inserted.
     *
     * @param decks The number of standard 52-card decks to include in the shoe.
     */
    public PlayingDeck(int decks) {
        this.decks = decks;
        populateStack(decks);
        shuffleStackAndAddCutCard();
    }

    /**
     * Adds the input amount of Decks onto the stack.
     * @param decks amount of decks to add
     */
    private void populateStack(int decks) {
        for (int i = 0; i < decks; i++) {
            for (int suitNumber = 0; suitNumber < 4; suitNumber++) { // loop through all four suits
                Suits suit = Suits.values()[suitNumber];
                stack.push(new Card(suit, Facevalues.Two));
                stack.push(new Card(suit, Facevalues.Three));
                stack.push(new Card(suit, Facevalues.Four));
                stack.push(new Card(suit, Facevalues.Five));
                stack.push(new Card(suit, Facevalues.Six));
                stack.push(new Card(suit, Facevalues.Seven));
                stack.push(new Card(suit, Facevalues.Eight));
                stack.push(new Card(suit, Facevalues.Nine));
                stack.push(new Card(suit, Facevalues.Ten));
                stack.push(new Card(suit, Facevalues.Jack));
                stack.push(new Card(suit, Facevalues.Queen));
                stack.push(new Card(suit, Facevalues.King));
                stack.push(new Card(suit, Facevalues.Ace));
            }
        }
    }

    /**
     * Takes the objects from the stack and inserts them shuffled, also inserts a Cut Card between 60% and 80% of the
     * total amount of cards.
     */
    private void shuffleStackAndAddCutCard() {
        Random random = new Random();
        int stackSize = stack.size() + 1;
        int cutCardMin = (int)Math.ceil(stackSize * 0.6);
        int cutCardMax = (int)Math.floor(stackSize * 0.8);
        int cutCardIndex = random.nextInt(cutCardMin, cutCardMax+1);

        List<Card> tmpList = new ArrayList<>();
        tmpList.addAll(stack);
        stack.clear();

        for (int i = 0; i < stackSize; i++) {
            if (i == cutCardIndex) {
                stack.push(new Card(Suits.Cut, Facevalues.Cut));
            }else{
                int rdm = random.nextInt(0, tmpList.size());
                Card tmp = tmpList.remove(rdm);
                stack.push(tmp);
            }
        }
    }

    /**
     * Gets the total number of decks used in this playing deck.
     *
     * @return The number of decks.
     */
    @Override
    public int getDeckAmount() {
        return decks;
    }

    /**
     * Gets the current number of cards remaining in the stack.
     *
     * @return The size of the stack.
     */
    @Override
    public int getStackSize() {
        return stack.size();
    }

    /**
     * Removes and returns the top card from the stack.
     * If the top card is the Cut Card, it is discarded, the flag is updated, and the next card is returned.
     *
     * @return The next Card from the stack.
     * @throws RuntimeException if the stack is empty.
     */
    @Override
    public Card Pop() {
        if(stack.size() == 0)
            throw new RuntimeException("Stack is never allowed to be empty");

        if (stack.peek().equals(new Card(Suits.Cut, Facevalues.Cut))) {
            Card _discard = stack.pop();
            isCutCardInStack = false;
        }
        return stack.pop();
    }

    /**
     * Checks if the Cut Card is still present in the stack.
     *
     * @return true if the Cut Card is in the stack, false otherwise.
     */
    @Override
    public boolean isCutCardInStack() {
        return isCutCardInStack;
    }
}
