var PirateApp = React.createClass({
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
            <div>
                <h1>{JSON.stringify(this.props.users)}</h1>
                {this.state.actions.map(function (action) {
                    return <h6>{JSON.stringify(action)}</h6>;
                })}
            </div>
        );
    }
});

window.gameStart = function (dom, api, users) {
    ReactDOM.render(
        <PirateApp api={api} users={users}/>,
        dom
    );
};
