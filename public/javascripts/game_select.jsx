var GameIcon = React.createClass({
    render: function() {
        var game = this.props.game;
        return (
            <img src={"/assets/svg/" + game + "_icon_color.svg"} />
        );
    }
});

var GameTitle = React.createClass({
    render: function() {
        var game = this.props.game;
        var title = this.props.title;
        return (
            <span className={"color-" + game}>{title}</span>
        );
    }
});

var GameChoice = React.createClass({
    render: function() {
        var game = this.props.game;
        var title = this.props.title;
        return (
            <div className={"game-icon-container " + game}>
                <GameIcon game={game} />
                <div className="center-span">
                    <GameTitle game={game} title={title} />
                </div>
            </div>
        );
    }
});

var GameSelectMenu = React.createClass({
    render: function () {
        return (
            <div>
                <GameChoice game="pirate" title="pirate's dice"/>
                <GameChoice game="mafia" title="mafia"/>
                <GameChoice game="pirate" title="pirate's dice"/>
            </div>
        );
    }

});

ReactDOM.render(
    <GameSelectMenu />,
    document.getElementById('pg-app')
);