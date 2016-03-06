var App = React.createClass({displayName: "App",
    getInitialState: function () {
        var me = this;
        this.props.api.addActionListener(function (action) {
            console.log(action);
            switch(action.actionType) {
                case "new-die":
                    me.setState({die: action.data.die});
                    break;
                case "prompt-turn":
                    me.setState({takingTurn: true});
                    break;
                case "lost-die":
                    me.setState({currentBidCount: -1, currentBidNumber: -1});
                    break;
                case "new-bid":
                    me.setState({currentBidCount: action.data.bid.dieCount, currentBidNumber: action.data.bid.dieNumber});
                    break;
                case "invalid-bid":
                    me.setState({takingTurn: true});
                    break;
            }
        });
        return {
            die: [],
            takingTurn: false,
            dieCount: -1,
            dieNumber: -1,
            currentBidCount: -1,
            currentBidNumber: -1,
            dieCountStr: "",
            dieNumberStr: ""
        };
    },
    callLiar: function(e) {
        e.preventDefault();
        this.props.api.callLiar();
        this.setState({takingTurn: false, dieCount: -1, dieNumber: -1, dieCountStr: "", dieNumberStr: ""});
    },
    makeBid: function(e) {
        e.preventDefault();
        if(this.state.dieCount > 0 && this.state.dieNumber > 0) {
            this.props.api.makeBid({
                dieCount: this.state.dieCount,
                dieNumber: this.state.dieNumber
            });
            this.setState({takingTurn: false, dieCount: -1, dieNumber: -1, dieCountStr: "", dieNumberStr: ""});
        }
    },
    handleDieCountChange: function (e) {
        this.setState({dieCount: parseInt(e.target.value), dieCountStr: parseInt(e.target.value)});
    },
    handleDieNumberChange: function (e) {
        this.setState({dieNumber: parseInt(e.target.value), dieNumberStr: parseInt(e.target.value)});
    },
    render: function () {
        var divStyle = {
            padding: "50px"
        };
        var dieStyle = {
            marginBottom: this.state.currentBidCount != -1 ? "0px" : "70px"
        };
        var headerStyle = {
            marginTop: "10px",
            marginBottom: "30px"
        };
        var formsStyle = {
            display: this.state.takingTurn ? "inherit" : "none"
        };
        return (
            React.createElement("div", {style: divStyle}, 
                React.createElement("h1", {style: headerStyle}, "Pirate's Dice"), 
                React.createElement("h2", {style: dieStyle}, JSON.stringify(this.state.die)), 
                (function()  {
                    if(this.state.currentBidCount != -1) {
                        return React.createElement("div", {style: headerStyle}, 
                            "Bid-Count: ", this.state.currentBidCount, React.createElement("br", null), 
                            "Bid-Number: ", this.state.currentBidNumber
                        )
                    }
                }.bind(this))(), 
                React.createElement("div", {style: formsStyle}, 
                    (function()  {
                        if(this.state.currentBidCount != -1) {
                            return React.createElement("form", {onSubmit: this.callLiar}, 
                                React.createElement("input", {type: "submit", value: "LIAR!!"})
                            )
                        }
                    }.bind(this))(), 
                    React.createElement("form", {onSubmit: this.makeBid}, 
                        React.createElement("input", {type: "text", placeholder: "Number of Die", value: this.state.dieCountStr, onChange: this.handleDieCountChange}), 
                        React.createElement("input", {type: "text", placeholder: "Die Number", value: this.state.dieNumberStr, onChange: this.handleDieNumberChange}), 
                        React.createElement("input", {type: "submit", value: "Make Bid"})
                    )
                )
            )
        );
    }
});

window.gameStart = function (dom, api, users) {
    ReactDOM.render(
        React.createElement(App, {api: api, users: users}),
        dom
    );
};
