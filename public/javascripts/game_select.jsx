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
            <div className={"game-choice-" + game}>
                <div className={"game-icon-container"}>
                    <GameIcon game={game} />
                    <div className="center-span">
                        <h1><GameTitle game={game} title={title} /></h1>
                    </div>
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
                <GameChoice game="fibbage" title="fibbage"/>
                <GameChoice game="mafia" title="mafia"/>
            </div>
        );
    }

});

ReactDOM.render(
    <GameSelectMenu />,
    document.getElementById('pg-app')
);