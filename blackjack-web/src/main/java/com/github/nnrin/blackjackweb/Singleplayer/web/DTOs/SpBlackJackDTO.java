package com.github.nnrin.blackjackweb.Singleplayer.web.DTOs;

import com.github.NNRIN.Helper.GameState;

public record SpBlackJackDTO(
        String id,
        boolean wasStackReshuffled,
        String gameState, // enum
        DealerDTO dealer,
        PlayerDTO player
) { }


