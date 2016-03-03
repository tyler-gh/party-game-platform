
var App = React.createClass({displayName: "App",
   render: function() {
       return React.createElement("h6", null, "Welcome to Pirate's Dice");
   }
});

window.gameStart = function(dom) {
    ReactDOM.render(
        React.createElement(App, null),
        dom
    );
};
