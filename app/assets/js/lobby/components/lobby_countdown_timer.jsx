var LobbyCountdownTimer = React.createClass({
    
    getInitialState: function() {
        return {time: 5};
    },

    render: function() {
    	var game = this.props.game;
        return (
	        <div className={"pg-lobby-countdown-timer-" + game}>
	        	<div className={"pg-lobby-countdown-label"}>starting in</div>
				<div className={"pg-lobby-countdown-timer-border"}>
					<div className={"pg-lobby-countdown-timer-time"}>
						<h2>{this.state.time}</h2>
					</div>
				</div>
	        </div>
	    );
    }
});