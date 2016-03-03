
var App = React.createClass({displayName: "App",
   render: function() {
       console.log("hi");

       return React.createElement("h1", null, "I'm the main screen!!!!");
   }
});

window.gameStart = function(dom) {
    ReactDOM.render(
        React.createElement(App, null),
        dom
    );
};
