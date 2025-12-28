export default class Controller {
    constructor(model, view) {
        this.model = model;
        this.view = view;

        // Model Bindings
        this.model.bindRenderDealerCards(cards => this.view.renderDealerCards(cards));
        this.model.bindRenderPlayerHands(hands => this.view.renderPlayerHands(hands)); // Updated
        this.model.bindUpdateGameInfo(info => this.view.renderGameInfo(info));
        this.model.bindShowMessage(msg => this.view.showMessage(msg));

        // View Bindings
        this.view.bindStart(this.handleStart);
        this.view.bindBet(this.handleBet);

        // Actions
        this.view.bindHit(() => this.model.sendAction('Hit'));
        this.view.bindStand(() => this.model.sendAction('Stand'));
        this.view.bindDouble(() => this.model.sendAction('DoubleDown'));
        this.view.bindSplit(() => this.model.sendAction('Split'));
        this.view.bindSurrender(() => this.model.sendAction('Surrender'));

        // Insurance: true = Insurance, false = NoInsurance
        this.view.bindInsurance((buy) => {
            this.model.sendAction(buy ? 'Insurance' : 'NoInsurance');
        });

        // Next Round
        this.view.bindNextRound(() => this.model.sendAction('NextRound'));
    }

    handleStart = () => {
        console.log("start")
        this.model.createGame();
    }

    handleBet = (amount) => {
        if(amount && amount > 0) {
            this.model.placeBet(amount);
        } else {
            this.view.showMessage("Please enter a valid bet!");
        }
    }
}