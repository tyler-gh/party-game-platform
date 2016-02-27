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
							<ColorPicker game={game} color="color">
							</ColorPicker>
							<LobbyButton game={game} text={"join"} handleClick={clickJoin} />
							<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
						</div>
					</div>
				</LobbyContainer>
			</div>
		);
	}
});