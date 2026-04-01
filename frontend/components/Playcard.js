import { CardMapping } from "./CardMapping.js";

export class Playcard {
    constructor(suit, face) {
        this.suit = suit;
        this.face = face;
    }

    render(container) {
        let className;

        if (this.suit === 'back' || this.face === 'back') {
            className = 'pcard-back';
        } else {
            const mapped = CardMapping.getShortCode(this.suit, this.face);
            // example format for css class: pcard-4d
            className = `pcard-${mapped.face}${mapped.suit}`;
        }
        const newCard = document.createElement('span');
        newCard.className = `${className} playcard`;
        container.appendChild(newCard);
    }
}