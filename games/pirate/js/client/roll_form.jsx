var RollForm = React.createClass({
    rollDice: function (e) {
        if (this.props.needToRoll) {
            Api.socketSend("ws", JSON.stringify({actionType: "dice-rolled"}));
            this.props.rolledDice();
        }
    },
    componentDidUpdate: function() {
        if (this.props.needToRoll) {
            $('#Roll-Button').removeClass('invisible');
        }
        else {
            $('#Roll-Button').addClass('invisible');
        }
    },
    render: function () {
        var needToRoll = this.props.needToRoll;

        return (
            <button id="Roll-Button" className="invisible"  onClick={this.rollDice}>
                Roll Dice
            </button>
        );
    }
});