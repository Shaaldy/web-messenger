const socket = new WebSocket("ws://" + window.location.host + "/ws/chat");

socket.onopen = () => console.log("WebSocket соединение установлено");

socket.onmessage = function (event) {
    const msg = JSON.parse(event.data);
    const area = document.getElementById("chat");
    const line = document.createElement("div");
    line.innerHTML = renderMessage(msg);
    area.appendChild(line);
    area.scrollTop = area.scrollHeight;
};

socket.onerror = function (error) {
    console.error("WebSocket error:", error);
};

function send() {
    const to = document.getElementById("toLogin").value.trim();
    const content = document.getElementById("msg").value.trim();
    if (!to || !content) return;

    const payload = {
        toLogin: to,
        content: content,
        type: "CHAT"
    };
    socket.send(JSON.stringify(payload));
    document.getElementById("msg").value = "";
}

function renderMessage(msg) {
    const from = msg.fromLogin;
    const content = msg.content.trim();

    let html = `<strong>[${from}]</strong>: `;

    if (content.match(/\.(jpeg|jpg|png|gif)$/i)) {
        html += `<br><img src="${content}" style="max-width:300px;">`;
    } else if (content.includes("youtube.com/watch?v=")) {
        const videoId = content.split("v=")[1];
        html += `<br><iframe width="300" height="169" src="https://www.youtube.com/embed/${videoId}" frameborder="0" allowfullscreen></iframe>`;
    } else if (content.match(/\.(mp4|webm)$/i)) {
        html += `<br><video src="${content}" controls style="max-width:300px;"></video>`;
    } else if (content.startsWith("http")) {
        html += `<a href="${content}" target="_blank">${content}</a>`;
    } else {
        html += content;
    }

    return html;
}
