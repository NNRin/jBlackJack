import { CardMapping } from "./CardMapping.js";

export class Playcard {
    constructor(suit, face) {
        this.suit = suit;
        this.face = face;
    }

    render(container) {
        let className;

        if (this.suit === 'back' || this.face === 'back') {
            className = 'pcard-back'; // Assuming your CSS has a back class
        } else {
            const mapped = CardMapping.getShortCode(this.suit, this.face);
            // Result format: pcard-4d, pcard-kh, etc.
            className = `pcard-${mapped.face}${mapped.suit}`;
        }

        const newCard = document.createElement('span');
        // Add both classes: specific card class and layout class
        newCard.className = `${className} playcard`;
        container.appendChild(newCard);
    }
}