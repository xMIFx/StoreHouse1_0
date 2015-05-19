/**
 * Created by bukatinvv on 02.04.2015.
 */
var cloneMassage;
var UUIDGenerator = createUUID();
var output;
var informationAboutMessagesInChat = {
    numbersMessagesInChat: 0,
    thereAreAnyMessages: true,
    howMuchMessagesWeNeedAfterScroll: 15,
    howMuchScrollWas: 0,
    minDateInChat: new Date()
}
var arrayUsers = [];
var cookieValue = getCookie('chat');
var wsUri = "ws://" + document.location.host + document.location.pathname + "/chat";
var dataLocalStorage = {
    set: function (key, value) {
        if (!key || !value) {
             return;
        }

        if (typeof value === "object") {
            value = JSON.stringify(value);
        }
        localStorage.setItem(key, value);

    },
    get: function (key) {
        var value = localStorage.getItem(key);

        if (!value) {
            return null;
        }

        // assume it is an object that has been stringified
        if (value[0] === "{") {
            value = JSON.parse(value);
        }

        return value;
    },

    remove: function (key) {
        if (!key) {
            return;
        }
        localStorage.removeItem(key);
    }
}
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

function createChatObject(chatId) {
    this.type = 'Chat';
    this.chatID = parseInt(chatId, 10);
    this.messages = [];
    this.addMessage = function (message) {
        this.messages.push(message);
    }
    this.removeMessage = function (message) {
        for (var i = 0; i < this.messages.length; i++) {
            if (this.messages[i].getUUIDFromBrowser() == message.getUUIDFromBrowser()) {
                this.messages.splice(i, 1);
                break;
            }
        }
        return this.messages.length;
    }
    this.getMessages = function () {
        return this.messages;
    }
    this.getID = function () {
        return this.chatID;
    }
}

function createChatObjectFromLocalStorage(chatFromLocalStorage) {
    var chatItem = null;
    if (chatFromLocalStorage != null) {

        chatItem = new createChatObject(chatFromLocalStorage.chatID);
        for (var i = 0; i < chatFromLocalStorage.messages.length; i++) {
            chatItem.addMessage(createMessageObjectFromLocalStorage(chatFromLocalStorage.messages[i]));
        }
    }
    return chatItem;
}

function createMessageObjectFromLocalStorage(messageFromLocalStorage) {
    var message = null;
    if (messageFromLocalStorage != null) {
        message = new createMessageObject(messageFromLocalStorage.chatID
            , messageFromLocalStorage.UUIDFromBrowser
            , createUserObjectFromLocalStorage(messageFromLocalStorage.userFrom)
            , messageFromLocalStorage.newMessage
            , new Date(messageFromLocalStorage.dateMessage)
            , messageFromLocalStorage.messageText
            , messageFromLocalStorage.fromServer);
        for (var i = 0; i < messageFromLocalStorage.usersWhichDontRead.length; i++) {
            message.addUserWhichDontRead(createUserObjectFromLocalStorage(messageFromLocalStorage.usersWhichDontRead[i].userUUID));
        }
    }
    return message;

}

function createMessageObject(chatID, UUIDFromBrowser, userFrom, newMessage, dateMessage, messageText, fromServer) {
    this.type = 'Messages';
    this.userFrom = userFrom;
    this.messageText = messageText;
    this.newMessage = newMessage;
    this.dateMessage = dateMessage;
    this.chatID = parseInt(chatID, 10);
    this.UUIDFromBrowser = UUIDFromBrowser;
    this.messageID = null;
    this.usersWhichDontRead = [];
    this.fromServer = fromServer;
    this.dateMessageInTime = this.dateMessage.getTime();
    this.exceptionWhenSending = false;

    //Setters
    this.setMessageID = function (messageID) {
        this.messageID = parseInt(messageID, 10);
    }
    this.setUUUIDFromBrowser = function () {
        this.UUIDFromBrowser = UUIDGenerator.generate();
    }
    this.addUserWhichDontRead = function (user) {
        this.usersWhichDontRead.push(user);
    }
    this.setExceptionWhenSending = function (ExceptionWhenSending) {
        this.exceptionWhenSending = ExceptionWhenSending;
    }
    //Getters
    this.getUserFrom = function () {
        return this.userFrom;
    }
    this.isNewMessage = function () {
        return this.newMessage;
    }
    this.getDateMessage = function () {
        return this.dateMessage;
    }
    this.getChatID = function () {
        return this.chatID;
    }
    this.getUUIDFromBrowser = function () {
        return this.UUIDFromBrowser;
    }
    this.getMessageID = function () {
        return this.messageID;
    }
    this.getUserWhichDontRead = function () {
        return this.usersWhichDontRead;
    }
    this.getMessageText = function () {
        return this.messageText;
    }
    this.isFromServer = function () {
        return this.fromServer;
    }
    this.getDateMessageInTime = function () {
        return this.dateMessageInTime;
    }
    this.isExceptionWhenSending = function () {
        return this.exceptionWhenSending;
    }

    this.addToLocalStorage = function () {
        var chatItem = createChatObjectFromLocalStorage(dataLocalStorage.get("chat_" + this.getChatID()));
        if (chatItem == null) {
            chatItem = new createChatObject(this.getChatID())
        }
        chatItem.addMessage(this);
        dataLocalStorage.set("chat_"+chatItem.chatID, chatItem);
    }
    this.removeFromLocalStorage = function () {
        var chatItem = createChatObjectFromLocalStorage(dataLocalStorage.get("chat_" + this.getChatID()));
        if (chatItem != null) {
            if (chatItem.removeMessage(this) == 0) {
                dataLocalStorage.remove(chatItem.getID());
            }
            else {
                dataLocalStorage.set("chat_"+chatItem.getID(), chatItem);
            }
        }
    }


    //ToJSON
    this.toJSON = function (key) {
        var replacement = new Object();
        for (var val in this) {
            if (val == 'userFrom') {
                replacement[val] = this[val].userUUID;
            }
            else {
                replacement[val] = this[val];
            }
        }
        return replacement;
    }
}

