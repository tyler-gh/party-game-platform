var LobbyCountdownTimer = React.createClass({
    getInitialState: function() {
        return {time: this.props.seconds};
    },
    tick: function() {
    	this.setState({time: this.state.time - 1});
    	if (this.state.time <= 0) {
      		clearInterval(this.interval);
      		this.props.callback();
    	}
    },
    startTimer: function() {
    	this.interval = setInterval(this.tick, 1000);
  	},
  	cancelTimer: function() {
  		this.setState({time: this.props.seconds});
    	clearInterval(this.interval);
  	},
  	componentWillUnmount: function() {
    	clearInterval(this.interval);
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