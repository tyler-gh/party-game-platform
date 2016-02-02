var LobbyContainer = React.createClass({
    render: function() {
        return (
	        <div className="game-lobby">
				{this.props.children}
	        </div>
	    );
    }
});