function createUserObject(name, login, userUUID) {
    this.type = 'User';
    this.userName = name;
    this.userLogin = login;
    this.userUUID = userUUID;
    this.currentUser = (userUUID == cookieValue);

}

function createUserObjectFromLocalStorage(userFromLocalStorage) {
    var user = null;
    if (userFromLocalStorage != null) {
        user = getUserFromCurrentChatByUUID(userFromLocalStorage);
    }
    return user;
}

function createMessageObjectFromJson(messageJson) {
    var message = new createMessageObject(messageJson.chatID
        , messageJson.uuidfromBrowser
        , new createUserObject(messageJson.userFrom.name
            , messageJson.userFrom.login
            , messageJson.userFrom.cryptUUID)
        , messageJson.newMessage
        , new Date(messageJson.dateMessage)
        , messageJson.message
        , true);
    message.setMessageID(messageJson.idMessage);
    message.setExceptionWhenSending(messageJson.exceptionWhenSending);

    for (var j = 0; j < messageJson.usersWhichDontRead.length; j++) {
        message.addUserWhichDontRead(new createUserObject(messageJson.usersWhichDontRead[j].name
            , messageJson.usersWhichDontRead[j].login
            , messageJson.usersWhichDontRead[j].cryptUUID));

    }
    return message;
}

