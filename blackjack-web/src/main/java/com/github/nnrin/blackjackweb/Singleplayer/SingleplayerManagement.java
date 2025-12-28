package com.github.nnrin.blackjackweb.Singleplayer;

import com.github.NNRIN.BlackJack;
import com.github.NNRIN.Components.SinglePlayerGameManager;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import com.github.NNRIN.Helper.Actions;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.nnrin.blackjackweb.Singleplayer.web.DTOs.ActionDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class SingleplayerManagement {

    private Cache<String, ISingePlayerGameManager> gameCache = Caffeine.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public ISingePlayerGameManager createGame() {
        ISingePlayerGameManager gameManager =  BlackJack.getSingleplayerGame(UUID.randomUUID().toString());
        saveGameState(gameManager.getId(), gameManager);
        return gameManager.getPublicGameManager();
    }

    public ISingePlayerGameManager takeActionOnGame(Actions action, String id) {
        ISingePlayerGameManager gameManager = getGameById(id);
        gameManager.takeAction(action);
        return gameManager.getPublicGameManager();
    }

    public ISingePlayerGameManager placeBet(double bet, String id) {
        ISingePlayerGameManager gameManager = getGameById(id);
        gameManager.placeBet(bet);
        return gameManager.getPublicGameManager();
    }

    private ISingePlayerGameManager getGameById(String id) {
        return gameCache.getIfPresent(id);
    }

    private void saveGameState(String id, ISingePlayerGameManager gameState) {
        gameCache.put(id, gameState);
    }

}
