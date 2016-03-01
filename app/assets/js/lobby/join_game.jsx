var GameCodeJoin = React.createClass({
    getInitialState: function () {
        return {gameCode: ''};
    },
    handleGameCodeChange: function (e) {
        this.setState({gameCode: e.target.value});
    },
    renderGameUserJoin: function() {
    	ReactDOM.render(<GameUserJoin game={this.props.game} gameCode={this.state.gameCode} title={this.props.title} description={this.props.description}/>, document.getElementById('pg-app'));
    },
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;

		var clickCancel = function() {
			$('#pg-app').css('animation','exitRight .2s ease-in');
			$("#lobby-banner").css('animation','exitBanner .4s ease-out');
    		setTimeout(function() {
				ReactDOM.render(<GameLobby game={game} title={title} description={description}/>, document.getElementById('pg-app'));
				$('#pg-app').css('animation','enterRight .2s ease-out');
			}, 200);
		};

		var clickJoin = function() {
			// TODO hit api to see if game actually exists
			if(this.state.gameCode) {
				$('#enter-game-code-content').css('animation','exitLeft .2s ease-in');
				var component = this;
				setTimeout(function() {
					component.renderGameUserJoin();
					$('#enter-player-info-content').css('animation','enterLeft .2s ease-in');
				}, 200);
			}
		}.bind(this);


        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">join game</h1>
						<div id="enter-game-code-content">
							<LobbyForm game={game} handleChange={this.handleGameCodeChange} instructions="enter game code"/>
							<LobbyButton game={game} text={"submit"} handleClick={clickJoin} />
							<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
						</div>
					</div>
				</LobbyContainer>
	        </div>
	    );
    }
});

var GameUserJoin = React.createClass({
    getInitialState: function () {
        return {name: ''};
    },
    handleNameChange: function (e) {
        this.setState({name: e.target.value});
    },
	render: function() {
		var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;
		var gameCode = this.props.gameCode;

		var clickCancel = function() {
			$('#enter-player-info-content').css('animation','exitRight .2s ease-in');
			setTimeout(function() {
				ReactDOM.render(<GameCodeJoin game={game} title={title} description={description} />, document.getElementById('pg-app'));
				$('#enter-game-code-content').css('animation','enterRight .2s ease-out');
			}, 200);
		};

		var clickJoin = function() {
		    var name = this.state.name;
			$('#game-lobby').css('animation','exitLeft .2s ease-in');
		    if(name) {
		        Api.joinGame(name, "green", game, gameCode, function(data) {
					window.location.href = '/game';
                });
			}
		}.bind(this);

		return (
			<div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">join game</h1>
						<div id="enter-player-info-content">
							<LobbyForm game={game} handleChange={this.handleNameChange} instructions="enter your name"/>
							<ColorPicker game={game} color="color"/>
							<LobbyButton game={game} text={"join"} handleClick={clickJoin} />
							<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
						</div>
					</div>
				</LobbyContainer>
			</div>
		);
	}
});