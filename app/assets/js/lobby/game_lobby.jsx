var GameLobby = React.createClass({
    render: function() {
    	var game = this.props.game;
        var title = this.props.title;
        return (
	        <div className={"game-lobby-" + game}>
	        	<GameIcon game={game} color={"white"}/>
	        </div>
	    );
    }
});
