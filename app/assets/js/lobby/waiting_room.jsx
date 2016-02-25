var WaitingRoom = React.createClass({
    getInitialState: function () {
        Api.connectSocket("ws", function() {
            Api.socketSend("ws", JSON.stringify({actionType:"hi guys"}));
        },function(){},function(event){
            var data = JSON.parse(event.data);

            if(data.actionType == "new-client" || data.actionType == "client-rejoined") {
                var users = jQuery.extend(true, {}, this.state.users);
                users[data.client.id] = data.client.name;
                this.setState({users: users});
            }
		}.bind(this),function(event){});
        return {users: {}};
    },
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;
		var gameCode = this.props.gameCode;

		var clickLeave = function() {
            Api.closeSocket("ws");
			ReactDOM.render(<GameUserJoin game={game} title={title} description={description} gameCode={gameCode}/>, document.getElementById('pg-app'));
		};

		var clickStart = function() {
			//ReactDOM.render(<GameUserJoin game={game} title={title} description={description}/>, document.getElementById('pg-app'));
		};

        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="waiting-room">waiting for players</h1>
						<h3 className="waiting-room">{"game code: " + gameCode}</h3>
						{JSON.stringify(this.state.users)}
						<LobbyButton game={game} text={"start"} handleClick={clickStart} />
						<LobbyButton game={game} hollow="color" text={"leave"} handleClick={clickLeave} />
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});