package com.github.NNRIN.Components.interfaces;

import com.github.NNRIN.Helper.Actions;
import com.github.NNRIN.Helper.GameState;
import com.github.NNRIN.Participants.interfaces.IDealer;
import com.github.NNRIN.Participants.interfaces.IPlayer;

public interface ISingePlayerGameManager {
    void takeAction(Actions action);

    IDealer getDealer();

    IPlayer getPlayer();

    /**
     * When the Cut card if found in the Stack the business logic 'reshuffles' the whole Stack. The program using the
     * gameManager gets this information over this method.
     * @return
     */
    boolean wasStackReshuffled();

    void placeBet(double bet);

    GameState getGameState();

    String getId();

    /**
     * Returns the current ISinglePlayerGameManager object but with all information removed that the public / player
     * should not be able to know. Meaning information like the hiddenCard of the dealer is removed in the public
     * version even though it is present in the real GameManager object.
     * @return
     */
    ISingePlayerGameManager getPublicGameManager();
}
