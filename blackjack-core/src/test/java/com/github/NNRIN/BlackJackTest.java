package com.github.NNRIN;

import com.github.NNRIN.Cards.Card;
import com.github.NNRIN.Cards.Facevalues;
import com.github.NNRIN.Cards.Suits;
import com.github.NNRIN.Cards.utils.FacevalueCalculator;
import com.github.NNRIN.Cards.utils.interfaces.IFacevalueCalculator;
import com.github.NNRIN.Components.PlayingDeck;
import com.github.NNRIN.Components.RoundResultCalculator;
import com.github.NNRIN.Components.SinglePlayerGameManager;
import com.github.NNRIN.Components.interfaces.IHand;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Helper.Actions;
import com.github.NNRIN.Helper.GameState;
import com.github.NNRIN.Helper.ParticipantStates;
import com.github.NNRIN.Helper.PayoutCalculator;
import com.github.NNRIN.Helper.interfaces.IPayoutCalculator;
import com.github.NNRIN.Mocks.MockDeck;
import com.github.NNRIN.Participants.Dealer;
import com.github.NNRIN.Participants.Player;
import com.github.NNRIN.Participants.interfaces.IDealer;
import com.github.NNRIN.Participants.interfaces.IPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlackJackRoundTest {

    @Test
    void testInitialState() {
        MockDeck mockDeck = new MockDeck();

        // 2. Define the exact cards to come out of the deck
        // Sequence: Player Card 1 -> Dealer Card 1 -> Player Card 2 -> Dealer Card 2
        mockDeck.setDeckSequence(
                new Card(Suits.Hearts, Facevalues.Two),   // Dealer
                new Card(Suits.Hearts, Facevalues.Nine),   // ^
                new Card(Suits.Hearts, Facevalues.Ace),  // Playe
                new Card(Suits.Clubs, Facevalues.King)    // ^
        );

        IFacevalueCalculator facevalueCalculator = new FacevalueCalculator();
        IPayoutCalculator payoutCalculator = new PayoutCalculator();

        ISingePlayerGameManager gameManager = new SinglePlayerGameManager(new Dealer(facevalueCalculator, payoutCalculator),
                new Player("You", 10_000, facevalueCalculator, payoutCalculator),
                mockDeck,
                new RoundResultCalculator()
        );

        // Check Game State----------------------------------------------------------------------------------------------
        assertEquals(GameState.WaitingForBet, gameManager.getGameState(),
                "Game should be in WaitingForBet state initially");

        // Check Stack Shuffle Status
        assertFalse(gameManager.wasStackReshuffled(),
                "Stack should not be signaled as reshuffled at start");

        // Ensure Actors exist
        assertNotNull(gameManager.getDealer(), "Dealer should be initialized");
        assertNotNull(gameManager.getPlayer(), "Player should be initialized");
        // Check Dealer ----------------------------------------------------------------------------------------------
        IDealer dealer = gameManager.getDealer();

        // Dealer Specific State
        assertTrue(dealer.isHiddenHand(),
                "Dealer hand is usually set to hidden by default");

        // Dealer Hand Checks
        IHand dealerHand = dealer.getHand();
        assertNotNull(dealerHand, "Dealer should have a Hand object");
        assertEquals(0, dealerHand.getCardAmount(),
                "Dealer should have 0 cards before betting round ends");
        assertTrue(dealerHand.getCards().isEmpty(),
                "Dealer card list should be empty");

        assertTrue(dealerHand.getStatus() == ParticipantStates.WaitingForTurn);

        // Check Player ----------------------------------------------------------------------------------------------

        IPlayer player = gameManager.getPlayer();

        // Basic Info
        assertEquals("You", player.getName(),
                "Player name should match initialization");
        assertEquals(10000.0, player.getCredit(), 0.001,
                "Player should start with default credit");

        // Status Booleans (All should be false at start)
        assertFalse(player.isGameOver(), "Game is not over at start");
        assertFalse(player.isSurrenderAvailable(), "Surrender not available before deal");
        assertFalse(player.isSplitAvailable(), "Split not available before deal");
        assertFalse(player.isDoubleDownAvailable(), "DoubleDown not available before deal");
        assertFalse(player.isInsuranceAvailable(), "Insurance not available before deal");

        // Insurance Specifics
        assertFalse(player.isInsuranceBought(), "Insurance cannot be bought yet");
        assertFalse(player.isInsuranceWon(), "Insurance cannot be won yet");
        assertEquals(0.0, player.getInsuranceBet(), 0.001, "Insurance bet should be 0");

        List<IHand> playerHands = player.getHand();
        // List checks
        assertNotNull(playerHands, "Player hand list should not be null");
        assertEquals(1, playerHands.size(),
                "Player should have 1 empty hand to start");

        // Individual Hand Checks
        IHand initialHand = playerHands.get(0);

        // Status: Should be Betting (or Preparing) based on GameState
        assertEquals(ParticipantStates.Betting, initialHand.getStatus(),
                "Hand should be in Betting state");

        // Bet and Value Checks
        assertEquals(0.0, initialHand.getBet(), 0.001, "Bet should be 0 initially");
        assertEquals(0, initialHand.getHandValue(), "Hand value should be 0");
        assertFalse(initialHand.isBusted(), "Hand cannot be busted yet");
        assertFalse(initialHand.isNaturalBlackJack(), "Cannot be BlackJack yet");

        // Content Checks
        assertEquals(0, initialHand.getCardAmount(), "Hand should have 0 cards");
        assertTrue(initialHand.getCards().isEmpty(), "Card list should be empty");

    }

}