var GameBanner = React.createClass({
   render: function() {
	   var game = this.props.game;
	   return (
		   <div className="banner">
			   <GameIcon game={game} color={"white"} size="small"/>
		   </div>
	   );
   }
});

var GameCreator = React.createClass({
    render: function() {
    	var game = this.props.game;
        return (
	        <div className={"game-lobby-" + game}>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">create game</h1>
						<h2 className="enter">enter your name</h2>
						<LobbyButton game={game} icon={"create"} text={"create"}/>
						<br/>
						<br/>
						<LobbyButton game={game} icon={"join"} text={"join"}/>
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});
