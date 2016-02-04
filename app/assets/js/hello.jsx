var Counter = React.createClass({
    getInitialState: function () {
        return { clickCount: 0 };
    },
    handleClick: function () {
        this.setState(function(state) {
            return {clickCount: state.clickCount + 1};
        });
    },
    render: function () {
        return (<h1 onClick={this.handleClick}>Hello react, Number of clicks: {this.state.clickCount}</h1>);
    }
});
ReactDOM.render(
    <Counter />,
    document.getElementById('message')
);