var GameBanner = React.createClass({
    render: function() {
        var game = this.props.game;
        return (
            <div className={"lobby-banner-" + game} id={"lobby-banner"}>
                <GameIcon game={game} color={"white"} size="small"/>
            </div>
        );
    }
});