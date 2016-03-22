var LobbyLoadingSpinner = React.createClass({
    render: function () {
        var game = this.props.game;
        
        return (
        	<div className={"pg-lobby-loading-spinner-" + game}>
        		<div className={"pg-lobby-loading-spinner-content"}>
					<div className={"contener_mixte"}><div className={"ballcolor ball_1"}>&nbsp;</div></div>
					<div className={"contener_mixte"}><div className={"ballcolor ball_2"}>&nbsp;</div></div>
					<div className={"contener_mixte"}><div className={"ballcolor ball_3"}>&nbsp;</div></div>
					<div className={"contener_mixte"}><div className={"ballcolor ball_4"}>&nbsp;</div></div>
				</div>
				<h3 className="pg-lobby-loading-spinner-text">loading...</h3>
			</div>
        );
    }
});