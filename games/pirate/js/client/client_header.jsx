var ClientHeader = React.createClass({
    render: function () {
        var username = this.props.username;
        
        return (
            <div className={"pi-cs-header"}>
                <div className={"pi-cs-banner"}>
                    <img className={"pi-cs-skull"} src={"/assets/svg/skull_crossbones.svg"}/>
                </div>
                <div className={"pi-cs-header-sign-container"}>
                    <img className={"pi-cs-header-sign"} src={"/assets/svg/pirate_sign.svg"}>
                        <h1 className={"pi-cs-header-sign-text"}>{username}</h1>
                    </img>
                </div>
            </div>
        );
    }

});