function getCookie(name) {
    var matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

function createUUID() {
    var self = {};
    var lut = [];
    for (var i = 0; i < 256; i++) {
        lut[i] = (i < 16 ? '0' : '') + (i).toString(16);
    }
    self.generate = function () {
        var d0 = Math.random() * 0xffffffff | 0;
        var d1 = Math.random() * 0xffffffff | 0;
        var d2 = Math.random() * 0xffffffff | 0;
        var d3 = Math.random() * 0xffffffff | 0;
        return lut[d0 & 0xff] + lut[d0 >> 8 & 0xff] + lut[d0 >> 16 & 0xff] + lut[d0 >> 24 & 0xff] + '-' +
            lut[d1 & 0xff] + lut[d1 >> 8 & 0xff] + '-' + lut[d1 >> 16 & 0x0f | 0x40] + lut[d1 >> 24 & 0xff] + '-' +
            lut[d2 & 0x3f | 0x80] + lut[d2 >> 8 & 0xff] + '-' + lut[d2 >> 16 & 0xff] + lut[d2 >> 24 & 0xff] +
            lut[d3 & 0xff] + lut[d3 >> 8 & 0xff] + lut[d3 >> 16 & 0xff] + lut[d3 >> 24 & 0xff];
    }
    return self;
}

function send_message() {
    if (output.id != 'usersChat_0') {
        var mes = new createMessageObject(output.id.substring(output.id.indexOf("_") + 1, output.id.length)
            , UUIDGenerator.generate()
            , getCurrentUser()
            , true
            , new Date()
            , document.getElementById('textID').value
            , false);
        var jsonStr = JSON.stringify(mes);
        dontReadedUsers = getUsersFromCurrentChatWithoutCurrent();
        for (var i = 0; i < dontReadedUsers.length; i++) {
            mes.addUserWhichDontRead(dontReadedUsers[i]);
        }
        mes.addToLocalStorage();
        writeMessageToScreen(mes);
        doSend(jsonStr);
    }
}

function addToArrayUsers(jsonUser) {
    var user = new createUserObject(jsonUser.name, jsonUser.login, jsonUser.cryptUUID);
    arrayUsers.push(user);
}

function getCurrentUser() {
    var curUser;
    for (var i = 0; i < arrayUsers.length; i++) {
        if (arrayUsers[i].currentUser) {
            curUser = arrayUsers[i];
        }
    }
    if (curUser == null) {
        curUser = new createUserObject('', '', '');
    }
    return curUser;
}

function getUsersFromCurrentChatWithoutCurrent() {
    var otherUsers = [];
    for (var i = 0; i < arrayUsers.length; i++) {
        if (!arrayUsers[i].currentUser) {
            otherUsers.push(arrayUsers[i]);
        }
    }
    return otherUsers;
}

function getUserFromCurrentChatByUUID(UUID) {
    var user;
    for (var i = 0; i < arrayUsers.length; i++) {
        if (arrayUsers[i].userUUID == UUID) {
            user = arrayUsers[i];
        }
    }
    if (user == null) {
        user = new createUserObject('', '', '');
    }
    return user;
}

function setOutput() {
    cloneMassage = document.getElementById("cloneMessage");
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
            var message = createMessageObjectFromJson(json);
            writeMessageToScreen(message, false);
        }
        if (cookieValue != json.userFrom.cryptUUID) {
            addNewMessages(json.userFrom, json.chatID);
        }
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
    var elementForChange = document.getElementById("user_" + json.id);

    if (elementForChange != null) {
        if (json.online) {
            if (elementForChange.classList.contains("offline")) {
                delementForChange.classList.remove("offline");
            }
            elementForChange.classList.add("online");
        }
        else {
            if (elementForChange.classList.contains("online")) {
                elementForChange.classList.remove("online");

            }
            elementForChange.classList.add("offline");
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

function writeMessageToScreen(message, beggining) {
    if (output === undefined) {
        setOutput();
    }
    var sendingMessage = document.getElementById("message_" + message.getUUIDFromBrowser());
    if (sendingMessage == null
        && document.getElementById("message_" + message.getUUIDFromBrowser() + "_" + message.getMessageID()) == null) {
        var newMessage = cloneMassage.cloneNode(true);
        newMessage.classList.remove("CloneClass");
        if (!message.isFromServer()) {
            newMessage.classList.add('SendingMessage');
            newMessage.id = "message_" + message.getUUIDFromBrowser();
        }
        else {
            newMessage.id = "message_" + message.getUUIDFromBrowser() + "_" + message.getMessageID();
        }
        if (message.isNewMessage()) {
            newMessage.classList.add('NewMessage');
        }
        if (message.getUserFrom().currentUser) {
            newMessage.classList.add('MyMessage');
        }
        var options = {
            weekday: "long", year: "numeric", month: "short", hour12: false,
            day: "numeric", hour: "2-digit", minute: "2-digit", second: "2-digit"
        };
        var elements = newMessage.childNodes, el, i = 0;
        while (el = elements[i++]) {
            if (el.classList === undefined) {
                continue;
            }
            var text = null;
            var usersWichDontRead = message.getUserWhichDontRead();
            if (el.classList.contains('WhoWright')) {
                text = message.getUserFrom().userName + " " + (message.getDateMessage()).toLocaleTimeString(navigator.language, options);
            }
            else if (el.classList.contains('WhoDontRead')
                && usersWichDontRead.length > 0) {
                text = "Doesn't read: " + usersWichDontRead[0].userLogin;
                for (var j = 1; j < usersWichDontRead.length; j++) {
                    text = text + "; " + usersWichDontRead[j].userLogin;

                }
            }
            if (el.classList.contains('MessageText')) {
                text = message.getMessageText();
            }
            if (text != null) {
                el.innerHTML = text;
            }
        }
        if (beggining) {
            output.insertBefore(newMessage, output.firstChild)
        }
        else {
            output.appendChild(newMessage);
            output.scrollTop = output.scrollHeight;
        }
        if (informationAboutMessagesInChat.minDateInChat > message.getDateMessage()) {
            informationAboutMessagesInChat.minDateInChat = message.getDateMessage();
        }
        informationAboutMessagesInChat.numbersMessagesInChat++;
    }
    else if (sendingMessage != null && message.isFromServer()) {
        if (sendingMessage.classList.contains("SendingMessage")) {
            sendingMessage.classList.remove("SendingMessage");
        }
        if(message.isExceptionWhenSending() && !sendingMessage.classList.contains("ExceptionMessage")){
            sendingMessage.classList.add("ExceptionMessage");
        }
        else {
            sendingMessage.id = sendingMessage.id + "_" + message.getMessageID();
            if (sendingMessage.classList.contains("ExceptionMessage")) {
                sendingMessage.classList.remove("ExceptionMessage");
            }
            if (informationAboutMessagesInChat.minDateInChat > message.getDateMessage()) {
                informationAboutMessagesInChat.minDateInChat = message.getDateMessage();
            }
            informationAboutMessagesInChat.numbersMessagesInChat++;
            message.removeFromLocalStorage();
        }
    }
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
        writeToScreenAboutCountNewMess(idForChange, parseInt(json.chats[i].newMessagesCount));

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
            "userFrom": cookieValue,
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
            "userFrom": cookieValue,
            "chatID": parseInt(idChat, 10)
        }
    );
    doSend(json);
}

function writeAboutChat(json) {
    if (output === undefined) {
        setOutput();
    }
    var chatItem = createChatObjectFromLocalStorage(dataLocalStorage.get("chat_" + json.idChat));
    var beginning = false;
    var arrayMessages;
    if (chatItem == null) {
        arrayMessages = [];
    }
    else {
        arrayMessages = chatItem.getMessages();
    }
    for (var j = 0; j < json.messagesList.length; j++) {
        arrayMessages.push(createMessageObjectFromJson(json.messagesList[j]));
    }
    informationAboutMessagesInChat.thereAreAnyMessages = json.thereSomeMoreMessages;
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
            addToArrayUsers(json.userList[i]);
        }
        addNewLastChat(valueIdForCheck, userTo, chatName);

        //sort by dateTime
        arrayMessages.sort(function (a, b) {
            return a.dateMessage.getTime() - b.dateMessage.getTime();
        });
        //scroll down
        // in messageWriter output.scrollTop = output.offsetHeight; //output.height;
    } else if (output.id == 'usersChat_' + json.idChat) {
        arrayMessages.sort(function (a, b) {
            return b.dateMessage.getTime() - a.dateMessage.getTime();
        });
        beginning = true;
    }
    // deleting messages from arrayMessages if they are from local storage and date < min date message from server
    if (json.messagesList.length > 0 && informationAboutMessagesInChat.thereAreAnyMessages) {
        var countIter = arrayMessages.length;
        var deleteFrom = 0;
        for (var k = 0; k < countIter; k++) {
            if (beginning) {
                deleteFrom = arrayMessages.length;
            }
            else {
                deleteFrom = 0;
            }

            if (arrayMessages[deleteFrom].isFromServer()) {
                break;
            }
            else {
                arrayMessages.splice(deleteFrom, 1);
            }

        }
    }
    for (var j = 0; j < arrayMessages.length; j++) {
        writeMessageToScreen(arrayMessages[j], beginning);
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
    writeToScreenAboutCountNewMess(idForChange, 1);
}

