package com.github.nnrin.blackjackweb.Singleplayer.web.DTOs;

public record DealerDTO(
        boolean isHiddenHand,
        HandDTO hand
) {
}
