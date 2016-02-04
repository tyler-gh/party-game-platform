var ResponseParse = function(str) {
    try {
        return JSON.parse(str);
    } catch (e) {
        return str;
    }
};

var ClientLoad = function(client, successFunction, errorFunction) {
    return function() {
        if (client.readyState === 4) {
            if (client.status >= 200 && client.status < 300) {
                if(successFunction) {
                    successFunction(ResponseParse(client.responseText), client.status);
                }
            } else if(errorFunction) {
                errorFunction(ResponseParse(client.responseText), client.status);
            }
        }
    };
};

var Entity = function(url, name) {
    this.url = url;
    this.name = name;
};

Entity.prototype.send = function(method, successFunction, errorFunction) {
    var client = new XMLHttpRequest();
    client.open(method, this.url + "/" + this.name, true);
    client.onload = ClientLoad(client, successFunction, errorFunction);
    client.send();
    console.log("sent:" + this.url + "/" + this.name);
};

Entity.prototype.post = function(successFunction, errorFunction) {
    this.send("POST", successFunction, errorFunction);
};

Entity.prototype.get = function(successFunction, errorFunction) {
    this.send("GET", successFunction, errorFunction);
};

Entity.prototype.delete = function(successFunction, errorFunction) {
    this.send("DELETE", successFunction, errorFunction);
};

Entity.prototype.put = function(successFunction, errorFunction) {
    this.send("PUT", successFunction, errorFunction);
};

Entity.prototype.one = function(name) {
    return new Entity(this.url, this.name + "/" + name);
};

var Rest = function(site, port) {
    if(port) {
        site += ":" + port;
    }
    this.url = "http://" + site;
    this.ws = "ws://" + site;
    this.sockets = {};
};

Rest.prototype.one = function(name, id) {
    return new Entity(this.url, name).one(id);
};

Rest.prototype.all = function(name) {
    return new Entity(this.url, name);
};

Rest.prototype.socket = function(name, onOpen, onClose, onMessage, onError) {
    if(this.sockets[name]) {
        this.sockets[name].close();
    }
    var websocket = new WebSocket(this.ws + "/" + name);
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror = onError;
    this.sockets[name] = websocket;
};

Rest.prototype.socketSend= function(name, data) {
    if(this.sockets[name]) {
        this.sockets[name].send(data);
    }
};

Rest.prototype.closeSocket = function(name) {
    var socket = this.sockets[name];
    if(socket) {
        socket.close();
    }
};

Rest.prototype.socketClosed = function(name) {
    return !this.sockets[name] || this.sockets[name].readyState > 1;
};