function writeToScreenAboutCountNewMess(idForWrite, countNewMes) {
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
    if (scrolled == 0 && informationAboutMessagesInChat.thereAreAnyMessages) {
        informationAboutMessagesInChat.howMuchScrollWas++;
        if (informationAboutMessagesInChat.howMuchScrollWas > 2 && informationAboutMessagesInChat.howMuchScrollWas < 5) {
            informationAboutMessagesInChat.howMuchMessagesWeNeedAfterScroll = 25;
        }
        else if (informationAboutMessagesInChat.howMuchScrollWas >= 5 && informationAboutMessagesInChat.howMuchScrollWas < 8) {
            informationAboutMessagesInChat.howMuchMessagesWeNeedAfterScroll = 35;
        }
        else if (informationAboutMessagesInChat.howMuchScrollWas >= 8) {
            informationAboutMessagesInChat.howMuchMessagesWeNeedAfterScroll = 50;
        }
        var json = JSON.stringify({
                "type": "MoreMessages",
                "operation": "gettingMoreMessageinChat",
                "chatID": parseInt(output.id.substring(output.id.indexOf("_") + 1, output.id.length), 10),
                "userFrom": cookieValue,
                "numberMessagesAlreadyInChat": informationAboutMessagesInChat.numbersMessagesInChat,
                "minDateInChat": informationAboutMessagesInChat.minDateInChat.getTime(),
                "howMuchWeNeed": informationAboutMessagesInChat.howMuchMessagesWeNeedAfterScroll
            }
        );
        doSend(json);

    }
}

function checkIfElementInDivScope(whereCheck, el) {
    var itIs = false;
    var scrollTop = whereCheck.scrollTop;
    var windowHeight = whereCheck.offsetHeight;
    var elementOffset = el.offsetTop;
    var elementHeight = el.offsetHeight;
    if (scrollTop <= (elementOffset - elementHeight) && (elementOffset - elementHeight <= (scrollTop + windowHeight))) {
        itIs = true;
    }
    return itIs;

}