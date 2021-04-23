function sendToBrowse() {
    let username = document.getElementById("username-input").value;
    if(username.length === 0){
        username = "Anonymous user"
    }
    window.location.href = "/browse.html?user=" + username;
}

function setNameInputFocus() {
    document.getElementById("username-input").focus();
}