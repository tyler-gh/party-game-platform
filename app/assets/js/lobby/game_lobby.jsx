var GameLobby = React.createClass({
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;

		var clickCancel = function() {
			ReactDOM.render(<GameSelectMenu />, document.getElementById('pg-app'));
		};

		var clickJoin = function() {
			ReactDOM.render(<GameJoin game={game}/>, document.getElementById('pg-app'));
		};

		var clickCreate = function() {
			ReactDOM.render(<GameCreator game={game} title={title} description={description}/>, document.getElementById('pg-app'));
		};

        return (
			<BackgroundColor game={game}>
	        	<LobbyContainer game={game} color="color">
					<GameIcon size="large"  game={game} color="white"/>
					<h1 className="lobby">{title}</h1>
					<h3 className="lobby">{description}</h3>
					<LobbyButton game={game} icon={"create"} text={"create"} handleClick={clickCreate}/>
					<LobbyButton game={game} icon={"join"} text={"join"} handleClick={clickJoin}/>
					<LobbyButton game={game} hollow="white" text={"cancel"} handleClick={clickCancel}/>
	        	</LobbyContainer>
			</BackgroundColor>
	    );
    }
});
