var GameIcon = React.createClass({
    render: function () {
        var game = this.props.game;
        var color = this.props.color;
        var size = this.props.size;
        return (
            <img className={"game-icon " + size} src={"/assets/svg/" + game + "_icon_" + color + ".svg"}/>
        );
    }
});