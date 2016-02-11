var WaitingRoom = React.createClass({
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;

		var clickCancel = function() {
			ReactDOM.render(<GameLobby game={game} title={title} description={description}/>, document.getElementById('pg-app'));
		};

		var clickJoin = function() {
			ReactDOM.render(<GameUserJoin game={game} title={title} description={description}/>, document.getElementById('pg-app'));
		};

        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">join game</h1>
						<LobbyForm game={game} instructions="enter game code"/>
						<LobbyButton game={game} text={"submit"} handleClick={clickJoin} />
						<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});