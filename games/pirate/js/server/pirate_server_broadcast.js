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
        data: {die: die}
    }));
};

var sendUserInfo = function (id, user) {
    sendActionToClient(id, makeAction({
        actionType: "user-info",
        data: user
    }));
};

var broadcastDie = function () {
    forEachUser(function (user) {
        sendDieToClient(user.id, copy(user.die));
    });
};

var broadcastLostDie = function(user) {
    doBroadcast(makeAction({
        actionType: "lost-die",
        data: {
            client_id: user.id
        }
    }));
};

var broadcastNewBid = function() {
    doBroadcast(makeAction({
        actionType: "new-bid",
        data: {
            bid: {dieCount: state.bid.dieCount, dieNumber: state.bid.dieNumber}
        }
    }));
};


//general broadcasters
var doBroadcast = function (action) {
    broadcastActions.push(action);
    //noinspection JSUnresolvedFunction
    broadcastAction(JSON.stringify(action));
};

var sendActionToClient = function(id, action) {
    sendActionToClients([id], action);
};

var sendActionToClients = function(ids, action) {
    //noinspection JSUnresolvedFunction
    sendAction(JSON.stringify(ids), JSON.stringify(action));
};

