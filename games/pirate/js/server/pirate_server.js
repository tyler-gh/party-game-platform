//----------------------------communcation file----------------------------

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

//----------------------------logic file----------------------------

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
    userIndexes: {0: 0}
};

//////creating die//////
var generateDie = function () {
    forEachUser(function (user) {
        user.die = [];
        for (var j = 0; j < user.numberOfDie; j++) {
            user.die.push(getRandomInt(1, 6));
        }
    });
};

//////advance turns//////
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

//////current turn action//////
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

var isValidBid = function(action)
{
    var isInvalidBid =
        (state.bid.dieNumber >= action.data.bid.dieNumber && state.bid.dieCount >= action.data.bid.dieCount) ||
        state.bid.dieCount > action.data.bid.dieCount ||
        action.data.bid.dieNumber <= 1 ||
        action.data.bid.dieNumber > 6;

    return !isInvalidBid
}

var setBid = function(action)
{
    state.bid.dieCount = action.data.bid.dieCount;
    state.bid.dieNumber = action.data.bid.dieNumber;
    state.bid.bidder = action.client.id;
}
var clearBid = function()
{
    state.bid.dieCount = -1;
    state.bid.dieNumber = -1;
    state.bid.bidder = -1;
}

var lieAction = function (action) {
    var user;
    if (didLie()) {
        user = state.users[state.userIndexes[state.bid.bidder]];
    } else {
        user = state.users[state.currentUserIndex];
    }
    clearBid();

    user.numberOfDie--;
    broadcastLostDie(user);


    generateDie();
    broadcastDie();

    if (checkWinner()) {
        state.finished = true;
        return;
    }
    state.currentUserIndex = state.userIndexes[user.id] - 1;
    advanceUser();
    promptCurrentTurn();
}

//bidAction

var bidAction = function (action)
{
    if (!isValidBid(action)){
        sendActionToClient(action.client.id, makeAction({actionType: "invalid-bid"}));
        return;
    }
    setBid(action);
    broadcastNewBid();

    advanceUser();
    promptCurrentTurn();
}

var takeTurn = function (action) {
    if (action.client.id == state.users[state.currentUserIndex].id) {
        switch (action.data.responseType) {
            case "lie":
                lieAction(action);
                break;
            case "bid":
                bidAction(action);
                break;
        }

    }
};

//----------------------------users file----------------------------

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

//----------------------------utils file----------------------------

var forEachUser = function (f) {
    for (var i = 1; i < state.users.length; i++) {
        f(state.users[i]);
    }
};
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