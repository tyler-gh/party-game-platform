var ApiActionListener = function(app) {
    this.app = app;
};
ApiActionListener.prototype.getActionHandler = function() {
    return function (action) {
        console.log(action);
        switch (action.actionType) {
            case "prompt-roll":
                //TODO tell client to roll on UI

                //TODO move this function to roll button
                Api.socketSend("ws", JSON.stringify({actionType: "dice-rolled"}));
                break;
            case "rolling-finished":
                //TODO not sure if this will do anything on the normal clients
                //this just says the bidding part of the round has begun
                break;
            case "new-die":
                this.app.setState({die: action.data.die});
                break;
            case "new-turn":
                //name is to be used in UI
                //just says whos turn it is
                //TODO the data sent will likely change. please change as needed
                var name = action.data.client_name
                break;
            case "prompt-turn":
                this.app.setState({takingTurn: true});
                break;
            case "invalid-bid":
                this.app.setState({takingTurn: true});
                break;
            case "no-bid":
                this.app.setState({takingTurn: true});
                break;
            case "new-bid":
                this.app.setState({
                    currentBidCount: action.data.bid.dieCount,
                    currentBidNumber: action.data.bid.dieNumber
                });
                break;
            case "reveal-dice":
                //TODO this will be the call to tell the clients we are going to start showing off the dice
                //the main screen will be the one telling the server that "dice-revealed"
                //when it is done showing the dice
                break;
            case "lost-die":
                this.app.setState({currentBidCount: -1, currentBidNumber: -1});
                break;
            case "game-winner":
                //TODO fill in what happens when the game finishes
                break;
        }
    }.bind(this);
};

var App = React.createClass({
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
        var divStyle = {
            padding: "50px"
        };
        var headerStyle = {
            marginTop: "10px",
            marginBottom: "30px"
        };

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
            <div style={divStyle}>
                <h1 style={headerStyle}>Pirate's Dice</h1>
                <DiceDisplay bid={bid} dice={this.state.die}/>
                {display}
                {body}
            </div>
        );
    }
});

window.gameStart = function (dom, api, users) {
    ReactDOM.render(
        <App api={api} users={users}/>,
        dom
    );
};
