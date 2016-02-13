var WaitingRoom = React.createClass({
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;
		var gameCode = this.props.gameCode;

		var clickLeave = function() {
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
						<LobbyButton game={game} text={"start"} handleClick={clickStart} />
						<LobbyButton game={game} hollow="color" text={"leave"} handleClick={clickLeave} />
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});