
var App = React.createClass({
   render: function() {
       console.log("hi");

       return <h1>I'm the main screen!!!!</h1>;
   }
});

window.gameStart = function(dom) {
    ReactDOM.render(
        <App/>,
        dom
    );
};
