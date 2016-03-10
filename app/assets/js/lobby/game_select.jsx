
var GameLogo = React.createClass({
    handleClick: function(e) {
        ReactDOM.render(<GameLobby game={this.props.game} title={this.props.title} description={this.props.description}/>, document.getElementById('pg-app'));
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
        var description = this.props.description;
        return (
            <div className={"game-choice " + game}>
                <GameLogo game={game} title={title} description={description}/>
            </div>
        );
    }
});

var api = new Rest("localhost", 9000);

var GameSelectMenu = React.createClass({
    getInitialState: function () {
        var me = this;
        document.getElementById('testing').innerText = "testing1"
        // TODO this should probably be passed in through props
        api.all("game_definition").get(function(games) {
            document.getElementById('testing').innerText = "games"
            me.setState({"games":games});
        });
        return {"games": []};
    },
    render: function () {
        return (
            <div>
                {
                    this.state.games.map(function (game) {
                        document.getElementById('testing').innerText = "game.description"
                        return <GameChoice key={game.id} game={game.id} title={game.title} description={game.description}/>
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
document.getElementById('testing').innerText = "testing"