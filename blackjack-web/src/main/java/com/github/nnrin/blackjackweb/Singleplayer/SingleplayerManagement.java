package com.github.nnrin.blackjackweb.Singleplayer;

import com.github.NNRIN.BlackJack;
import com.github.NNRIN.Components.SinglePlayerGameManager;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Helper.Actions;
import com.github.nnrin.blackjackweb.Singleplayer.web.DTOs.ActionDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SingleplayerManagement {

    private final Map<String, ISingePlayerGameManager> activeGames = new ConcurrentHashMap<>();

    public ISingePlayerGameManager createGame() {
        ISingePlayerGameManager gameManager =  BlackJack.getSingleplayerGame(UUID.randomUUID().toString());
        saveGameState(gameManager.getId(), gameManager);
        return gameManager;
    }

    public ISingePlayerGameManager takeActionOnGame(Actions action, String id) {
        ISingePlayerGameManager gameManager = getGameById(id);
        gameManager.takeAction(action);
        return gameManager;
    }

    public ISingePlayerGameManager placeBet(double bet, String id) {
        ISingePlayerGameManager gameManager = getGameById(id);
        gameManager.placeBet(bet);
        return gameManager;
    }

    private ISingePlayerGameManager getGameById(String id) {
        return activeGames.get(id);
    }

    private void saveGameState(String id, ISingePlayerGameManager gameState) {
        activeGames.put(id, gameState);
    }

}
