var GameCodeJoin = React.createClass({
    getInitialState: function () {
        return {gameCode: ''};
    },
    handleGameCodeChange: function (e) {
        this.setState({gameCode: e.target.value});
    },
    render: function() {
    	var game = this.props.game;
		var title = this.props.title;
		var description = this.props.description;

		var clickCancel = function() {
			ReactDOM.render(<GameLobby game={game} title={title} description={description}/>, document.getElementById('pg-app'));
		};

		var clickJoin = function() {
			if(this.state.gameCode) {
				// TODO hit api to see if game actually exists
				ReactDOM.render(<GameUserJoin game={game} gameCode={this.state.gameCode} title={title} description={description}/>, document.getElementById('pg-app'));
			}
		}.bind(this);


        return (
	        <div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">join game</h1>
						<LobbyForm game={game} handleChange={this.handleGameCodeChange} instructions="enter game code"/>
						<LobbyButton game={game} text={"submit"} handleClick={clickJoin} />
						<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
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
			ReactDOM.render(<GameCodeJoin game={game} title={title} description={description} />, document.getElementById('pg-app'));
		};

		var clickJoin = function() {
		    var name = this.state.name;
		    if(name) {
		        Api.joinGame(name, "green", game, gameCode, function(data) {
					window.location.href = '/game';
					//ReactDOM.render(<WaitingRoom game={game} name={name} title={title} gameCode={gameCode} />, document.getElementById('pg-app'));
                });
			}
		}.bind(this);

		return (
			<div>
				<GameBanner game={game} />
				<LobbyContainer game={game} color="color">
					<div className="container">
						<h1 className="create-game">join game</h1>
						<LobbyForm game={game} handleChange={this.handleNameChange} instructions="enter your name"/>
						<h3 className="create-game">select your color</h3>
						<LobbyButton game={game} text={"join"} handleClick={clickJoin} />
						<LobbyButton game={game} hollow="color" text={"cancel"} handleClick={clickCancel} />
					</div>
				</LobbyContainer>
			</div>
		);
	}
});