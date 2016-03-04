var WaitingRoom = React.createClass({
    getInitialState: function () {
        var gameContainer = new GameContainer(Api, document.getElementById('pg-app'));
        gameContainer.addUserListener(this.onUserUpdate);
        gameContainer.connect();
        return {users: {}, startButtonState: "start"};
    },
    onUserUpdate: function(users) {
        this.setState({users: users});
    },
    clickLeave: function () {
        Api.closeSocket("ws");
        Api.leaveGame(function () {
            window.location.href = '/';
        });
    },
    clickStart: function () {

        var countdownContainer = $("#waiting-room-start-countdown-container");
        var startButtonContainer = $("#waiting-room-start-button-container");

        if (this.state.startButtonState == "start") {
            this.setState({startButtonState: "cancel"});

            countdownContainer.removeClass("countdown-leave");
            startButtonContainer.removeClass("button-slide-left");

            countdownContainer.addClass("countdown-enter");
            startButtonContainer.addClass("button-slide-right");

        }
        else if (this.state.startButtonState == "cancel") {
            this.setState({startButtonState: "start"});

            countdownContainer.removeClass("countdown-enter");
            startButtonContainer.removeClass("button-slide-right");

            countdownContainer.addClass("countdown-leave");
            startButtonContainer.addClass("button-slide-left")
        }

        if (window.gameStart) {
            Api.socketSend("ws", JSON.stringify({actionType: "start-game"}));
        }
    },
    render: function () {
        var game = this.props.game;
        var title = this.props.title;
        var description = this.props.description;
        var gameCode = this.props.gameCode;

        return (
            <div>
                <GameBanner game={game}/>
                <div id="game-container">
                    <LobbyContainer game={game} color="color">
                        <div className="container">
                            <h1 className="waiting-room">waiting for players</h1>
                            <h3 className="waiting-room">{"game code: " + gameCode}</h3>
                            {JSON.stringify(this.state.users)}
                            <div className="waiting-room-players"></div>
                            <div className="pg-waiting-room-toggle">
                                <div id="waiting-room-start-countdown-container"
                                     className="waiting-room-start-countdown-container">
                                    <LobbyCountdownTimer game={game}/>
                                </div>
                                <div id="waiting-room-start-button-container"
                                     className="waiting-room-start-button-container">
                                    <LobbyButton game={game} text={this.state.startButtonState}
                                                 handleClick={this.clickStart}/>
                                </div>
                            </div>
                            <LobbyButton game={game} hollow="color" text={"leave"} handleClick={this.clickLeave}/>
                        </div>
                    </LobbyContainer>
                </div>
            </div>
        );
    }
});