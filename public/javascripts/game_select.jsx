var GameIcon = React.createClass({
    render: function () {
        var game = this.props.game;
        var title = this.props.title;
        return (
            <div className={"game-icon-container " + game}>
                <img src={"/assets/svg/" + game + "_icon_color.svg"}/>
                <div className="center-span"><span className={"color-" + game}>{title}</span></div>
            </div>
        );
    }
});

var GameSelectMenu = React.createClass({
    render: function () {
        return (
            <div>
                <GameIcon game="pirate" title="pirate's dice"/>
                <GameIcon game="mafia" title="mafia"/>
            </div>
        );
    }

});

ReactDOM.render(
    <GameSelectMenu />,
    document.getElementById('pg-app')
);