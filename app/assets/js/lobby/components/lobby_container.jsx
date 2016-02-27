var LobbyContainer = React.createClass({
    render: function() {
		var game = this.props.game;
        return (
	        <div className={"game-lobby-" + game} id={"game-lobby"}>
				{this.props.children}
	        </div>
	    );
    }
});
