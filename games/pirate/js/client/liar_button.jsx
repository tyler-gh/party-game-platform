var LiarButton = React.createClass({
    callLiar: function (e) {
        e.preventDefault();
        this.props.api.callLiar();
        if(this.props.onSubmit) {
            this.props.onSubmit();
        }
    },
    render: function () {
        return <form onSubmit={this.callLiar}>
            <input type="submit" value="LIAR!!"/>
        </form>
    }
});
