var LobbyContainer = React.createClass({
    render: function() {
		var game = this.props.game;
        return (
	        <div className={"game-lobby-" + game}>
				{this.props.children}
	        </div>
	    );
    }
});
