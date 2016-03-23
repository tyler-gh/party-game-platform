var ApiActionListener = function(app) {
    this.app = app;
};
ApiActionListener.prototype.getActionHandler = function() {
    return function (action) {
        console.log(action);
        switch (action.actionType) {
            case "new-die":
                this.app.setState({die: action.data.die});
                break;
            case "prompt-turn":
                this.app.setState({takingTurn: true});
                break;
            case "lost-die":
                this.app.setState({currentBidCount: -1, currentBidNumber: -1});
                break;
            case "new-bid":
                this.app.setState({
                    currentBidCount: action.data.bid.dieCount,
                    currentBidNumber: action.data.bid.dieNumber
                });
                break;
            case "invalid-bid":
                this.app.setState({takingTurn: true});
                break;
        }
    }.bind(this);
};

var PirateClient = React.createClass({
    getInitialState: function () {
        var al = new ApiActionListener(this);
        this.props.api.addActionListener(al.getActionHandler());
        return {
            die: [],
            takingTurn: false,
            currentBidCount: -1,
            currentBidNumber: -1
        };
    },
    tookTurn: function () {
        this.setState({takingTurn: false});
    },
    render: function () {

        var body = "", display = "", bid = this.state.currentBidCount != -1, takingTurn = this.state.takingTurn;

        if (bid) {
            display = <BidDisplay bidCount={this.state.currentBidCount} bidNumber={this.state.currentBidNumber} />;
        }

        if (takingTurn) {
            if(bid) {
                body = <div>
                    <LiarButton onSubmit={this.tookTurn} api={this.props.api}/>
                    <BidForm onSubmit={this.tookTurn} api={this.props.api}/>
                </div>;
            } else {
                body = <BidForm onSubmit={this.tookTurn} api={this.props.api}/>
            }
        }


        return (
            <div>
                <ClientHeader username={"Jackson"}/>
                <ClientDiceDisplay bid={bid} dice={this.state.die}/>
                {display}
                {body}
            </div>
        );
    }
});

window.gameStart = function (dom, api, users) {
    ReactDOM.render(
        <PirateClient api={api} users={users}/>,
        dom
    );
};
