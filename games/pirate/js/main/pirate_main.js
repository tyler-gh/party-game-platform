var PirateApp = React.createClass({displayName: "PirateApp",
    getInitialState: function () {
        var me = this;
        this.props.api.addActionListener(function (action) {
            me.state.actions.push(action);
            me.setState({actions: me.state.actions});
        });
        return {actions: []};
    },
    render: function () {
        return (
            React.createElement("div", null, 
                React.createElement("h1", null, JSON.stringify(this.props.users)), 
                this.state.actions.map(function (action) {
                    return React.createElement("h6", null, JSON.stringify(action));
                })
            )
        );
    }
});

window.gameStart = function (dom, api, users) {
    ReactDOM.render(
        React.createElement(PirateApp, {api: api, users: users}),
        dom
    );
};
