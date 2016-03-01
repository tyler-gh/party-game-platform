var ColorPicker = React.createClass({
   
    getInitialState: function() {
        return {
            selected: false,
            selected_id: ""
        };
    },
	
	onChildClick: function(button_id, selected) {
		if (selected) {
	        
	        $("#cp-button-" + this.state.selected_id + "-splash").addClass('pg-lobby-color-picker-button-splash-out');
  			$("#cp-button-" + this.state.selected_id + "-highlight").addClass('pg-lobby-color-picker-button-highlight-out');

	        $("#cp-button-" + this.state.selected_id + "-splash").removeClass('pg-lobby-color-picker-button-splash');
  			$("#cp-button-" + this.state.selected_id + "-highlight").removeClass('pg-lobby-color-picker-button-highlight');

	        this.setState({
	            selected: true,
	            selected_id: button_id
	        });

	        $("#cp-button-" + button_id + "-splash").removeClass('pg-lobby-color-picker-button-splash-out');
  			$("#cp-button-" + button_id + "-highlight").removeClass('pg-lobby-color-picker-button-highlight-out');

	        $("#cp-button-" + button_id + "-splash").addClass('pg-lobby-color-picker-button-splash');
  			$("#cp-button-" + button_id + "-highlight").addClass('pg-lobby-color-picker-button-highlight');
        }
        else {
	        this.setState({
	            selected: false
	        });
        }

  //       var buttons = $(".color-picker-button").map(function() {
		//     return this.innerHTML;
		// }).get();

  //       $.each(buttons, function() {
  //       	debugger
  //       })
    },
    
    buildButton: function(id, color) {
        return <ColorPickerButton id={id} selected={this.state.selected === id} parentHandleClick={this.onChildClick} color={color}/>
    },

  	render: function() {

		var game = this.props.game;

		var children = this.props.children;

        return (
	        <div className="pg-lobby-color-picker">
				<h2 className="pg-lobby-color-picker-header">select your color</h2>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color1-button", "color1")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color2-button", "color2")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color3-button", "color3")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color4-button", "color4")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color5-button", "color5")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color6-button", "color6")}
				</div>

				<br/>

				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color7-button", "color7")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color8-button", "color8")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color9-button", "color9")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color10-button", "color10")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color11-button", "color11")}
				</div>
				<div className="pg-lobby-color-picker-button-container">
					{this.buildButton("color12-button", "color12")}
				</div>
	        </div>
	    );
    }
});


var ColorPickerButton = React.createClass({
	
  	handleClick: function(event) {
  		this.props.parentHandleClick(this.props.id, !this.props.selected);
  	},

	render: function() {
		var id = "cp-button-" + this.props.color + "-button";
		var className = "pg-lobby-color-picker-button-" + this.props.color;

		return (
			<div className={"color-picker-button"}>
				<div id={id} className={className} onClick={this.handleClick}>
					<div id={id+"-splash"}>
						<div id={id+"-highlight"}></div>
					</div>
				</div>
			</div>
		);
	}
});