var LobbyForm = React.createClass({
	
	getInitialState: function() {
		return {value: this.props.instructions};
	},
	handleChange: function(event) {
		this.setState({value: event.target.value});
	},
	handleFocus: function(event) {
		
		//display persistent instructions on focus
		var persistentInstructions = $('#pg-lobby-form-persistent-instructions');
		persistentInstructions.removeClass("pg-lobby-form-persistent-instructions-fade-out");
		persistentInstructions.addClass("pg-lobby-form-persistent-instructions-fade-in");
		persistentInstructions.css("visibility","initial");
		
		//changes color to match theme as the text is inputted
		$('#pg-lobby-form-input').removeClass("pg-lobby-form-input-gray");

		if (this.state.value == this.props.instructions) {
			this.setState({value: ""});
		}
	},
	handleBlur: function(event) {
		if (this.state.value == "") {
			
			//if form is not filled out, hide the instructions
			var persistentInstructions = $('#pg-lobby-form-persistent-instructions');
			persistentInstructions.removeClass("pg-lobby-form-persistent-instructions-fade-in");
			persistentInstructions.addClass("pg-lobby-form-persistent-instructions-fade-out");
			
			//return text color to gray as it is not filled out
			$('#pg-lobby-form-input').addClass("pg-lobby-form-input-gray");

			//display instructions again
			this.setState({value: this.props.instructions});
		}
	},
	render: function() {
		var game = this.props.game;
		var value = this.state.value;
		
		return (
			<div className={"pg-lobby-form-" + game}>
				<div id="pg-lobby-form-persistent-instructions" className={"pg-lobby-form-persistent-instructions"}>
					{this.props.instructions}
				</div> 
				<input id="pg-lobby-form-input" className={"pg-lobby-form-input pg-lobby-form-input-gray"} type="text" value={value} onChange={this.handleChange} onFocus={this.handleFocus} onBlur={this.handleBlur}/>
			</div>
		);
	}

});