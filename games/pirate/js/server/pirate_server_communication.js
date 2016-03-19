var broadcastActions = [];

//specific broadcasters

var promptCurrentTurn = function () {
    sendActionToClient(state.users[state.currentUserIndex].id, makeAction({
        actionType: "prompt-turn"
    }));
};

var sendDieToClient = function (id, die) {
    sendActionToClient(id, makeAction({
        actionType: "new-die",
        data: {
            die: die
        }
    }));
};

var broadcastDie = function () {
    forEachUser(function (user) {
        sendDieToClient(user.id, copy(user.die));
    });
};

var broadcastLostDie = function(user)
{
    doBroadcast(makeAction({
        actionType: "lost-die",
        data: {
            client_id: user.id
        }
    }));
}
var broadcastNewBid = function()
{
    doBroadcast(makeAction({
        actionType: "new-bid",
        data: {
            bid: {dieCount: state.bid.dieCount, dieNumber: state.bid.dieNumber}
        }
    }));
}


//general broadcasters
var doBroadcast = function (action) {
    broadcastActions.push(action);
    //noinspection JSUnresolvedFunction
    broadcastAction(JSON.stringify(action));
};

function sendActionToClient(id, action) {
    sendActionToClients([id], action);
}

function sendActionToClients(ids, action) {
    //noinspection JSUnresolvedFunction
    sendAction(JSON.stringify(ids), JSON.stringify(action));
}

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
