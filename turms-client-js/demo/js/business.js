const userOneNotificationContainer = document
    .querySelector("#user-one-notification-container");
const userTwoNotificationContainer = document
    .querySelector("#user-two-notification-container");
const ERROR_PREFIX = "error: ";

function appendContainer(element, text, isError) {
    element.value = element.value
        + (element.value ? "\n" : "")
        + (isError ? "✖ " : "✔ ")
        + new Date().toLocaleTimeString() + "\n"
        + (isError ? ERROR_PREFIX : '')
        + text;
    element.scrollTop = element.scrollHeight;
}

function beautify(object) {
    if (object instanceof Error) {
        object = {
            message: object.message
        }
    }
    return JSON.stringify(object, null, "\t");
}
function setupClient(container, client, userId, password, targetId) {
    client.userService.addOnOfflineListener(sessionCloseInfo => {
        appendContainer(container, `onOffline: ${beautify(sessionCloseInfo)}`);
    });
    client.notificationService.addNotificationListener(notification => {
        appendContainer(container, `Notification: Receive a notification from other users or server: ${beautify(notification)}`);
    });
    client.messageService.addMessageListener(message => {
        appendContainer(container, `Message: Receive a message from other users or server: ${beautify(message)}`);
    });
    client.userService.login({
        userId,
        password
    })
        .then(() => {
            appendContainer(container, `login: User ${userId} has logged in`);
            // return;
            // client.messageService.queryMessagesWithTotal({
            //     ids: ['1']
            // })
            //     .then(res => appendContainer(container, `Offline messages: ${beautify(res.data)}`))
            //     .catch(error => appendContainer(container, `failed to query offline messages ${beautify(error)}`, true));
            // const intervalId = setInterval(() => {
            //     if (client.driver.isConnected) {
            //         client.messageService.sendMessage({
            //             isGroupMessage: false,
            //             targetId,
            //             deliveryDate: new Date(),
            //             text: "Hello Turms, My userId is " + userId,
            //             burnAfter: 30
            //         })
            //             .then(res => appendContainer(container, `message ${res.data} has been sent`))
            //             .catch(error => appendContainer(container, `failed to send message: ${beautify(error)}`, true));
            //     } else {
            //         clearInterval(intervalId);
            //     }
            // }, 2000);
            // client.groupService.createGroup({
            //     name: 'Turms Developers Group',
            //     intro: 'This is a group for the developers who are interested in Turms',
            //     announcement: 'nope'
            // })
            //     .then(res => appendContainer(container, `group ${res.data} has been created`))
            //     .catch(error => appendContainer(container, `failed to create group: ${beautify(error)}`, true));
        })
        .catch(reason => appendContainer(container, `failed to log in ${beautify(reason)}`, true));
}
const clientUserOne = new TurmsClient('ws://localhost:10510', 30 * 1000);
const clientUserTwo = new TurmsClient('ws://localhost:10510', 30 * 1000);

function login(userid,container,client,twoid){
    setupClient(container, client, userid, 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMDA1IiwiYXV0aGVudGljYXRlZCI6dHJ1ZSwic3RhdGVtZW50cyI6W3siZWZmZWN0IjoiQUxMT1ciLCJhY3Rpb25zIjoiUVVFUlkiLCJyZXNvdXJjZXMiOiIqIn0seyJlZmZlY3QiOiJBTExPVyIsImFjdGlvbnMiOiJDUkVBVEUiLCJyZXNvdXJjZXMiOiJNRVNTQUdFIn1dfQ.R5vVw3b1FLMS5D2uRU7Cf5sj2JIz5jeNMFR_Pa1iID5FOnDT9vkJ0mBegu-SvQQ6RvVYYpo4pAYKiIfYxSYy6mR-LNbiVJT4Ne9uEXlTdKgzEJTUVVu6UaHI4bx4kyw_ZVB5WKYYhXLhghMWqWl3OEj4B__xP9iBcfI13hG2erY4pOTXEUjbvkpSJw9vkZrZDEYYfxvwYbKGoKA4yH7auSUGWNZLHdiyfKKFWtfOfvpYIgjVTSKrNNGYOpTD3sOL4g8ZZ_6ifSKRq7LlPTryylMAeAQxAonv4_l2QcGOUb-dehSCkJ1AtMffdKbwPjzn2V_ADDtkRukLtwPm7QtZhg', twoid);
}
function loginOut(client,userid){
    client.userService.logout().then(()=>{
        appendContainer(container, `login: User ${userid} has logout`);
    })
}

function start() {
    // const USER_ONE_ID = '1005';
    // const USER_TWO_ID = '1006';
    // setupClient(userOneNotificationContainer, clientUserOne, USER_ONE_ID, '123456', USER_TWO_ID);
    // setupClient(userTwoNotificationContainer, clientUserTwo, USER_TWO_ID, '123456', USER_ONE_ID);
}
function msgTotal(client,container){
    client.messageService.queryMessagesWithTotal({
        ids: ['1'],
        areGroupMessages: false
    })
        .then(res => appendContainer(container, `Offline messages: ${beautify(res.data)}`))
        .catch(error => appendContainer(container, `failed to query offline messages ${beautify(error)}`, true));
}
function createGroup(client,container){
    client.groupService.createGroup({
            name: 'Turms Developers Group',
            intro: 'This is a group for the developers who are interested in Turms',
            announcement: 'nope'
        })
            .then(res => appendContainer(container, `group ${res.data} has been created`))
            .catch(error => appendContainer(container, `failed to create group: ${beautify(error)}`, true));
}
function addUser2Group(){
    const userId = document.getElementById("group_userId").value;
    const groupId = document.getElementById("group_id").value;
    clientUserOne.groupService.createJoinRequest({
        groupId,
        content:userId
    })
    .then(res => appendContainer(userOneNotificationContainer, `user has been add group ${res.data}`))
    .catch(error => appendContainer(userOneNotificationContainer, `failed to add group: ${beautify(error)}`, true));
}

function sendMsg(client,targetId,container){
    if (client.driver.isConnected) {
                client.messageService.sendMessage({
                    isGroupMessage: false,
                    targetId,
                    deliveryDate: new Date(),
                    text: "Hello Turms, My userId is qb" ,
                    burnAfter: 30
                })
                    .then(res => appendContainer(container, `message ${res.data} has been sent`))
                    .catch(error => appendContainer(container, `failed to send message: ${beautify(error)}`, true));
            }
}

function sendMsgGroup(client,container){
    const groupId = document.getElementById("msg_group_id").value;
    if (client.driver.isConnected) {
                client.messageService.sendMessage({
                    targetId:groupId,
                    isGroupMessage: true,
                    deliveryDate: new Date(),
                    text: "it is group messsage, Hello Turms, My userId is qb" ,
                    burnAfter: 30
                })
                    .then(res => appendContainer(container, `message ${res.data} has been sent`))
                    .catch(error => appendContainer(container, `failed to send message: ${beautify(error)}`, true));
            }
}