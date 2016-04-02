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
                this.app.setState({needToRoll: true});

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

var PirateClient = React.createClass({
    getInitialState: function () {
        var al = new ApiActionListener(this);
        this.props.api.addActionListener(al.getActionHandler());
        return {
            die: [],
            takingTurn: false,
            currentBidCount: -1,
            currentBidNumber: -1,
            needToRoll: false
        };
    },
    componentDidUpdate: function(oldProps, oldState) {
        if (this.state.die != oldState.die) {
            $( "#clientDiceDisplay" ).empty();
            ReactDOM.render(<ClientDiceDisplay bid={this.state.currentBidCount} dice={this.state.die}/>, document.getElementById('clientDiceDisplay'));
        }
        
        $( "#clientFooter" ).empty();
        ReactDOM.render(<ClientFooter bidCount={this.state.currentBidCount} bidNumber={this.state.currentBidNumber} />, document.getElementById('clientFooter'));
        
    },
    tookTurn: function () {
        this.setState({takingTurn: false});
    },
    rolledDice: function () {
        this.setState({needToRoll: false});
    },
    render: function () {

        var body = "", display = "", bid = this.state.currentBidCount != -1, takingTurn = this.state.takingTurn;

        return (
            <div>
                <ClientHeader username={this.props.userInfo.name}/>
                <div id="clientDiceDisplay"></div>
                <ClientActionPanel bid={this.state.currentBidCount} api={this.props.api} takingTurn={takingTurn} tookTurn={this.tookTurn}/>
                <RollForm needToRoll={this.state.needToRoll} rolledDice={this.rolledDice}/>
                <div id="clientFooter"></div>
            </div>
        );
    }
});

window.gameStart = function (dom, api, users, userInfo) {
    ReactDOM.render(
        <PirateClient api={api} users={users} userInfo={userInfo}/>,
        dom
    );
};
