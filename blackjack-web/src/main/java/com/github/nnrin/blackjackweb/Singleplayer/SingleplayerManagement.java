package com.github.nnrin.blackjackweb.Singleplayer;

import com.github.NNRIN.BlackJack;
import com.github.NNRIN.Components.SinglePlayerGameManager;
import com.github.NNRIN.Components.interfaces.ISingePlayerGameManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SingleplayerManagement {

    public ISingePlayerGameManager createGame() {
        return BlackJack.getSingleplayerGame(UUID.randomUUID().toString());
    }

}
