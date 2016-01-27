var GameIcon = React.createClass({
    render: function () {
        var game = this.props.game;
        var color = this.props.color;
        return (
            <img className="game-icon" src={"/assets/svg/" + game + "_icon_" + color + ".svg"}/>
        );
    }
});