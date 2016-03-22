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

var isValidBid = function (action) {
    var isInvalidBid =
        (state.bid.dieNumber >= action.data.bid.dieNumber && state.bid.dieCount >= action.data.bid.dieCount) ||
        state.bid.dieCount > action.data.bid.dieCount ||
        action.data.bid.dieNumber <= 1 ||
        action.data.bid.dieNumber > 6;

    return !isInvalidBid
};

var setBid = function (action) {
    state.bid.dieCount = action.data.bid.dieCount;
    state.bid.dieNumber = action.data.bid.dieNumber;
    state.bid.bidder = action.client.id;
};
var clearBid = function () {
    state.bid.dieCount = -1;
    state.bid.dieNumber = -1;
    state.bid.bidder = -1;
};

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

    if (checkWinner()) {
        state.finished = true;
        return;
    }

    generateDie();
    broadcastDie();

    state.currentUserIndex = state.userIndexes[user.id] - 1;
    advanceUser();
    promptCurrentTurn();
};

//bidAction

var bidAction = function (action) {
    if (!isValidBid(action)) {
        sendActionToClient(action.client.id, makeAction({actionType: "invalid-bid"}));
        return;
    }
    setBid(action);
    broadcastNewBid();

    advanceUser();
    promptCurrentTurn();
};

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