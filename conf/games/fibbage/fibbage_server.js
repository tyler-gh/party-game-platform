
var actions = [];

setActionHandler(function(action){

    action = JSON.parse(action);
    actions.push(action);


    broadcastAction(JSON.stringify(action));


    actions.forEach(function(action) {
        sendAction(JSON.stringify([action.client.id]), JSON.stringify(action));
    });
});