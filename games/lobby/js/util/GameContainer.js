var GameContainer = function (api, dom) {
    this.api = api;
    this.dom = dom;
    this.users = {};
    this.SOCKET = "ws";
    this.actionListeners = [];
    this.userListeners = [];
    this.preGame = true;
    this.userInfo = null;
};

GameContainer.prototype.onConnect = function () {
    this.api.socketSend(this.SOCKET, JSON.stringify({actionType: "hi guys"}));
};

GameContainer.prototype.onDisconnect = function () {
    // TODO we should have the client close the connection instead
    window.location.href = '/';
};

GameContainer.prototype.onError = function (event) {

};

GameContainer.prototype.userEvent = function() {
    var usersCopy = JSON.parse(JSON.stringify(this.users));
    this.userListeners.forEach(function(listener){
        listener(usersCopy);
    }.bind(this));
};

GameContainer.prototype.addUserListener = function (listener) {
    this.userListeners.push(listener);
};

GameContainer.prototype.addActionListener = function (listener) {
    this.actionListeners.push(listener);
};

GameContainer.prototype.onPreGameMessage = function (data) {
    switch (data.actionType) {
        case "client-joined":
            this.users[data.client.id] = {id: data.client.id, name: data.client.name, color: data.client.color};
            this.userEvent();
            break;
        case "client-left":
            this.users[data.client.id] = undefined;
            this.userEvent();
            break;
        case "start-game":
            this.preGame = false;
            window.gameStart(this.dom, new GameApi(this.addActionListener.bind(this), this.api), this.users, this.userInfo);
            break;
        case "countdown-started":
            var myElement = document.getElementById('WaitingRoom');

            myElement.clickStart_Action();
            break;
        case "countdown-cancelled":
            window.clickStart_Action();
            break;
        case "user-info":
            this.userInfo = data.data;
            break;
    }
};

GameContainer.prototype.onDuringGameMessage = function (data) {
    this.actionListeners.forEach(function (listener) {
        listener(data);
    });
};

GameContainer.prototype.onMessage = function (event) {
    var data = JSON.parse(event.data);
    if (this.preGame) {
        this.onPreGameMessage(data);
    } else {
        this.onDuringGameMessage(data);
    }
};

GameContainer.prototype.connect = function () {
    this.api.connectSocket(
        this.SOCKET,
        this.onConnect.bind(this),
        this.onDisconnect.bind(this),
        this.onMessage.bind(this),
        this.onError.bind(this)
    );
};

var GameApi = function(addActionListener, api) {
    this.addActionListener = addActionListener;
    this.api = api;
};

GameApi.prototype.send = function(action) {
    this.api.socketSend("ws", JSON.stringify(action));
};

GameApi.prototype.callLiar = function() {
    this.send({
        actionType: "take-turn",
        data: {
            responseType: "lie"
        }
    });
};

GameApi.prototype.makeBid = function(bid) {
    this.send({
        actionType: "take-turn",
        data: {
            responseType: "bid",
            bid: bid
        }
    });
};
