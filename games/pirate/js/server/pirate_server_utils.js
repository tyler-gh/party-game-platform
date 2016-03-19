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