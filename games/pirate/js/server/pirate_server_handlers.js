//handlers

var actionHandler = function (actionStr) {
    var action = JSON.parse(actionStr);

    switch (action.actionType) {
        case "client-joined":
        case "client-dropped":
            doBroadcast(action);
            break;
        case "client-left":
            removeUser(action.client);
            doBroadcast(action);
            break;
        case "start-game":
            doBroadcast(action);
            generateDie();
            broadcastDie();
            promptCurrentTurn();
            break;
        case "take-turn":
            takeTurn(action);
            break;
    }
};

var newClientConnectionHandler = function (clientStr) {
    var client = JSON.parse(clientStr);
    if (!state.users[state.userIndexes[client.id]]) {
        addUser(client);
    }
    sendUserInfo(client.id, client);
    broadcastActions.forEach(function (action) {
        sendActionToClient(client.id, action);
    });
    if (client.id != 0) {
        sendDieToClient(client.id, copy(state.users[state.userIndexes[client.id]].die));

        if (state.userIndexes[client.id] == state.currentUserIndex) {
            promptCurrentTurn();
        }
    }
};

//noinspection JSUnresolvedFunction
setActionHandler("actionHandler");

//noinspection JSUnresolvedFunction
setNewClientConnectionHandler("newClientConnectionHandler");