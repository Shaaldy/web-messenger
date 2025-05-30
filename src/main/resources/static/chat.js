const socket = new WebSocket("ws://" + window.location.host + "/ws/chat");
const dialogs = new Map();
const avatars = new Map();
let currentLogin = null;

const myLogin = localStorage.getItem("myLogin");
localStorage.setItem("myLogin", myLogin);

socket.onmessage = function (event) {
    const msg = JSON.parse(event.data);
    const login = msg.fromLogin === myLogin ? msg.toLogin : msg.fromLogin;

    if (!dialogs.has(login)) dialogs.set(login, []);
    dialogs.get(login).push(msg);

    if (msg.fromAvatar) avatars.set(msg.fromLogin, msg.fromAvatar);
    if (msg.fromLogin === myLogin && msg.fromAvatar) avatars.set(myLogin, msg.fromAvatar);

    console.log("📥 fromAvatar:", msg.fromAvatar);

    if (login === currentLogin) renderMessages();
    renderDialogsList();
};

function send() {
    const content = document.getElementById("msg").value.trim();
    if (!currentLogin || !content) return;

    const msg = {
        fromLogin: myLogin,
        toLogin: currentLogin,
        fromAvatar: avatars.get(myLogin),
        content: content
    };

    socket.send(JSON.stringify({
        toLogin: currentLogin,
        content: content,
        type: "CHAT"
    }));

    dialogs.get(currentLogin).push(msg);
    document.getElementById("msg").value = "";
    renderMessages();
}

function renderDialogsList() {
    const container = document.getElementById("dialogsList");
    container.innerHTML = "";

    // Добавить «Вы» сверху
    const myAvatar = avatars.get(myLogin) || "";
    const me = document.createElement("div");
    me.className = "dialog-entry";
    me.innerHTML = `<img src="${myAvatar}" referrerpolicy="no-referrer"/><span><strong>Вы (${myLogin})</strong></span>`;
    me.onclick = () => {
        if (!dialogs.has(myLogin)) dialogs.set(myLogin, []);
        currentLogin = myLogin;
        renderMessages();
    };
    container.appendChild(me);

    dialogs.forEach((_, login) => {
        if (login === myLogin) return;
        const avatar = avatars.get(login) || "";
        const el = document.createElement("div");
        el.className = "dialog-entry";
        el.innerHTML = `<img src="${avatar}" referrerpolicy="no-referrer"/><span>${login}</span>`;
        el.onclick = () => {
            currentLogin = login;
            renderMessages();
        };
        container.appendChild(el);
    });
}

function renderMessages() {
    const messages = dialogs.get(currentLogin) || [];
    const area = document.getElementById("messages");

    area.innerHTML = messages.map(msg => {
        const side = msg.fromLogin === myLogin ? 'right' : 'left';
        const avatar = msg.fromAvatar || "";
        return `
        <div class="message ${side}">
          <img src="${avatar}" class="avatar" referrerpolicy="no-referrer" />
          <div class="bubble">${escapeHtml(msg.content)}</div>
        </div>`;
    }).join("");

    area.scrollTop = area.scrollHeight;
}

function addUserFromSearch() {
    const login = document.getElementById("searchBar").value.trim();
    if (!login) return;
    if (!dialogs.has(login)) dialogs.set(login, []);
    currentLogin = login;
    renderDialogsList();
    renderMessages();
    document.getElementById("searchBar").value = "";
}

function escapeHtml(text) {
    return text.replace(/[&<>'"]/g, function (char) {
        return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char];
    });
}

// Обработка Enter
document.addEventListener("keydown", function (e) {
    const isSearchFocused = document.activeElement.id === "searchBar";
    const isMsgFocused = document.activeElement.id === "msg";

    if (e.key === "Enter") {
        if (isSearchFocused) {
            e.preventDefault();
            addUserFromSearch();
        } else if (isMsgFocused) {
            e.preventDefault();
            send();
        }
    }
});
