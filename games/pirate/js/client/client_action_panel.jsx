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
        var bidCount = this.props.bidCount;
        var bidNumber = this.props.bidNumber;

        var bidForm = "";
        var liarButton = "";

        if (takingTurn) {
            bidForm = <BidForm onSubmit={tookTurn} api={api} bidCount={bidCount} bidNumber={bidNumber}/>;
            liarButton = <LiarButton onSubmit={tookTurn} api={api}/>;
        }

        return (
            <div id="pi-cs-action-panel" className="pi-cs-action-panel">
                {bidForm}
            </div>
        )
    }
});