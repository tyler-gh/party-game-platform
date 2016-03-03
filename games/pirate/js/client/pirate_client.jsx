
var App = React.createClass({
   render: function() {
       return <h6>Welcome to Pirate's Dice</h6>;
   }
});

window.gameStart = function(dom) {
    ReactDOM.render(
        <App/>,
        dom
    );
};
