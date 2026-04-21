package com.github.nnrin.blackjackweb.Singleplayer.web.DTOs;

import java.util.List;

public record PlayerDTO(
        String name,
        double credit,
        boolean isSurrenderAvailable,
        boolean isInsuranceBought,
        double insuranceBet,
        boolean isInsuranceWon,
        boolean isSplitAvailable,
        boolean isDoubleDownAvailable,
        List<HandDTO> hands
) {
}
