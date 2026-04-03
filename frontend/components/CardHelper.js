import {Playcard} from "./Playcard.js";

export const CardHelper = {
    getHandValue(hand) {
        let cards = hand.cards.map(c => new Playcard(c.suit, c.faceValue));

        let aceCount = cards.filter(c => c.getValue() === 11).length;

        let handValue = cards.reduce((acc, curr) => acc + curr.getValue(), 0);
        while (aceCount > 0 && handValue > 21) {
            handValue -= 10;
            aceCount--;
        }
        return handValue;
    },
};