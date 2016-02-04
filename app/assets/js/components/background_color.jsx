var BackgroundColor = React.createClass({
    render: function() {
    	var game = this.props.game;
        return (
	        <div className={"pg-" + game + "-background-color"}>
				{this.props.children}
	        </div>
	    );
    }
});