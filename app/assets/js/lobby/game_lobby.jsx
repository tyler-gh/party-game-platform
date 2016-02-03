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

var GameLobby = React.createClass({
    render: function() {
    	var game = this.props.game;
        return (
			<BackgroundColor game={game}>
	        	<LobbyContainer game={game} color="color">
					<GameIcon size="large"  game={game} color="white"/>
					<IconButton game={game} icon={"create"} text={"create"}/>
					<IconButton game={game} icon={"join"} text={"join"}/>
					<div></div>
					<button className="pg-lobby-hollow-button-white">cancel</button>
	        	</LobbyContainer>
			</BackgroundColor>
	    );
    }
});
