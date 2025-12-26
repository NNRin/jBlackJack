package com.github.NNRIN.Mocks;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Components.interfaces.IPlayingDeck;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MockDeck implements IPlayingDeck {

    // We use a Queue to maintain strict FIFO order (First card added = First card Popped)
    private final Queue<Card> scriptedCards = new LinkedList<>();

    // Default setting for tests, can be toggled
    private boolean isCutCardInStack = true;
    private int deckAmount = 6;

    /**
     * TEST HELPER: Clears the current deck and sets the exact sequence of cards to be drawn.
     * The first card in the arguments will be the first card returned by Pop().
     * * @param cards The cards to be drawn.
     */
    public void setDeckSequence(Card... cards) {
        this.scriptedCards.clear();
        this.scriptedCards.addAll(Arrays.asList(cards));
    }

    /**
     * TEST HELPER: Set the return value for isCutCardInStack()
     */
    public void setCutCardInStack(boolean isPresent) {
        this.isCutCardInStack = isPresent;
    }

    // --- Interface Implementation ---

    @Override
    public int getDeckAmount() {
        return this.deckAmount; // Returns standard 6 unless you add a setter to change it
    }

    @Override
    public int getStackSize() {
        return scriptedCards.size();
    }

    @Override
    public Card Pop() {
        if (scriptedCards.isEmpty()) {
            throw new RuntimeException("MockDeck Empty: You requested a Pop() but no cards were defined in the sequence.");
        }
        // "removes and returns the topmost card"
        return scriptedCards.poll();
    }

    @Override
    public boolean isCutCardInStack() {
        // "Signals if the Cut Card is present"
        return this.isCutCardInStack;
    }
}