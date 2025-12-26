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

    @Test
    void testPlayerHitThenStand_PlayerWinsViaDealerBust() {
        MockDeck mockDeck = new MockDeck();

        // 1. Define Deck Sequence (FIFO)
        // Note: Based on SinglePlayerGameManager.dealInitialCards(), the order is:
        // Dealer Card 1 -> Dealer Card 2 -> Player Card 1 -> Player Card 2 -> Hit Cards...
        mockDeck.setDeckSequence(
                // Initial Deal
                new Card(Suits.Clubs, Facevalues.Ten),    // Dealer Card 1 (10)
                new Card(Suits.Clubs, Facevalues.Six),    // Dealer Card 2 (6) -> Dealer Total: 16
                new Card(Suits.Hearts, Facevalues.Ten),   // Player Card 1 (10)
                new Card(Suits.Hearts, Facevalues.Two),   // Player Card 2 (2)  -> Player Total: 12

                // Game Actions
                new Card(Suits.Hearts, Facevalues.Seven), // Player Hit Card (7) -> Player Total: 19
                new Card(Suits.Spades, Facevalues.Ten)    // Dealer Draw Card (10) -> Dealer Total: 26 (BUST)
        );

        // 2. Initialize Game Components
        IFacevalueCalculator facevalueCalculator = new FacevalueCalculator();
        IPayoutCalculator payoutCalculator = new PayoutCalculator();
        double initialCredit = 1000.0;

        ISingePlayerGameManager gameManager = new SinglePlayerGameManager(
                new Dealer(facevalueCalculator, payoutCalculator),
                new Player("You", initialCredit, facevalueCalculator, payoutCalculator),
                mockDeck,
                new RoundResultCalculator()
        );

        // 3. Start Round by Placing Bet
        double betAmount = 100.0;
        gameManager.placeBet(betAmount);

        // Assert: Game should allow Surrender immediately after dealing
        assertEquals(GameState.WaitingForMoveSurrenderAvailable, gameManager.getGameState());

        // 4. Action: Hit
        // Player moves from 12 to 19.
        gameManager.takeAction(Actions.Hit);

        // Assert: Surrender is no longer available after an action is taken
        assertFalse(gameManager.getPlayer().isSurrenderAvailable());
        assertEquals(GameState.WaitingForMove, gameManager.getGameState());

        // Check Player Hand Value is now 19
        assertEquals(19, gameManager.getPlayer().getHand().get(0).getHandValue());

        // 5. Action: Stand
        // Player freezes at 19. Dealer plays (draws 10 -> busts).
        gameManager.takeAction(Actions.Stand);

        // 6. Final State Assertions
        IPlayer player = gameManager.getPlayer();
        IHand playerHand = player.getHand().get(0);

        // Verify Round Over
        assertEquals(GameState.RoundOver, gameManager.getGameState(),
                "Game should be over after Player stands and Dealer busts");

        // Verify Result Status
        assertEquals(ParticipantStates.Winner, playerHand.getStatus(),
                "Player should be Winner because Dealer busted");

        // Verify Payout
        // Logic: 1000 - 100 (Bet) + 200 (Winnings: Bet + 1:1 Payout) = 1100
        double expectedCredit = initialCredit + betAmount;
        assertEquals(expectedCredit, player.getCredit(), 0.001,
                "Credit should reflect standard win payout (1:1)");
    }

}