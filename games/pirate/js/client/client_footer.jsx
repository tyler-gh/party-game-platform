var ClientFooter = React.createClass({
    render: function () {
        var bidCount  = this.props.bidCount;
        var bidNumber = this.props.bidNumber;
        
        return (
            <div className={"pi-cs-footer"}>
                <h1>Bid</h1>
                <div className={"pi-cs-footer-sign"}>
                    <div className={"pi-cs-footer-sign-content"}>
                        <span className={"pi-cs-footer-sign-text"}>
                            {bidCount < 1 ? '–' : bidCount}
                        </span>
                        { bidNumber < 1 ? 
                            <span className={"pi-cs-footer-sign-text"}>–</span> : 
                            <Die die_val={bidNumber}/> 
                        }
                    </div>
                </div>
            </div>
        );
    }
});