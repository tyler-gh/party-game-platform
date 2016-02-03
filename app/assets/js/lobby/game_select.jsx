var GameLogo = React.createClass({
    handleClick: function(e) {
        ReactDOM.render(<GameLobby game={this.props.game} title={this.props.title}/>, document.getElementById('pg-app'));
    },
    render: function () {
        var game = this.props.game;
        var title = this.props.title;
        return (
            <div className="game-logo" onClick={this.handleClick}>
                <GameIcon game={game} color="color" size="large"/>
                <div className="center-span">
                    <h1><span>{title}</span></h1>
                </div>
            </div>
        );
    }
});

var GameChoice = React.createClass({
    render: function () {
        var game = this.props.game;
        var title = this.props.title;
        return (
            <div className={"game-choice " + game}>
                <GameLogo game={game} title={title}/>
            </div>
        );
    }
});

var api = new Rest("localhost", 9000);

var GameSelectMenu = React.createClass({
    getInitialState: function () {
        var me = this;
        // TODO this should probably be passed in through props
        api.all("game_definition").get(function(games) {
            me.setState({"games":games});
            console.log(games);
        });
        return {"games": []};
    },
    render: function () {
        return (
            <div>
                {
                    this.state.games.map(function (game) {
                        return <GameChoice key={game.id} game={game.id} title={game.title}/>
                    })
                }
            </div>
        );
    }

});

ReactDOM.render(
    <GameSelectMenu />,
    document.getElementById('pg-app')
);