var ColorPicker = React.createClass({
   
    getInitialState: function() {
        return {
            selected: false
        };
    },
	
	onChildClick: function(id, selected) {
		if (selected) {
	        this.setState({
	            selected: id
	        });
        }
        else {
	        this.setState({
	            selected: false
	        });
        }
    },
    
    buildButton: function(id, color) {
        return <ColorPickerButton
            id={id}
            selected={this.state.selected === id}
            parentHandleClick={this.onChildClick}
            color={color} />
    },

  	render: function() {

		var game = this.props.game;

		var children = this.props.children;

        return (
	        <div className="color-picker">
				<h3 className="create-game">select your color</h3>

				{this.buildButton("color1-button", "color1")}
				{this.buildButton("color2-button", "color2")}
				{this.buildButton("color3-button", "color3")}
				{this.buildButton("color4-button", "color4")}
				{this.buildButton("color5-button", "color5")}
				{this.buildButton("color6-button", "color6")}
				{this.buildButton("color7-button", "color7")}
				{this.buildButton("color8-button", "color8")}
				{this.buildButton("color9-button", "color9")}
				{this.buildButton("color10-button", "color10")}
				{this.buildButton("color11-button", "color11")}
				{this.buildButton("color12-button", "color12")}

	        	{this.props.children}

	        </div>
	    );
    }
});


var ColorPickerButton = React.createClass({
	
  	handleClick: function(event) {
  		this.props.parentHandleClick(this.props.id, !this.props.selected);
  	},

	render: function() {
		var color = this.props.color;

		if (this.props.selected) {
			color += "-selected";
		}

		var className = "pg-lobby-color-picker-button-" + color;
		return (
			<div className={className} onClick={this.handleClick}></div>
		);
	}
});