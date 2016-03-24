var ClientActionPanel = React.createClass({
    componentDidUpdate: function() {

        if (this.props.takingTurn) {
            $('#pi-cs-action-panel').addClass('pi-cs-action-panel-bidding');
        }
        else {
            $('#pi-cs-action-panel').removeClass('pi-cs-action-panel-bidding');
        }
    },

    render: function () {
        var tookTurn = this.props.tookTurn;
        var takingTurn = this.props.takingTurn;
        var api = this.props.api;
        var bid = this.props.bid;

        var bidForm = "";
        var liarButton = "";

        if (this.props.takingTurn) {
            bidForm = <BidForm onSubmit={tookTurn} api={api}/>;
            liarButton = <LiarButton onSubmit={tookTurn} api={api}/>;
        }

        return (
            <div id="pi-cs-action-panel" className="pi-cs-action-panel">
                {bidForm}
                {liarButton}
            </div>
        )
    }
});