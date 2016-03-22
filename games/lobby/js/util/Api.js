
var API = function(){
    this.sockets = {};
};

API.prototype.postJson = function(url, obj, success) {
    $.ajax(url, {
       data : JSON.stringify(obj),
       contentType : 'application/json',
       type : 'POST'
   }).then(success);
};

API.prototype.get = function(url, success, error) {
    $.ajax(url, {
        type : 'GET'
    }).done(success).fail(error);
};

API.prototype.getGameDefinitions = function(success, error) {
    this.get("/game_definition", success, error);
};

API.prototype.createGame = function(gameId, success) {
    this.postJson("/create_game", {'game_id': gameId}, success);
};

API.prototype.joinGame = function(userName, color, gameId, gameInstanceId, success) {
    this.postJson("/join_game", {"user_name": userName, "color": color, "game_id": gameId, "game_instance_id":gameInstanceId}, success);
};

API.prototype.gameExists = function(gameId, gameInstanceId, success, error) {
    $.ajax({
        url: "/game/exists",
        type: "GET", 
        data: {"game_id": gameId, "game_instance_id": gameInstanceId},
        success: success,
        error: error
    });
};

API.prototype.leaveGame = function(success) {
    this.postJson("/leave_game",{}, success);
};

API.prototype.socketAddress = function(name) {
    var loc = window.location,
        new_uri = loc.protocol === "https:" ? "wss:" : "ws:";

    return new_uri + "//" + loc.host + "/" + name;
};

API.prototype.connectSocket = function(name, onOpen, onClose, onMessage, onError) {
    var websocket = new WebSocket(this.socketAddress(name));
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror = onError;
    this.sockets[name] = websocket;
};

API.prototype.socketSend = function(name, data) {
    if(this.sockets[name]) {
        this.sockets[name].send(data);
    }
};

API.prototype.closeSocket = function(name) {
    var socket = this.sockets[name];
    if(socket) {
        socket.close();
    }
    this.sockets[name] = undefined;
};

API.prototype.socketClosed = function(name) {
    return !this.sockets[name] || this.sockets[name].readyState > 1;
};

var Api = new API();