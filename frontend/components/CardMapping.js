export const CardMapping = {
    suits: {
        'Diamonds': 'd',
        'Hearts': 'h',
        'Clubs': 'c',
        'Spades': 's'
    },
    faces: {
        'Two': '2', 'Three': '3', 'Four': '4', 'Five': '5',
        'Six': '6', 'Seven': '7', 'Eight': '8', 'Nine': '9',
        'Ten': '10', 'Jack': 'j', 'Queen': 'q', 'King': 'k', 'Ace': 'a'
    },
    getShortCode(suit, face) {
        if (suit === 'back') return 'back';

        // map short codes
        const s = this.suits[suit] || suit;
        const f = this.faces[face] || face;
        return { suit: s, face: f };
    }
};