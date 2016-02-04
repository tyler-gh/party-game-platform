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

        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">create game</h1>
						<h3 className="create-game">enter your name</h3>
						<LobbyForm game={game} initialValue={"name"} />
						<h3 className="create-game">select your color</h3>
						<LobbyButton game={game} text={"create"}/>
						<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});
