var state = {
    finished: false,
    waitingOnRevealingDice: false,
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
        numberOfDie: -1,
        needsToRoll:false
    }],
    userIndexes: {0: 0}
};
///-------------------start turn stage---------------------///
var startNextRound = function()
{
    if (checkWinner()) {
        state.finished = true;
        broadcastGameWinner(getWinner())
        return;
    }
    state.roundStarted = true;
    generateDie();
    beginRolling();
};

//TODO refactor this
var getWinner = function () {
    var numWinners = 0;
    var userWinner;
    forEachUser(function (user) {
        if (user.numberOfDie > 0) {
            numWinners++;
            userWinner = user;
        }
    });

    if(numWinners != 1)
    {
        //BAD!
        //TODO: throw error?
        return null
    }

    return userWinner;
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
var generateDie = function () {
    forEachUser(function (user) {
        user.die = [];
        for (var j = 0; j < user.numberOfDie; j++) {
            user.die.push(getRandomInt(1, 6));
        }
    });
};
///-----------------------------roll stage---------------------------///
var beginRolling = function()
{
    forEachUser(function (user) {
        user.needsToRoll = true
    });
    broadcastBeginRolling();
};

var clientRolled = function (action) {
    var user = state.users[state.userIndexes[action.client.id]];
    user.needsToRoll = false;

    sendDieToClient(user.id, copy(user.die));

    //tell the main screen that this client has rolled
    reportClientRolled(user)

    if(allClientsHaveRolled())
    {
        broadcastAllClientsDoneRolling()
        beginTurn()
    }
};

var allClientsHaveRolled = function()
{
    var allClientsReady = true
    forEachUser(function (user) {
        if(user.needsToRoll)
            allClientsReady = false
    });
    return allClientsReady
};

///-----------------------------bid stage---------------------------///
var beginTurn = function()
{
    advanceUser();

    //TODO refactor? this could be the same thing, maybe its good to be separate though
    broadcastCurrentTurn(state.users[state.currentUserIndex]);

    promptCurrentTurn(state.users[state.currentUserIndex]);

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

var bidAction = function (action) {
    if (!isValidBid(action)) {
        promptInvalidBid(action.client.id);
        return;
    }
    setBid(action);
    broadcastNewBid(state.bid);

    beginTurn();
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

///-----------------------------show die stage---------------------------///

var lieAction = function (action) {
    if (state.bid.bidder == -1)
    {
        promptNoBid(action.client.id);
        return;
    }
    var user;
    if (didLie()) {
        user = state.users[state.userIndexes[state.bid.bidder]];
    } else {
        user = state.users[state.currentUserIndex];
    }

    //this will be the user who will lose the dice after the dice has been revealed
    state.currentUserIndex = state.userIndexes[user.id];

    clearBid();
    state.waitingOnRevealingDice = true
    broadcastShowDice();

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

var clearBid = function () {
    state.bid.dieCount = -1;
    state.bid.dieNumber = -1;
    state.bid.bidder = -1;
};

var roundOver = function (action) {
    state.waitingOnRevealingDice = false

    var user = state.users[state.currentUserIndex]
    user.numberOfDie--;
    broadcastLostDie(user);

    state.currentUserIndex = state.userIndexes[user.id] - 1;
    startNextRound();
}
