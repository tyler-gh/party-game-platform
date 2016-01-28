var IconButton = React.createClass({
    render: function() {
    	var game = this.props.game;
    	var icon = this.props.icon;
    	var text = this.props.text;
        
        return (
	        <button className={"pg-lobby-icon-button-"+game}>
	        	{text}
	        	<div className={"pg-lobby-icon-button-icon-container"}>
	        		<img className={"pg-lobby-icon-button-icon"} src={"/assets/svg/icon_" + icon + ".svg"}/>
	        	</div>
	        </button>
	    );
    }
});