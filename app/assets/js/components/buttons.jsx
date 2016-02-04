var LobbyButton = React.createClass({
    render: function() {
    	var game = this.props.game;
    	var icon = this.props.icon;
    	var text = this.props.text;
        var hollow = this.props.hollow;

		var className;
		if (icon != null) {
			className = "pg-lobby-icon-button";
		} else if (hollow != null) {
			className = "pg-lobby-hollow-button";
			if (hollow == "white") {
				className += " white";
			}
		} else {
			className = "pg-lobby-solid-button";
		}

		className += " " + game;

        return (
	        <button className={className}>
	        	{text}
				{(() => {
					if (icon != null) {
						return <IconButtonImage icon={icon} />;
					} else {
						return "";
					}
				})()}
	        </button>
	    );
    }
});

var IconButtonImage = React.createClass({
	render: function() {
		var icon = this.props.icon;
		return (
			<div className={"pg-lobby-icon-button-icon-container"}>
				<img className={"pg-lobby-icon-button-icon"} src={"/assets/svg/icon_" + icon + ".svg"}/>
			</div>
		);
	}
});