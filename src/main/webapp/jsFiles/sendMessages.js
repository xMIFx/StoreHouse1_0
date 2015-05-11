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
    //writeToScreen("Connected to Endpoint!");
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
        if (output.id == 'usersChat_' + json.chatID) {
            writeToScreenFromJson(json);
        }
        addNewMessages(json.userFrom, json.chatID);
    }

    else if (json.type == "Exception") {
        writeAboutException(json.value);
    }
    else if (json.type == "Chat") {
        writeAboutChat(json);
    }
    else if (json.type == "newMessages") {
        writeAboutCountNewMassages(json);
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
    newMessage.id = "message_" + messageJson.idMessage;
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

function writeAboutCountNewMassages(json) {
    var i;
    for (i = 0; i < json.chats.length; i++) {
        var idForChange = 'bigChat_' + json.chats[i].chatID;
        if (document.getElementById(idForChange) == null) {
            var j;
            for (j = 0; j < json.chats[i].users.length; j++) {
                if (!json.chats[i].users[j].currentUser) {
                    idForChange = 'user_' + json.chats[i].users[j].useriD;
                    break;
                }
            }
        }
        writeToScreenAbountCountNewMess(idForChange, parseInt(json.chats[i].newMessagesCount));

    }

}

function functionChangingChat(UUIDUser, idUser) {
    //we need get chat id
    var elements = document.getElementsByClassName('user_ch'), el, i = 0;
    while (el = elements[i++]) {
        if (el.classList.contains('active')) {
            el.classList.remove('active')
        }
    }
    var idForChange = "user_" + idUser;
    if (document.getElementById(idForChange) != null) {
        document.getElementById(idForChange).classList.add('active');
    }

    var json = JSON.stringify({
            "type": "Chat",
            "operation": "gettingChatID",
            "userTo": UUIDUser
        }
    );
    doSend(json);
}

function functionChangingChatByID(idChat) {
    var elements = document.getElementsByClassName('user_ch'), el, i = 0;
    while (el = elements[i++]) {
        if (el.classList.contains('active')) {
            el.classList.remove('active')
        }
    }
    var idForChange = "bigChat_" + idChat;
    if (document.getElementById(idForChange) != null) {
        document.getElementById(idForChange).classList.add('active');
    }

    var json = JSON.stringify({
            "type": "bigChat",
            "operation": "gettingChatID",
            "userTo": idChat
        }
    );
    doSend(json);
}

function writeAboutChat(json) {
    if (output === undefined) {
        setOutput();
    }

    if (output.id != 'usersChat_' + json.idChat) {
        removeChildrenRecursively(output);
        removeChildrenRecursively(document.getElementById('information_about_chat'));
        output.id = 'usersChat_' + json.idChat;
        //need check if there are this chat in last
        var valueIdForCheck = null, userTo = null, chatName = null;
        if (json.userList.length > 2) {
            valueIdForCheck = "lastChat_" + json.idChat;
            chatName = json.nameChat;
        }
        for (var i = 0; i < json.userList.length; i++) {
            if (valueIdForCheck == null && json.userList[i].cryptUUID != cookieValue) {
                valueIdForCheck = "lastUser_" + json.userList[i].id;
                userTo = json.userList[i];
                chatName = json.userList[i].name;
            }
            var pre = document.createElement("p");
            pre.innerHTML = json.userList[i].name;
            pre.id = 'chatsUsers_' + json.userList[i].cryptUUID;
            document.getElementById('information_about_chat').appendChild(pre);
        }
        addNewLastChat(valueIdForCheck, userTo, chatName);
        setArrayUsers();
        //sort by dateTime
        json.messagesList.sort(function (a, b) {
            return a.dateMessage - b.dateMessage;
        });
        for (var j = 0; j < json.messagesList.length; j++) {
            writeToScreenFromJson(json.messagesList[j]);
        }
        //scroll down
        output.scrollTop = output.offsetHeight; //output.height;
    }
}

function addNewLastChat(valueIdForCheck, userTo, chatName) {
    if (document.getElementById(valueIdForCheck) != null) {
        replaceLastChat(valueIdForCheck);

    }
    else {
        addLastChat(valueIdForCheck, userTo, chatName);
    }

}

function replaceLastChat(valueIdForCheck) {
    var elementWhereReplace = document.getElementById("lastChats");
    var elementToReplace = document.getElementById(valueIdForCheck);
    elementWhereReplace.insertBefore(elementToReplace, elementWhereReplace.firstChild)

}

function addLastChat(valueIdForAdd, userTo, chatName) {
    var elementToAppend = document.getElementById("lastChats");
    var newLastChat = document.createElement("li");
    newLastChat.id = valueIdForAdd;
    newLastChat.classList.add("user_ch");
    var newAInLastChat = document.createElement("a");
    newAInLastChat.href = "javascript:";
    if (userTo == null) {
        newAInLastChat.onclick = functionChangingChatByID(valueIdForAdd.substring(valueIdForAdd.indexOf("_") + 1, valueIdForAdd.length));
    }
    else {
        newAInLastChat.onclick = functionChangingChat(userTo.cryptUUID, userTo.id);
    }
    var newSpaninA = document.createElement("span");
    newAInLastChat.appendChild(newSpaninA);
    newAInLastChat.innerHTML = chatName;
    newLastChat.appendChild(newAInLastChat);
    elementToAppend.insertBefore(newLastChat, elementToAppend.firstChild);
    if (elementToAppend.childNodes.length > 10) {
        elementToAppend.removeChild(elementToAppend.lastChild);
    }
}

function removeChildrenRecursively(node) {
    if (!node) return;
    while (node.hasChildNodes()) {
        removeChildrenRecursively(node.firstChild);
        node.removeChild(node.firstChild);
    }
}

function addNewMessages(userFrom, chatID) {
    var idForChange = 'bigChat_' + chatID;
    if (document.getElementById(idForChange) == null) {
        idForChange = 'user_' + userFrom.id;
    }
    writeToScreenAbountCountNewMess(idForChange, 1);
}

function writeToScreenAbountCountNewMess(idForWrite, countNewMes) {
    if (document.getElementById(idForWrite) != null) {
        var elements = document.getElementById(idForWrite).childNodes, el, i = 0;
        while (el = elements[i++]) {
            if (el.classList === undefined) {
                continue;
            }
            if (el.classList.contains("MessageCount")) {
                el.innerHTML = (el.innerHTML === undefined || el.innerHTML == null || el.innerHTML == '') ? countNewMes : parseInt(el.innerHTML, 10) + countNewMes;
                break;
            }
        }
        var parentsBrothers = document.getElementById(idForWrite).parentNode.parentNode.childNodes, element, j = 0;
        while (element = parentsBrothers[j++]) {
            if (element.classList === undefined) {
                continue;
            }
            if (element.classList.contains("MessageCount")) {
                element.innerHTML = (element.innerHTML === undefined || element.innerHTML == null || element.innerHTML == '') ? countNewMes : parseInt(element.innerHTML, 10) + countNewMes;
                break;
            }

        }
    }
}

function functionOnScrollChat(div) {
    var scrolled = div.scrollTop;
    //when scrolled =0. then need more messages
    alert(scrolled + 'px');
}
