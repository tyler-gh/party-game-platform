var ClientFooter = React.createClass({
    render: function () {
        
        return (
            <div id={"pi-cs-footer"} className={"pi-cs-footer"}>
                <h1>Bid</h1>
                <div className={"pi-cs-footer-sign"}>
                    <div id={"pi-cs-footer-sign-co"} className={"pi-cs-footer-sign-content"}>
                        <span className={"pi-cs-footer-sign-text"}>
                            {this.props.bidCount < 1 ? '–' : this.props.bidCount}
                        </span>
                        { this.props.bidNumber < 1 ? 
                            <span className={"pi-cs-footer-sign-text"}>–</span> : 
                            <Die die_val={this.props.bidNumber}/> 
                        }
                    </div>
                </div>
            </div>
        );
    }
});