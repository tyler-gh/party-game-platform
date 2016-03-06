
var actions = [];

var actionHandler = function(actionStr){
    actions.push(JSON.parse(actionStr));
    //noinspection JSUnresolvedFunction
    broadcastAction(actionStr);
};

var newClientConnectionHandler = function(clientStr) {
    var client = JSON.parse(clientStr);
    var clientId = JSON.stringify([client.id]);
    actions.forEach(function(action) {
        //noinspection JSUnresolvedFunction
        sendAction(clientId, JSON.stringify(action));
    });
};

//noinspection JSUnresolvedFunction
setActionHandler("actionHandler");
//noinspection JSUnresolvedFunction
setNewClientConnectionHandler("newClientConnectionHandler");