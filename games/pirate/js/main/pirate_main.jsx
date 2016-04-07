var ApiActionListener = function(app) {
    this.app = app;
};
ApiActionListener.prototype.getActionHandler = function(me) {
    return function (action) {

        console.log(action);

        //these are to print on the screen
        me.state.actions.push(action);
        me.setState({actions: me.state.actions});


        switch (action.actionType) {
            case "rolling-finished":
                //TODO  "rolling-finished" means all clients have rolled
                //this just says the bidding part of the round has begun
                break;
            case "new-turn":
                //name is to be used in UI
                //just says whos turn it is
                //TODO the data sent will likely change. please change as needed
                var name = action.data.client_name
                break;
            case "client-rolled":
            //TODO Server has added a unique to main screen broadcast called - "client-rolled"
            // that will have which client rolled.
            // Show the any UI change needed to show that user has finished rolling
                break;
            case "reveal-dice":
                //TODO this will be the call to tell the clients we are going to start showing off the dice
                //the main screen will be the one telling the server that "dice-revealed"
                //when it is done showing the dice

                //TODO move this call to a spot after the main screen is ready to continue the game
                Api.socketSend("ws", JSON.stringify({actionType: "dice-revealed"}));
                break;
            case "game-winner":
                //TODO fill in what happens when the game finishes
                break;
        }
    }.bind(this);
};

var PirateApp = React.createClass({
    getInitialState: function () {
        var me = this;
        var al = new ApiActionListener(this);
        this.props.api.addActionListener(al.getActionHandler(me));

        //this.props.api.addActionListener(function (action) {

        //});
        return {
            actions: []
        };
    },
    render: function () {
        return (
            <div>
                <h1>{JSON.stringify(this.props.users)}</h1>
                {this.state.actions.map(function (action) {
                    return <h6>{JSON.stringify(action)}</h6>;
                })}
            </div>

        );
    }
});

window.gameStart = function (dom, api, users) {
    ReactDOM.render(
        <PirateApp api={api} users={users}/>,
        dom
    );
};


