var BidForm = React.createClass({
    getInitialState: function () {
        return {
            dieCount: -1,
            dieNumber: -1,
            dieCountStr: "",
            dieNumberStr: ""
        };
    },
    makeBid: function (e) {
        e.preventDefault();
        if (this.state.dieCount > 0 && this.state.dieNumber > 0) {
            this.props.api.makeBid({dieCount: this.state.dieCount, dieNumber: this.state.dieNumber});
            if(this.props.onSubmit) {
                this.props.onSubmit();
            }
            this.setState({dieCount: -1, dieNumber: -1, dieCountStr: "", dieNumberStr: ""});
        }
    },
    handleDieCountChange: function (e) {
        this.setState({dieCount: parseInt(e.target.value), dieCountStr: e.target.value});
    },
    handleDieNumberChange: function (e) {
        this.setState({dieNumber: parseInt(e.target.value), dieNumberStr: e.target.value});
    },
    render: function () {
        return (
            <form onSubmit={this.makeBid}>

                <input type="text" placeholder="Number of Die"
                       value={this.state.dieCountStr}
                       onChange={this.handleDieCountChange}/>

                <input type="text" placeholder="Die Number"
                       value={this.state.dieNumberStr}
                       onChange={this.handleDieNumberChange}/>
                <input type="submit" value="MAKE THE BID"/>
            </form>
        );
    }
});