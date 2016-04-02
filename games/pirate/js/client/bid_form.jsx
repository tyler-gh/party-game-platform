var BidForm = React.createClass({
    getInitialState: function () {

        var initialCount = this.props.bidCount;
        if (initialCount < 0) {
            initialCount = 1;
        }

        return {
            dieCount: initialCount,
            dieNumber: -1
        };
    },
    makeBid: function (e) {
        e.preventDefault();
        if (this.state.dieCount > 0 && this.state.dieNumber > 0) {
            this.props.api.makeBid({dieCount: this.state.dieCount, dieNumber: this.state.dieNumber});
            if(this.props.onSubmit) {
                this.props.onSubmit();
            }
            this.setState({dieCount: this.props.bidCount, dieNumber: -1});
        }
    },
    callLiar: function(e) {
        debugger
        e.preventDefault();
        this.props.api.callLiar();
        if(this.props.onSubmit) {
            this.props.onSubmit();
        }
    },
    incrementDieCount: function() {
        var incremented = this.state.dieCount + 1;
        this.setState({dieCount: incremented});
    },
    decrementDieCount: function() {
        if (this.state.dieCount > 1) {
            var decremented = this.state.dieCount - 1;
            this.setState({dieCount: decremented});
        }
    },
    selectDie: function(number) {
        this.setState({dieNumber: number});
    },
    render: function () {
        return (
            <div>
                <div className={"pi-bid-form-inputs"}>
                    <DiceCountController dieCount={this.state.dieCount} incrementDieCount={this.incrementDieCount} decrementDieCount={this.decrementDieCount}/>
                    <DiceValueSelection dieNumber={this.state.dieNumber} selectDie={this.selectDie}/>
                </div>
                <div className={"pi-bid-form-button-container"}>
                    <button className={"pi-make-bid-button"} type={"submit"} onClick={this.makeBid}>
                        <h1 className={"pi-bid-form-button-text"}>MAKE THE BID</h1>
                    </button>
                </div>
                <h1 className={"pi-bid-form-text"}>OR</h1>
                <div className={"pi-bid-form-button-container"}>
                    <button className={"pi-make-bid-button"} type={"submit"} onClick={this.callLiar}>
                        <h1 className={"pi-bid-form-button-text"}>SAY THAT BE A LIE</h1>
                    </button>
                </div>
            </div>
        );
    }
});

var DiceCountController = React.createClass({   

    componentDidMount: function() {
        
        incrementDieCount = this.props.incrementDieCount;
        decrementDieCount = this.props.decrementDieCount;

        $('#pi-dice-count-increment').click(function() {
            incrementDieCount();
        });

        $('#pi-dice-count-decrement').click(function() {
            decrementDieCount();
        });
    },

    render: function () {

        return (
            <div className={"pi-dice-count-controller"}>
                <div className={"pi-dice-count-control-arrow-container"} id={"pi-dice-count-increment"}>
                    <img className={"pi-dice-count-control-arrow"} src={"/assets/svg/dice_count_control_up.svg"}/>
                </div>
                <div className={"pi-dice-count-control-number"}>{this.props.dieCount}</div>
                <div className={"pi-dice-count-control-arrow-container"} id={"pi-dice-count-decrement"}>
                    <img className={"pi-dice-count-control-arrow"} src={"/assets/svg/dice_count_control_down.svg"}/>
                </div>
            </div>
        );
    }
});

var DiceValueSelection = React.createClass({
    
    componentDidMount: function() {

        component = this;
        $('#pi-dice-value-2').click(function() {
            component.handleDieSelect(2);
        });

        $('#pi-dice-value-3').click(function() {
            component.handleDieSelect(3);
        });

        $('#pi-dice-value-4').click(function() {
            component.handleDieSelect(4);
        });

        $('#pi-dice-value-5').click(function() {
            component.handleDieSelect(5);
        });

        $('#pi-dice-value-6').click(function() {
            component.handleDieSelect(6);
        });
    },

    handleDieSelect: function(number) {
        if (this.props.dieNumber > 1) {
            $('#pi-dice-value-' + this.props.dieNumber).attr('src', '/assets/svg/die_hollow_' + this.props.dieNumber + '.svg');
        }
        this.props.selectDie(number);
        $('#pi-dice-value-' + number).attr('src', '/assets/svg/die_' + number + '.svg');
    },

    render: function () {
        return (
            <div className={"pi-dice-value-selection"}>
                <div className={"pi-dice-value-selection-row"}>
                    <img className={"pi-dice-value-svg"} id={"pi-dice-value-2"} src={"/assets/svg/die_hollow_2.svg"}/>
                    <img className={"pi-dice-value-svg"} id={"pi-dice-value-3"} src={"/assets/svg/die_hollow_3.svg"}/>
                    <img className={"pi-dice-value-svg"} id={"pi-dice-value-4"} src={"/assets/svg/die_hollow_4.svg"}/>
                </div>
                <div className={"pi-dice-value-selection-row"}>
                    <img className={"pi-dice-value-svg"} id={"pi-dice-value-5"} src={"/assets/svg/die_hollow_5.svg"}/>
                    <img className={"pi-dice-value-svg"} id={"pi-dice-value-6"} src={"/assets/svg/die_hollow_6.svg"}/>
                </div>

            </div>
        );
    }

});