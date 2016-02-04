var LobbyIconButtonsStyleDemo = React.createClass({
    render: function() {
	    return (
	        <div>
	        	<LobbyButton game={"pirate"} icon={"create"} text={"create"}/>
	        	<br/>
	        	<br/>
	        	<LobbyButton game={"pirate"} icon={"join"} text={"join"}/>
	        </div>
	    );
    }
});

ReactDOM.render(
    <LobbyIconButtonsStyleDemo />,
    document.getElementById('pg-styleguide-icon-buttons')
);

