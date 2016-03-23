var ClientDiceDisplay = React.createClass({
    render: function () {
        return (
        	<div className={"pi-cs-dice-display"}>
        		<h2>{JSON.stringify(this.props.dice)}</h2>
        		<DieOne/>
        		<div className={"pi-cs-dice-shelf"}></div>
        	</div>
        )
    }
});

