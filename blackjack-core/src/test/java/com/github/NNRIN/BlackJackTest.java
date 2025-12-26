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

        // ==============================================================================================
        // 4. Meticulous End-State Assertions
        // ==============================================================================================

        // --- Game Manager State ---
        assertEquals(GameState.RoundOver, gameManager.getGameState(),
                "Game must be in RoundOver state");
        assertFalse(gameManager.wasStackReshuffled(),
                "Stack should not be reshuffled during this specific sequence");

        // --- Dealer State ---
        IDealer dealer = gameManager.getDealer();

        // Key Check: Dealer hand must be revealed at RoundOver
        assertFalse(dealer.isHiddenHand(),
                "Dealer hand must be revealed when round is over");

        IHand dealerHand = dealer.getHand();
        assertEquals(3, dealerHand.getCardAmount(),
                "Dealer should have 3 cards (10, 6, 10)");
        assertEquals(26, dealerHand.getHandValue(),
                "Dealer hand value should be 26");
        assertTrue(dealerHand.isBusted(),
                "Dealer should be busted");

        // --- Player State ---
        IPlayer player = gameManager.getPlayer();

        // 1. Credit Check (1000 start - 100 bet + 200 payout = 1100)
        assertEquals(1100.0, player.getCredit(), 0.001,
                "Credit should reflect Win (1:1 payout)");

        // 2. Flag Cleanup Checks
        // Crucial: Ensure 'In-Round' flags are turned off
        assertFalse(player.isSurrenderAvailable(), "Surrender must not be available after round end");
        // Note: The following depend on your Player implementation resetting them,
        // but typically 'Available' checks rely on GameState or Hand Status, so they should be false or irrelevant.
        // If your Player logic is strict, you might assert specific internal booleans here.

        // 3. Insurance Checks
        assertFalse(player.isInsuranceBought(), "Insurance was not bought");
        assertFalse(player.isInsuranceWon(), "Insurance was not won");
        assertEquals(0.0, player.getInsuranceBet(), 0.001, "Insurance bet should be 0");

        // --- Player Hand State ---
        List<IHand> hands = player.getHand();
        assertEquals(1, hands.size(), "Player should still have exactly 1 hand");

        IHand hand = hands.get(0);

        // Status Check
        assertEquals(ParticipantStates.Winner, hand.getStatus(),
                "Hand status should be Winner");

        // Card Content Check
        assertEquals(3, hand.getCardAmount(), "Player should have 3 cards");
        assertEquals(19, hand.getHandValue(), "Player hand value should be 19");
        assertEquals(100.0, hand.getBet(), 0.001, "Bet amount on hand should remain 100");

        // Final sanity check: Cards are what we expect
        assertEquals(Facevalues.Seven, hand.getCards().get(2).facevalue(),
                "The 3rd card should be the Seven we Hit for");
    }

    @Test
    void testPlayerSplit_BothHandsWin_FullStateAssertion() {
        MockDeck mockDeck = new MockDeck();

        // 1. Define Deck Sequence
        // Order: Dealer1, Dealer2, Player1, Player2, SplitC1, SplitC2, Hand1Hit, DealerHit
        mockDeck.setDeckSequence(
                // Initial Deal
                new Card(Suits.Clubs, Facevalues.Ten),    // Dealer Card 1 (10)
                new Card(Suits.Clubs, Facevalues.Five),   // Dealer Card 2 (5) -> Dealer Initial: 15
                new Card(Suits.Hearts, Facevalues.Eight), // Player Card 1 (8)
                new Card(Suits.Spades, Facevalues.Eight), // Player Card 2 (8) -> Player Initial: 16 (Pair)

                // Split Action (Manager pops 2 cards immediately for the new hands)
                new Card(Suits.Diamonds, Facevalues.Three), // Card for Hand 1 (8 + 3 = 11)
                new Card(Suits.Diamonds, Facevalues.King),  // Card for Hand 2 (8 + 10 = 18)

                // Hand 1 Action: Hit
                new Card(Suits.Hearts, Facevalues.Nine),    // Hand 1 hits (11 + 9 = 20)

                // Dealer Action (played after both player hands stand)
                new Card(Suits.Spades, Facevalues.Ten)      // Dealer hits (15 + 10 = 25 BUST)
        );

        // 2. Setup
        IFacevalueCalculator facevalueCalculator = new FacevalueCalculator();
        IPayoutCalculator payoutCalculator = new PayoutCalculator();
        double initialCredit = 1000.0;

        ISingePlayerGameManager gameManager = new SinglePlayerGameManager(
                new Dealer(facevalueCalculator, payoutCalculator),
                new Player("You", initialCredit, facevalueCalculator, payoutCalculator),
                mockDeck,
                new RoundResultCalculator()
        );

        double betAmount = 100.0;
        gameManager.placeBet(betAmount); // State -> WaitingForMoveSurrenderAvailable

        // ==============================================================================================
        // 3. Execution Phase
        // ==============================================================================================

        // --- Step A: Split ---
        // Player splits the 8s. A second bet of 100.0 is deducted.
        gameManager.takeAction(Actions.Split);
        // Current Status:
        // Hand 1 (OnTurn): 8 + 3 = 11
        // Hand 2 (Waiting): 8 + 10 = 18

        // --- Step B: Play Hand 1 ---
        // We hit on 11 to get 20.
        gameManager.takeAction(Actions.Hit);
        // We stand on 20. This finishes Hand 1 and moves focus to Hand 2.
        gameManager.takeAction(Actions.Stand);

        // --- Step C: Play Hand 2 ---
        // Hand 2 is now OnTurn with 18. We stand immediately.
        // This finishes Hand 2, triggers Dealer turn, and ends round.
        gameManager.takeAction(Actions.Stand);

        // ==============================================================================================
        // 4. Meticulous End-State Assertions
        // ==============================================================================================

        // --- Game Manager State ---
        assertEquals(GameState.RoundOver, gameManager.getGameState(),
                "Game must be in RoundOver state after all hands are played");
        assertFalse(gameManager.wasStackReshuffled(), "Stack should not have shuffled");

        // --- Dealer State ---
        IDealer dealer = gameManager.getDealer();
        assertFalse(dealer.isHiddenHand(), "Dealer hand must be revealed");

        IHand dealerHand = dealer.getHand();
        assertEquals(3, dealerHand.getCardAmount(), "Dealer should have 3 cards (10, 5, 10)");
        assertTrue(dealerHand.isBusted(), "Dealer should be busted (25)");

        // --- Player State ---
        IPlayer player = gameManager.getPlayer();

        // 1. Credit Check
        // Logic: 1000 (Start)
        // - 100 (Bet Hand 1) - 100 (Bet Hand 2)
        // + 200 (Win Hand 1) + 200 (Win Hand 2)
        // = 1200
        assertEquals(1200.0, player.getCredit(), 0.001,
                "Credit should reflect 2 Wins (Original Bet + Split Bet)");

        // 2. Flags
        assertFalse(player.isSurrenderAvailable(), "Surrender not available at end");
        // Note: isSplitAvailable usually returns true only if the *current* hand is a pair.
        // Since the round is over, this should be false or irrelevant, but safe to check false.
        assertFalse(player.isSplitAvailable(), "Split shouldn't be available at RoundOver");

        // --- Player Hands State ---
        List<IHand> hands = player.getHand();
        assertEquals(2, hands.size(), "Player should have exactly 2 hands after split");

        // Check Hand 1
        IHand hand1 = hands.get(0);
        assertEquals(ParticipantStates.Winner, hand1.getStatus(), "Hand 1 should be a Winner");
        assertEquals(20, hand1.getHandValue(), "Hand 1 value should be 20");
        assertEquals(3, hand1.getCardAmount(), "Hand 1 should have 3 cards (8, 3, 9)");

        // Check Hand 2
        IHand hand2 = hands.get(1);
        assertEquals(ParticipantStates.Winner, hand2.getStatus(), "Hand 2 should be a Winner");
        assertEquals(18, hand2.getHandValue(), "Hand 2 value should be 18");
        assertEquals(2, hand2.getCardAmount(), "Hand 2 should have 2 cards (8, K)");

        // Check Bets on Hands
        assertEquals(betAmount, hand1.getBet(), 0.001, "Hand 1 bet should be 100");
        assertEquals(betAmount, hand2.getBet(), 0.001, "Hand 2 bet should be 100");
    }

    @Test
    void testPlayerDoubleDown_PlayerWins_FullStateAssertion() {
        MockDeck mockDeck = new MockDeck();

        // 1. Define Deck Sequence
        // Order: Dealer1, Dealer2, Player1, Player2, DoubleDownCard, DealerHit
        mockDeck.setDeckSequence(
                // Initial Deal
                new Card(Suits.Clubs, Facevalues.Ten),     // Dealer Card 1 (10)
                new Card(Suits.Clubs, Facevalues.Five),    // Dealer Card 2 (5) -> Dealer Initial: 15
                new Card(Suits.Hearts, Facevalues.Six),    // Player Card 1 (6)
                new Card(Suits.Spades, Facevalues.Five),   // Player Card 2 (5) -> Player Initial: 11

                // Double Down Action (One card dealt, turn ends)
                new Card(Suits.Diamonds, Facevalues.King), // Player Card 3 (11 + 10 = 21)

                // Dealer Action (Dealer must play as Player finished turn)
                new Card(Suits.Spades, Facevalues.Ten)     // Dealer Card 3 (15 + 10 = 25 BUST)
        );

        // 2. Setup
        IFacevalueCalculator facevalueCalculator = new FacevalueCalculator();
        IPayoutCalculator payoutCalculator = new PayoutCalculator();
        double initialCredit = 1000.0;

        ISingePlayerGameManager gameManager = new SinglePlayerGameManager(
                new Dealer(facevalueCalculator, payoutCalculator),
                new Player("You", initialCredit, facevalueCalculator, payoutCalculator),
                mockDeck,
                new RoundResultCalculator()
        );

        double initialBet = 100.0;
        gameManager.placeBet(initialBet); // State -> WaitingForMoveSurrenderAvailable

        // ==============================================================================================
        // 3. Execution Phase
        // ==============================================================================================

        // --- Step A: Double Down ---
        // Player doubles down.
        // Logic:
        // 1. Checks if Player has enough credit (1000 - 100 [initial] = 900 available > 100 needed).
        // 2. Draws 1 Card.
        // 3. Doubles the Hand's bet.
        // 4. Forces 'Stand' (FinishedTurn).
        // 5. Game Manager detects finished turn and triggers Dealer.
        gameManager.takeAction(Actions.DoubleDown);

        // ==============================================================================================
        // 4. Meticulous End-State Assertions
        // ==============================================================================================

        // --- Game Manager State ---
        assertEquals(GameState.RoundOver, gameManager.getGameState(),
                "Game must be in RoundOver state immediately after DoubleDown");

        // --- Dealer State ---
        IDealer dealer = gameManager.getDealer();
        assertFalse(dealer.isHiddenHand(), "Dealer hand must be revealed");
        assertTrue(dealer.getHand().isBusted(), "Dealer should be busted (25)");

        // --- Player State ---
        IPlayer player = gameManager.getPlayer();

        // 1. Credit Check
        // Calculation:
        // Start: 1000
        // - 100 (Initial Bet)
        // - 100 (Double Down Bet deduction)
        // + 400 (Payout: 200 Bet + 200 Win)
        // = 1200
        assertEquals(1200.0, player.getCredit(), 0.001,
                "Credit should reflect Win on a Doubled Bet");

        // 2. Flags
        assertFalse(player.isSurrenderAvailable(), "Surrender not available");

        // --- Player Hand State ---
        List<IHand> hands = player.getHand();
        assertEquals(1, hands.size(), "Player should have 1 hand");

        IHand hand = hands.get(0);

        // Status & Value
        assertEquals(ParticipantStates.Winner, hand.getStatus(), "Hand should be Winner");
        assertEquals(21, hand.getHandValue(), "Hand value should be 21");
        assertEquals(3, hand.getCardAmount(), "Hand should have exactly 3 cards");

        // Bet Check (Crucial for Double Down)
        assertEquals(initialBet * 2, hand.getBet(), 0.001,
                "Hand bet should be doubled (200.0)");
    }

    @Test
    void testPlayerSurrender_CorrectStateAndPayout() {
        MockDeck mockDeck = new MockDeck();

        // 1. Define Deck Sequence
        // Order: Dealer1, Dealer2, Player1, Player2 (No further cards needed as round ends instantly)
        mockDeck.setDeckSequence(
                // Initial Deal
                new Card(Suits.Clubs, Facevalues.Ten),     // Dealer Card 1 (10)
                new Card(Suits.Clubs, Facevalues.Six),     // Dealer Card 2 (6) -> Dealer Initial: 16
                new Card(Suits.Hearts, Facevalues.Ten),    // Player Card 1 (10)
                new Card(Suits.Spades, Facevalues.Two)     // Player Card 2 (2) -> Player Initial: 12
        );

        // 2. Setup
        IFacevalueCalculator facevalueCalculator = new FacevalueCalculator();
        IPayoutCalculator payoutCalculator = new PayoutCalculator();
        double initialCredit = 1000.0;

        ISingePlayerGameManager gameManager = new SinglePlayerGameManager(
                new Dealer(facevalueCalculator, payoutCalculator),
                new Player("You", initialCredit, facevalueCalculator, payoutCalculator),
                mockDeck,
                new RoundResultCalculator()
        );

        double betAmount = 100.0;
        gameManager.placeBet(betAmount);

        // Check State allows surrender (WaitingForMoveSurrenderAvailable)
        assertEquals(GameState.WaitingForMoveSurrenderAvailable, gameManager.getGameState(),
                "Game must allow surrender immediately after deal");

        // ==============================================================================================
        // 3. Execution Phase
        // ==============================================================================================

        // --- Step A: Surrender ---
        // Player gives up half the bet to end the round.
        gameManager.takeAction(Actions.Surrender);

        // ==============================================================================================
        // 4. Meticulous End-State Assertions
        // ==============================================================================================

        // --- Game Manager State ---
        assertEquals(GameState.RoundOver, gameManager.getGameState(),
                "Game must be in RoundOver state immediately after Surrender");

        // --- Dealer State ---
        IDealer dealer = gameManager.getDealer();
        assertFalse(dealer.isHiddenHand(), "Dealer hand must be revealed");

        // Critical Check: Dealer should NOT have played out the hand
        // Even though Dealer has 16 (below threshold), Surrender ends the round before Dealer turn.
        assertEquals(2, dealer.getHand().getCardAmount(),
                "Dealer should still have only 2 cards");

        // --- Player State ---
        IPlayer player = gameManager.getPlayer();

        // 1. Credit Check
        // Calculation:
        // Start: 1000
        // - 100 (Initial Bet)
        // + 50 (Payout: 0.5 * Bet for Surrender)
        // = 950
        assertEquals(950.0, player.getCredit(), 0.001,
                "Credit should reflect a loss of half the bet");

        // 2. Flags
        assertFalse(player.isSurrenderAvailable(), "Surrender flag should be reset/false");

        // --- Player Hand State ---
        List<IHand> hands = player.getHand();
        assertEquals(1, hands.size(), "Player should have 1 hand");

        IHand hand = hands.get(0);

        // Status Check
        assertEquals(ParticipantStates.Surrendered, hand.getStatus(),
                "Hand status should be Surrendered");

        // Bet Check
        assertEquals(betAmount, hand.getBet(), 0.001,
                "Original bet amount remains recorded on the hand");
    }

}