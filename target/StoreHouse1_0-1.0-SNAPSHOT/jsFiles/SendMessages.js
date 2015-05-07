/**
 * Created by bukatinvv on 02.04.2015.
 */

var output;
var arrayUsers = [];
var cookieValue = getCookie('chat');
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

function closeIt() {
    websocket.close();
}
window.onunload = closeIt;

function getCookie(name) {
    var matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

function send_message() {
    if (output.id != 'usersChat_0') {

        var jsonStr = JSON.stringify({
            "type": 'Messages',
            "userFrom": cookieValue,
            "message": document.getElementById('textID').value,
            "newMessage": true,
            "dateMessage": new Date().getTime(),
            "chatID": output.id.substring(output.id.indexOf("_") + 1, output.id.length)
        });
        doSend(jsonStr);
    }
}

function setArrayUsers() {
    var elementsUsersChat = document.getElementById('information_about_chat').childNodes, strId, elUser, j = 0;
    var reUs = new RegExp('chatsUsers', 'g');
    while (elUser = elementsUsersChat[j++]) {
        strId = elUser.id;
        if (strId === undefined) {
            continue;
        }
        else if (strId.match(reUs)) {
            arrayUsers.push(strId.substring(11, strId.length));//11 = 'chatsUsers'.length+1
        }

    }
}

function setOutput() {
    var elements = document.getElementById("output_box").childNodes, i = 0, el;
    var re = new RegExp('usersChat', 'g');

    while (el = elements[i++]) {
        if (el.id === undefined) {
            continue;
        }
        if (el.id.match(re)) {
            output = el;
            break;
        }
    }
    setArrayUsers();
}

function initOutput() {
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

    websocket.send(message);
    //websocket.close();
}


function parseJsonStr(str) {
    var json = JSON.parse(str);
    if (json.type == "User") {
        changeOnlineStatus(json);
    }
    else if (json.type == "Messages") {
        if (output.id == 'usersChat_' + json.chatID)
            writeToScreenFromJson(json);
        else {
            addNewMessages(json.userFrom);
        }
    }

    else if (json.type == "Exception") {
        writeAboutException(json.value);
    }
    else if (json.type == "Chat") {
        writeAboutChat(json);
    }
    else {
        writeToScreen(json.type)
        writeToScreen(str)
    }
}

function changeOnlineStatus(json) {
    var idForChange = "user_" + json.id;
    if (document.getElementById(idForChange) != null) {
        if (json.online) {
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

function writeAboutException(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;

    document.getElementById('output_box').appendChild(pre);
}

function writeToScreen(message) {
    if (output === undefined) {
        setOutput();
    }
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    //  pre.style.textAlign = "right"; if current user
    pre.innerHTML = message;

    output.appendChild(pre);
}

function writeToScreenFromJson(messageJson) {
    if (output === undefined) {
        setOutput();
    }
    var newMessage = document.createElement("div");
    newMessage.classList.add('MessageClass');
    if (messageJson.newMessage) {
        newMessage.classList.add('NewMessage');
    }
    if (cookieValue == messageJson.userFrom.cryptUUID) {
        newMessage.classList.add('MyMessage');
    }

    //from
    var options = {
        weekday: "long", year: "numeric", month: "short", hour12: false,
        day: "numeric", hour: "2-digit", minute: "2-digit", second: "2-digit"
    };
    var whoWright = document.createElement("p");
    whoWright.innerHTML = messageJson.userFrom.login + " " + (new Date(messageJson.dateMessage)).toLocaleTimeString(navigator.language, options);
    whoWright.classList.add('WhoWright');
    //message
    var pre = document.createElement("p");
    pre.innerHTML = messageJson.message;
    pre.classList.add('MessageText');
    newMessage.appendChild(whoWright);
    newMessage.appendChild(pre);
    output.appendChild(newMessage);
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

function writeAboutChat(json) {
    if (output === undefined) {
        setOutput();
    }
    if (output.id != 'usersChat_' + json.idChat) {
        output.id = 'usersChat_' + json.idChat;
        for (var i = 0; i < json.userList.length; i++) {
            var pre = document.createElement("p");
            pre.innerHTML = json.userList[i].name;
            pre.id = 'chatsUsers_' + json.userList[i].cryptUUID;
            document.getElementById('information_about_chat').appendChild(pre);
        }
        setArrayUsers();
        //sort by dateTime
        json.messagesList.sort(function (a, b) {
            return a.dateMessage - b.dateMessage;
        });
        for (var j = 0; j < json.messagesList.length; j++) {
            writeToScreenFromJson(json.messagesList[j]);
        }
    }
}

function addNewMessages(userFrom) {
    var idForChange = "user_" + userFrom.id;
    if (document.getElementById(idForChange) != null) {
        var elements = document.getElementById(idForChange).childNodes, el, i = 0;
        while (el = elements[i++]) {
            if (el.classList === undefined) {
                continue;
            }
            if (el.classList.contains("MessageCount")) {
                alert(el.innerHTML);
                el.innerHTML = (el.innerHTML === undefined||el.innerHTML==null||el.innerHTML=='') ? 1 : parseInt(el.innerHTML, 10) + 1;
            }
        }
    }
}