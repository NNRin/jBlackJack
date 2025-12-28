package com.github.NNRIN.Helper;

import com.github.NNRIN.Components.interfaces.IPlayingDeck;
import com.github.NNRIN.Components.interfaces.IRoundResultCalculator;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Participants.interfaces.IDealer;
import com.github.NNRIN.Participants.interfaces.IPlayer;

/**
 * Helper class that can be used to return the current gameState that should be known to the public, applicable when
 * Dealers hand should still be partialyl Hidden.
 */
public class SinglePlayerGameManagerPublic implements ISingePlayerGameManager {
    private IDealer dealer;
    private IPlayer player;
    private boolean wasStackReshuffled = false;
    private GameState gameState;
    private String id;

    public SinglePlayerGameManagerPublic(IDealer dealer, IPlayer player, boolean wasStackReshuffled, GameState gameState, String id) {
        this.dealer = dealer;
        this.player = player;
        this.wasStackReshuffled = wasStackReshuffled;
        this.gameState = gameState;
        this.id = id;
    }

    @Override
    public void takeAction(Actions action) {
        throw new RuntimeException("This is just a data class");
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
    public void placeBet(double bet) {
        throw new RuntimeException("This is just a data class");
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ISingePlayerGameManager getPublicGameManager() {
        return this;
    }
}
