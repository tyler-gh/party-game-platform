var WaitingRoom = React.createClass({
    getInitialState: function () {
        Api.connectSocket("ws", function() {
            Api.socketSend("ws", JSON.stringify({actionType:"hi guys"}));
        },function(){},function(event){
            var data = JSON.parse(event.data);
			var users = jQuery.extend(true, {}, this.state.users);

            if(data.actionType == "client-joined") {
                users[data.client.id] = data.client.name;
                this.setState({users: users});
            } else if(data.actionType == "client-left") {
				users[data.client.id] = undefined;
				this.setState({users: users});
			}
		}.bind(this),function(event){});
        return {users: {}, startButtonState: "start"};
    },
	clickLeave: function() {
		Api.closeSocket("ws");
		Api.leaveGame(function(){
			window.location.href = '/';
		});
		//ReactDOM.render(<GameUserJoin game={this.props.game} title={this.props.title} description={this.props.description} gameCode={this.props.gameCode} />, document.getElementById('pg-app'));
	},
	clickStart: function() {

		var countdownContainer = $("#waiting-room-start-countdown-container");
		var startButtonContainer = $("#waiting-room-start-button-container");

		if (this.state.startButtonState == "start") {
			this.setState({startButtonState: "cancel"});

			countdownContainer.removeClass("countdown-leave")
			startButtonContainer.removeClass("button-slide-left")

			countdownContainer.addClass("countdown-enter")
			startButtonContainer.addClass("button-slide-right");

		}
		else if (this.state.startButtonState == "cancel") {
			this.setState({startButtonState: "start"});

			countdownContainer.removeClass("countdown-enter")
			startButtonContainer.removeClass("button-slide-right")

			countdownContainer.addClass("countdown-leave")
			startButtonContainer.addClass("button-slide-left")
		}
	},
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;
		var gameCode = this.props.gameCode;

        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="waiting-room">waiting for players</h1>
						<h3 className="waiting-room">{"game code: " + gameCode}</h3>
						{JSON.stringify(this.state.users)}
						<div className="waiting-room-players"></div>
						<div className="pg-waiting-room-toggle">
							<div id="waiting-room-start-countdown-container" className="waiting-room-start-countdown-container">
								<LobbyCountdownTimer game={game}/>
							</div>
							<div id="waiting-room-start-button-container"className="waiting-room-start-button-container">
								<LobbyButton game={game} text={this.state.startButtonState} handleClick={this.clickStart} />
							</div>
						</div>
						<LobbyButton game={game} hollow="color" text={"leave"} handleClick={this.clickLeave} />
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});