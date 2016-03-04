//noinspection JSUnresolvedFunction
var broadcastActions = [];

var state = {
    finished: false,
    gameStarted: false,
    numberOfUsers: 0,
    currentUserIndex: 1,
    bid: {
        dieNumber: -1,
        dieCount: -1,
        bidder: -1
    },
    users: [{
        id: 0,
        name: "root",
        color: "black",
        die: [],
        numberOfDie: -1
    }],
    userIndexes: {
        0: 0
    }
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

var forEachUser = function (f) {
    for (var i = 1; i < state.users.length; i++) {
        f(state.users[i]);
    }
};

var generateDie = function () {
    forEachUser(function (user) {
        user.die = [];
        for (var j = 0; j < user.numberOfDie; j++) {
            user.die.push(getRandomInt(1, 6));
        }
    });
};

var addUser = function (client) {
    client = copy(client);
    client.die = [];
    client.numberOfDie = 5;
    state.userIndexes[client.id] = state.users.length;
    state.users.push(client);
};

var removeUser = function (client) {
    var index = state.userIndexes[client.id];
    if (index) {
        state.userIndexes[client.id] = undefined;
        state.users.splice(index, 1);
        for (var i = 1; i < state.users.length; i++) {
            state.userIndexes[state.users[i].id] = i;
        }
    }
};

var doBroadcast = function (action) {
    broadcastActions.push(action);
    //noinspection JSUnresolvedFunction
    broadcastAction(JSON.stringify(action));
};

var promptCurrentTurn = function () {
    sendActionToClient(state.users[state.currentUserIndex].id, makeAction({
        actionType: "prompt-turn"
    }));
};

var advanceUser = function () {
    if (!state.finished) {
        state.currentUserIndex++;
        if (state.currentUserIndex >= state.users.length) {
            state.currentUserIndex = 1;
        }
        if (state.users[state.currentUserIndex].numberOfDie <= 0) {
            advanceUser();
        }
    }
};

var didLie = function () {
    if (state.bid.bidder != -1) {
        var count = 0;

        forEachUser(function (user) {
            user.die.forEach(function (die) {
                if (die === state.bid.dieNumber || die === 1) {
                    count++;
                }
            });
        });

        if (count < state.bid.dieCount) {
            return true;
        }
    }
    return false;

};

var checkWinner = function () {
    var numPlayers = 0;
    forEachUser(function (user) {
        if (user.numberOfDie > 0) {
            numPlayers++;
        }
    });
    return numPlayers == 1;
};

var takeTurn = function (action) {
    if (action.client.id == state.users[state.currentUserIndex].id) {
        switch (action.data.responseType) {
            case "lie":
                var user;
                if (didLie()) {
                    user = state.users[state.userIndexes[state.bid.bidder]];
                } else {
                    user = state.users[state.currentUserIndex];
                }
                state.bid.dieCount = -1;
                state.bid.dieNumber = -1;
                state.bid.bidder = -1;
                user.numberOfDie--;
                doBroadcast(makeAction({
                    actionType: "lost-die",
                    data: {
                        client_id: user.id
                    }
                }));
                state.currentUserIndex = state.userIndexes[user.id] - 1;
                if (checkWinner()) {
                    state.finished = true;
                    return;
                }
                generateDie();
                broadcastDie();
                break;
            case "bet":
                if ((state.bid.dieNumber >= action.data.bid.dieNumber && state.bid.dieCount >= action.data.bid.dieCount) ||
                    state.bid.dieCount > action.data.bid.dieCount ||
                    action.data.bid.dieNumber < 1 ||
                    action.data.bid.dieNumber > 6
                ) {
                    sendActionToClient(action.client.id, makeAction({actionType: "invalid-bid"}));
                    return;
                }
                state.bid.dieCount = action.data.bid.dieCount;
                state.bid.dieNumber = action.data.bid.dieNumber;
                state.bid.bidder = action.client.id;

                doBroadcast(makeAction({
                    actionType: "new-bid",
                    data: {
                        bid: {dieCount: state.bid.dieCount, dieNumber: state.bid.dieNumber}
                    }
                }));

                break;
        }
        advanceUser();
        promptCurrentTurn();
    }
};

//noinspection JSUnresolvedFunction
setActionHandler(function (actionStr) {
    var action = JSON.parse(actionStr);

    switch (action.actionType) {
        case "client-joined":
            doBroadcast(action);
            break;
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
});

//noinspection JSUnresolvedFunction
setNewClientConnectionHandler(function (clientStr) {
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
});

function sendActionToClient(id, action) {
    sendActionToClients([id], action);
}

function sendActionToClients(ids, action) {
    //noinspection JSUnresolvedFunction
    sendAction(JSON.stringify(ids), JSON.stringify(action));
}

var makeAction = function (action) {
    //noinspection JSUnresolvedFunction
    return JSON.parse(createAction(JSON.stringify(action)));
};


var getRandomInt = function (min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
};

var copy = function (obj) {
    return JSON.parse(JSON.stringify(obj));
};

