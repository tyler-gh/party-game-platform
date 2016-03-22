var BidDisplay = React.createClass({
    render: function () {
        var style = {
            marginTop: "10px",
            marginBottom: "30px"
        };
        return <div style={style}>
            Bid-Count: {this.props.bidCount}<br/>
            Bid-Number: {this.props.bidNumber}
        </div>
    }

});
