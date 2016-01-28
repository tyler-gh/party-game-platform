var GameBanner = React.createClass({
   render: function() {
	   var game = this.props.game;
	   return (
		   <div className="banner">
			   <GameIcon game={game} color={"white"}/>
		   </div>
	   );
   }
});

var GameLobby = React.createClass({
    render: function() {
    	var game = this.props.game;
        var title = this.props.title;
        return (
	        <div className={"game-lobby-" + game}>
				<GameBanner game={game} />
				<div className="container">
					<h1 className="create-game">create game</h1>
					<h2 className="enter">enter your name</h2>
				</div>
	        </div>
	    );
    }
});
