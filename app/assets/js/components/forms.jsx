var LobbyForm = React.createClass({
	
	getInitialState: function() {
		return {value: this.props.initialValue};
	},
	handleChange: function(event) {
		this.setState({value: event.target.value});
	},
	render: function() {
		var game = this.props.game;
		var value = this.state.value;
		return <input className={"pg-lobby-form-" + game} type="text" value={value} onChange={this.handleChange} />;
	}

});