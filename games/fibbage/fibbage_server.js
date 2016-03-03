
var actions = [];

setActionHandler(function(actionStr){
    actions.push(JSON.parse(actionStr));
    broadcastAction(actionStr);
});

setNewClientConnectionHandler(function(clientStr) {
    var client = JSON.parse(clientStr);
    var clientId = JSON.stringify([client.id]);
    actions.forEach(function(action) {
        sendAction(clientId, JSON.stringify(action));
    });
});