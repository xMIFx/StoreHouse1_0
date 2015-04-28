/**
 * Created by bukatinvv on 02.04.2015.
 */
var output;
var wsUri = "ws://" + document.location.host + document.location.pathname + "/chat";
websocket = new WebSocket(wsUri);

websocket.onopen = function (evt) {
    onOpen(evt)
};
websocket.onmessage = function (evt) {
    onMessage(evt)
};
websocket.onerror = function (evt) {
    onError(evt)
};


function send_message() {
    var jasonStr = JSON.stringify({
        "type": "Messages",
        "message": textID.value
    });
    doSend(jasonStr);
}

function setOutput() {
    var elements = document.getElementById("output_box").childNodes, i = 0, el;
    var re = new RegExp('chatUser', 'g');

    while (el = elements[i++]) {
        if (el.id === undefined) {
            continue;
        }
        if (el.id.match(re)) {
            output = el;
        }
    }
}

function init() {
    setOutput();
}

function onOpen(evt) {
    writeToScreen("Connected to Endpoint!");
    // doSend(textID.value);
}

function onMessage(evt) {

    parseJsonStr(evt.data);
}

function onError(evt) {
    writeToScreen('ERROR: ' + evt.data);
}

function doSend(message) {
    writeToScreen("Message Sent: " + message);
    websocket.send(message);
    //websocket.close();
}

function parseJsonStr(str) {
    var json = JSON.parse(str);
    if (json.type == "User") {
        changeOnlineStatus(json);
    }
    else if (json.type == "Messages") {
        writeMessageFromJson(json);
    }
}

function writeToScreen(message) {
    if (output === undefined) {
        setOutput();
    }
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;

    output.appendChild(pre);
}

function changeOnlineStatus(json) {
    var idForChange = "user_" + json.id;
    if (json.online) {
        if (document.getElementById(idForChange) != null) {
            if (document.getElementById(idForChange).classList.contains("offline")) {
                document.getElementById(idForChange).classList.remove("offline");
            }
            document.getElementById(idForChange).classList.add("online");
        }
        else {
            if (document.getElementById(idForChange).classList.contains("online")) {
                document.getElementById(idForChange).classList.remove("online");
            }
            document.getElementById(idForChange).classList.add("offline");
        }
    }
}

function writeMessageFromJson(json) {
    writeToScreen("Message Received: " + json.message);
}

function functionChangingChat(idUser) {
    //we need get chat id
    var json = JSON.stringify({
            "type": "Chat",
            "operation": "gettingChatID",
            "userTo": idUser
        }
    );
    doSend(json);
}