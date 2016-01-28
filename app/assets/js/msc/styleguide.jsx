var LobbyIconButtonsStyleDemo = React.createClass({
    render: function() {
	    return (
	        <div>
	        	<IconButton game={"pirate"} icon={"create"} text={"create"}/>
	        	<br/>
	        	<br/>
	        	<IconButton game={"pirate"} icon={"join"} text={"join"}/>
	        </div>
	    );
    }
});

ReactDOM.render(
    <LobbyIconButtonsStyleDemo />,
    document.getElementById('pg-styleguide-icon-buttons')
);

