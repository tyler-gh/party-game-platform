var GameCodeJoin = React.createClass({
    getInitialState: function () {
        return {gameCode: '',
    			badCode: false};
    },
    handleGameCodeChange: function (e) {
        this.setState({gameCode: e.target.value,
        			   badCode: false});
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
			var game = this.props.game;
			var gameCode = this.state.gameCode;
			var component = this

			var success = function() {
				$('#enter-game-code-content').css('animation','exitLeft .2s ease-in');
					setTimeout(function() {
					component.renderGameUserJoin();
					$('#enter-player-info-content').css('animation','enterLeft .2s ease-in');
				}, 200);
			}

			var error = function() {
				component.setState({badCode: true});
			}

			Api.gameExists(game, gameCode, success, error);

		}.bind(this);


        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">join game</h1>
						<div id="enter-game-code-content">
							<LobbyForm game={game} handleChange={this.handleGameCodeChange} instructions="enter game code" errorMessage="sorry, there's no game with this code" hasError={this.state.badCode}/>
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
        return {name: '', color: ''};
    },
    handleNameChange: function (e) {
        this.setState({name: e.target.value});
    },
	handleColorChange: function(color_id) {
		this.setState({color: color_id});
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
			var color = this.state.color;

		    if(name && color) {
				$('#game-lobby').css('animation','exitLeft .2s ease-in');

				setTimeout(function() {
					ReactDOM.render(<LobbyLoadingSpinner game={game}/>, document.getElementById('game-lobby'));
					
					Api.joinGame(name, color, game, gameCode, function(data) {
						window.location.href = '/game';
	                });
				}, 200);

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
							<ColorPicker onColorPicked={this.handleColorChange} />
							<LobbyButton game={game} text={"join"} handleClick={clickJoin} />
							<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
						</div>
					</div>
				</LobbyContainer>
			</div>
		);
	}
});