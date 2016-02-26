var GameLobby = React.createClass({
    
    componentDidMount: function() {
         $('#pg-app').css('animation','enterLeft .2s ease-out'); 
    },

    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;
		var gameCode = this.props.gameCode;

		var clickCancel = function() {
			$('#pg-app').css('animation','exitRight .2s ease-in');
    		setTimeout(function() {
                ReactDOM.render(<GameSelectMenu />, document.getElementById('pg-app'));
                $('#pg-app').css('animation','enterRight .2s ease-out');
        	}, 200);
		};

		var clickJoin = function() {
			$('#pg-app').css('animation','exitLeft .2s ease-in');
    		setTimeout(function() {
                ReactDOM.render(<GameCodeJoin game={game} title={title} description={description}/>, document.getElementById('pg-app'));
                $("#lobby-banner").css('animation','enterBanner .4s ease-out');
				$('#pg-app').css('animation','enterLeft .2s ease-in');
        	}, 200);
		};

		var clickCreate = function() {
			$('#pg-app').css('animation','exitLeft .2s ease-in');
    		setTimeout(function() {
            	Api.createGame(game, function(data) {
					ReactDOM.render(<GameCreatedSuccess game={game} title={title} gameCode={data.game_instance_id} />, document.getElementById('pg-app'));
				});
        	}, 200);
		};

        return (
			<BackgroundColor game={game}>
	        	<LobbyContainer game={game} color="color">
					<GameIcon size="large"  game={game} color="white"/>
					<h1 className="lobby">{title}</h1>
					<h3 className="lobby">{description}</h3>
					<LobbyButton game={game} icon={"create"} text={"create"} handleClick={clickCreate}/>
					<LobbyButton game={game} icon={"join"} text={"join"} handleClick={clickJoin}/>
					<LobbyButton game={game} hollow="white" text={"cancel"} handleClick={clickCancel}/>
	        	</LobbyContainer>
			</BackgroundColor>
	    );
    }
});
