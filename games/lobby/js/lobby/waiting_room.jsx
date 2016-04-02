var WaitingRoom = React.createClass({
    getInitialState: function () {
        var gameContainer = new GameContainer(Api, document.getElementById('pg-app'));
        gameContainer.addUserListener(this.onUserUpdate);
        gameContainer.connect();

        return {users: {}, startButtonState: "start", countdownTime: "5", intervalTimer: null};
    },
    onUserUpdate: function(users) {
        var usersKeys = Object.keys(users);
        var stateUsersKeys = Object.keys(this.state.users);

        if(usersKeys.length !== stateUsersKeys.length) {
            Api.socketSend("ws", JSON.stringify({actionType: "countdown-cancelled"}));
        }

        this.setState({users: users});
    },
    clickLeave: function () {
        Api.closeSocket("ws");
        Api.leaveGame(function () {
            window.location.href = '/';
        });
    },
    clickStart: function () {

        if (this.state.startButtonState == "start") {
            Api.socketSend("ws", JSON.stringify({actionType: "countdown-started"}));
        }
        else if (this.state.startButtonState == "cancel") {
            Api.socketSend("ws", JSON.stringify({actionType: "countdown-cancelled"}));
        }
    },
    clickStart_Remote: function (starting) {

        var countdownContainer = $("#waiting-room-start-countdown-container");
        var startButtonContainer = $("#waiting-room-start-button-container");

        if (this.state.startButtonState == "start" && starting) {
            this.setState({startButtonState: "cancel"});

            countdownContainer.removeClass("countdown-leave");
            startButtonContainer.removeClass("button-slide-left");

            countdownContainer.addClass("countdown-enter");
            startButtonContainer.addClass("button-slide-right");

            this.refs['countdown-timer'].startTimer();
        }
        else if (this.state.startButtonState == "cancel" && !starting) {
            this.setState({startButtonState: "start"});

            countdownContainer.removeClass("countdown-enter");
            startButtonContainer.removeClass("button-slide-right");

            countdownContainer.addClass("countdown-leave");
            startButtonContainer.addClass("button-slide-left");

            this.refs['countdown-timer'].cancelTimer();
        }
    },
    render: function () {
        var game = this.props.game;
        var title = this.props.title;
        var description = this.props.description;
        var gameCode = this.props.gameCode;

        var remoteCountdownClick = this.clickStart_Remote;
        $("#waiting-room-start-button").bind("remote_countdown_started", function(event, params) {
            remoteCountdownClick(true);
        });
        $("#waiting-room-start-button").bind("remote_countdown_stopped", function(event, params) {
            remoteCountdownClick(false);
        });

        var callback = function() {
            if (window.gameStart) {
                    Api.socketSend("ws", JSON.stringify({actionType: "start-game"}));
            }
        }

        return (
            <div>
                <GameBanner game={game}/>
                <div id="game-container">
                    <LobbyContainer game={game} color="color">
                        <div className="container">
                            <h1 className="waiting-room">waiting for players</h1>
                            <h3 className="waiting-room">{"game code: " + gameCode}</h3>
                            <div className="waiting-room-players">
                                <WaitingRoomPlayersTable users={this.state.users} />
                                </div>
                            <div className="pg-waiting-room-toggle">
                                <div id="waiting-room-start-countdown-container"
                                     className="waiting-room-start-countdown-container">
                                    <LobbyCountdownTimer ref="countdown-timer" game={game} seconds={5} callback={callback}/>
                                </div>
                                <div id="waiting-room-start-button-container"
                                     className="waiting-room-start-button-container">
                                    <LobbyButton id="waiting-room-start-button" game={game} text={this.state.startButtonState}
                                                 handleClick={this.clickStart} />
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

var WaitingRoomPlayersTable = React.createClass({
    render: function() {
        var users = this.props.users;

        var userRows = [];
        var usersKeys = Object.keys(users);
        for (var i=0; i < usersKeys.length; i++) {
            var userKey = usersKeys[i];
            var user = users[userKey];
            if (user !== undefined && user.name != 'root') {
                userRows.push(<WaitingRoomPlayer key={user.id} user={user}/>);
            }
        }

        return (
            <table className="pg-waiting-room-player-table">
                <tbody>
                    {userRows}
                </tbody>
            </table>
        );
    }
});

var WaitingRoomPlayer = React.createClass({
    render: function() {
        var user = this.props.user;
        return (
            <tr className={'pg-waiting-room-player ' + user.color}>
                <td><div className="player-circle"></div></td>
                <td><h2 className="player-name">{user.name}</h2></td>
            </tr>
        );
    }
});