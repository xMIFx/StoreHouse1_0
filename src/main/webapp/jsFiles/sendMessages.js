/**
 * Created by bukatinvv on 02.04.2015.
 */
var wsUri = "ws://localhost:8081/chat";
function send_message() {
    websocket = new WebSocket(wsUri);
    websocket.onopen = function(evt) {
        onOpen(evt)
    };
    websocket.onmessage = function(evt) {
        onMessage(evt)
    };
    websocket.onerror = function(evt) {
        onError(evt)
    };
}
function init() {
    output = document.getElementById("output");
}

function onOpen(evt) {
    writeToScreen("Connected to Endpoint!");
    doSend(textID.value);
}
function onMessage(evt) {
    writeToScreen("Message Received: " + evt.data);
}
function onError(evt) {
    writeToScreen('ERROR: ' + evt.data);
}
function doSend(message) {
    writeToScreen("Message Sent: " + message);
    websocket.send(message);
    //websocket.close();
}
function writeToScreen(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;

    output.appendChild(pre);}
