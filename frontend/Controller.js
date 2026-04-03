export default class Controller {
    constructor(model, view) {
        this.model = model;
        this.view = view;

        // model Bindings
        this.model.bindRenderDealerCards(data => this.view.renderDealerCards(data));
        this.model.bindRenderPlayerHands(hands => this.view.renderPlayerHands(hands));
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

        this.view.bindInsurance((buy) => {
            this.model.sendAction(buy ? 'Insurance' : 'NoInsurance');
        });
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
            this.view.showMessage("enter a valid bet");
        }
    }
}