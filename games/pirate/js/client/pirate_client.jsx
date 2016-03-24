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
    componentDidUpdate: function(oldProps, oldState) {
        if (this.state.die != oldState.die) {
            $( "#clientDiceDisplay" ).empty();
            ReactDOM.render(<ClientDiceDisplay bid={this.state.currentBidCount} dice={this.state.die}/>, document.getElementById('clientDiceDisplay'));
        }
    },
    tookTurn: function () {
        this.setState({takingTurn: false});
    },
    render: function () {

        var body = "", display = "", bid = this.state.currentBidCount != -1, takingTurn = this.state.takingTurn;

        return (
            <div>
                <ClientHeader username={this.props.userInfo.name}/>
                <div id="clientDiceDisplay"></div>
                <ClientActionPanel bid={this.state.currentBidCount} api={this.props.api} takingTurn={takingTurn} tookTurn={this.tookTurn}/>
                <BidDisplay bidCount={this.state.currentBidCount} bidNumber={this.state.currentBidNumber} />
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
