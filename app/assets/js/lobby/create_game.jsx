var GameBanner = React.createClass({
	render: function() {
		var game = this.props.game;
		return (
			<div className={"lobby-banner-" + game}>
				<GameIcon game={game} color={"white"} size="small"/>
			</div>
		);
	}
});

var GameCreator = React.createClass({
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;

		var clickCancel = function() {
			ReactDOM.render(<GameLobby game={game} title={title} description={description} />, document.getElementById('pg-app'));
		};

		var clickCreate = function() {
			ReactDOM.render(<GameCreatedSuccess game={game} title={title} />, document.getElementById('pg-app'));
		};


        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">create game</h1>
						<LobbyForm game={game} instructions="enter your name"/>
						<h3 className="create-game">select your color</h3>
						<LobbyButton game={game} text={"create"} handleClick={clickCreate} />
						<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});

var GameCreatedSuccess = React.createClass({
	render: function() {
		var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;

		var clickCancel = function() {
			ReactDOM.render(<GameLobby game={game} title={title} description={description} />, document.getElementById('pg-app'));
		};

		var gameCode = "475YZ";

		return (
			<div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="success">success!</h1>
						<h3 className="success">{"Your " + title + " game was successfully created.  Give friends access with the following code:"}</h3>
						<h1 className="game-code">{gameCode}</h1>
					</div>
				</LobbyContainer>
			</div>
		);
	}
});