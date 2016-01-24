var GameIcon = React.createClass({
    render: function () {
        var game = this.props.game;
        var title = this.props.title;
        return (
            <div className={"game-icon-container " + game}>
                <img src={"/assets/svg/" + game + "_icon_color.svg"}/>
                {title}
            </div>
        );
    }
});

var GameSelectMenu = React.createClass({
    render: function () {
        return (
            <div>
                <div> select a game placeholder</div>
                <GameIcon game="pirate" title="Pirate's Dice"/>
                <GameIcon game="mafia" title="Mafia"/>
            </div>
        );
    }

});

ReactDOM.render(
    <GameSelectMenu />,
    document.getElementById('pg-app')
);