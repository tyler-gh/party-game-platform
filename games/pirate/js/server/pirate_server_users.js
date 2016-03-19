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