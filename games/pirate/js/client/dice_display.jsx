var ClientDiceDisplay = React.createClass({
    render: function () {
        return (
        	<div className={"pi-cs-dice-display"}>
        		<div className={"pi-cs-dice-bar-container"}>
        			<DiceBar dice={this.props.dice}/>
        		</div>
        		<div className={"pi-cs-dice-shelf"}></div>
        	</div>
        )
    }
});

var DiceBar = React.createClass({

    render: function () {
        dice = this.props.dice;
        return (
        	<div className="pi-cs-dice-bar">
                {dice.map(function(die_val){
                    return <Die die_val={die_val}/>;
                 })}
            </div>
        )
    }

});


var Die = React.createClass({
    
    componentDidMount() {

		$('.dice_svg').each(function() {
		    var $img = jQuery(this);
		    var imgID = $img.attr('id');
		    var imgClass = $img.attr('class');
		    var imgURL = $img.attr('src');

		    jQuery.get(imgURL, function(data) {
		        // Get the SVG tag, ignore the rest
		        var $svg = jQuery(data).find('svg');

		        // Add replaced image's ID to the new SVG
		        if(typeof imgID !== 'undefined') {
		            $svg = $svg.attr('id', imgID);
		        }
		        // Add replaced image's classes to the new SVG
		        if(typeof imgClass !== 'undefined') {
		            $svg = $svg.attr('class', imgClass+' replaced-svg');
		        }

		        // Remove any invalid XML tags as per http://validator.w3.org
		        $svg = $svg.removeAttr('xmlns:a');

		        // Replace image with new SVG
		        $img.replaceWith($svg);

		    }, 'xml');

		});
    },	

    render: function () {
    	die_val = this.props.die_val
        return <img className={"dice_svg"} src={"/assets/svg/die_" + die_val + ".svg"}/>
       
    }
});
