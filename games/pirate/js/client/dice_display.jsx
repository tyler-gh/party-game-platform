var DiceDisplay = React.createClass({
    render: function () {
        var dieStyle = {
            marginBottom: this.props.bid ? "0px" : "70px"
        };
        return <h2 style={dieStyle}>{JSON.stringify(this.props.dice)}</h2>
    }
});