package com.github.NNRIN.Components;

import com.github.NNRIN.Components.interfaces.IPlayingDeck;
import com.github.NNRIN.Components.interfaces.IRoundResultCalculator;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Helper.Actions;
import com.github.NNRIN.Helper.GameState;
import com.github.NNRIN.Helper.ParticipantStates;
import com.github.NNRIN.Participants.interfaces.IDealer;
import com.github.NNRIN.Participants.interfaces.IPlayer;

public class SinglePlayerGameManager implements ISingePlayerGameManager {
    private IDealer dealer;
    private IPlayer player;
    private IPlayingDeck playingDeck;
    private boolean wasStackReshuffled = false;
    private GameState gameState;
    private IRoundResultCalculator roundResultCalculator;

    public SinglePlayerGameManager(IDealer dealer, IPlayer player, IPlayingDeck playingDeck,
                                   IRoundResultCalculator roundResultCalculator) {
        this.dealer = dealer;
        this.player = player;
        this.playingDeck = playingDeck;
        this.roundResultCalculator = roundResultCalculator;

        initializeBettingPhase();
    }

    private void initializeBettingPhase() {
        player.getHand().get(0).setStatus(ParticipantStates.Betting);
        gameState = GameState.WaitingForBet;
    }

    private void roundStartProcedure() {
        initializePlayerActionPhase();
        dealInitialCards();
        checkToOfferInsurance();
    }

    private void initializePlayerActionPhase() {
        gameState = GameState.WaitingForMoveSurrenderAvailable;
        player.getHand().get(0).setStatus(ParticipantStates.WaitingForTurn);
    }

    private void dealInitialCards() {
        // todo: deal cards
    }

    private void checkToOfferInsurance() {
        // only offer if the dealers hand fulfills the criteria and the Player has the credits for it
        if (dealer.isInsuranceOffered() && player.isInsuranceAvailable()) {
            gameState = GameState.OfferingInsurance;
            player.getHand().get(0).setStatus(ParticipantStates.Insuring);
        }
    }

    @Override
    public void placeBet(double bet) {
        if(gameState != GameState.WaitingForBet)
            throw new RuntimeException("Can't place a bet if not in specific GameState.");
        handleBet(bet);
    }

    @Override
    public void takeAction(Actions action) {
        switch(action) {
            case Hit -> handleHit();
            case Stand -> handleStand();
            case DoubleDown -> handleDoubleDown();
            case Split -> handleSplit();
            case Surrender -> handleSurrender();
            case Insurance -> handleInsurance();
            case NoInsurance -> handleNoInsurance();
            case NextRound -> handleNextRound();
        }
    }

    private void verifyStandardAction() {
        if (gameState != GameState.WaitingForMove && gameState != GameState.WaitingForMoveSurrenderAvailable) {
            throw new RuntimeException("Invalid Action for gameState");
        }
    }

    private void setRoundOver() {
        gameState = GameState.RoundOver;
        dealer.setHiddenHand(false);
    }

    /**
     * Call this once all the players actions have been taken and his turn is thus over.
     */
    private void finishedPlayerActions() {
        //todo: payout and status setting idk
        setRoundOver();
    }
    
    private void handleHit() {
        verifyStandardAction();

    }

    private void handleStand() {
        verifyStandardAction();
        if (player.stand()) { // Stand on this hand, returns true to signal all of the players hand are dealt with.
            finishedPlayerActions();
        }else{
            gameState = GameState.WaitingForMove;     // continue waiting for moves as player has more hands (now on turn)
        }
    }

    private void handleDoubleDown() {
        verifyStandardAction();
    }

    private void handleSplit() {
        verifyStandardAction();
    }

    private void handleSurrender() {
        if(gameState != GameState.WaitingForMoveSurrenderAvailable)
            throw new RuntimeException("Can't surrender if not in specific GameState.");

    }

    private boolean hasDealerBlackJackDuringInsurance() {
        return dealer.isPrematureBlackJack();
    }

    private void triggerInsuranceIfApplicable() {
        if (player.isInsuranceBought()) {
            player.triggerInsuranceWin();
        }
    }

    private void moveOnFromInsuranceStage() {
        if(hasDealerBlackJackDuringInsurance()){
            // Dealer has BlackJack
            player.getHand().get(0).setStatus(roundResultCalculator.calculateResult(
                    player.getHand().get(0), // always only one hand for player
                    dealer.getHand())
            );
            player.payoutWinnings();
            triggerInsuranceIfApplicable();
            setRoundOver();
        }else {
            // No BlackJack we just move onto phase where player can take action
            initializePlayerActionPhase();
        }
    }

    private void handleInsurance() {
        if(gameState != GameState.OfferingInsurance)
            throw new RuntimeException("Can't take insurance if not in specific GameState.");
        player.insure();
        moveOnFromInsuranceStage();
    }

    private void handleNoInsurance() {
        if(gameState != GameState.OfferingInsurance)
            throw new RuntimeException("Can't dismiss insurance if not in specific GameState.");
        moveOnFromInsuranceStage();
    }

    private void handleBet(double bet) {
        player.setBet(bet);
        roundStartProcedure();
    }

    private void handleNextRound() {

    }

    @Override
    public IDealer getDealer() {
        return dealer;
    }

    @Override
    public IPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean wasStackReshuffled() {
        return wasStackReshuffled;
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }
}
