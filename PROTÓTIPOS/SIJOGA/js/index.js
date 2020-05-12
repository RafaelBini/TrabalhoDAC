function entrar() {
    const user = document.getElementById("login").value;
    if (user.includes("j")) {
        window.location.href = "juiz.html";
    }
    else if (user.includes("a")) {
        window.location.href = "advogado.html";
    }
    else {
        window.location.href = "parte.html";
    }

    return false;
}

function cadastrar() {
    window.location.href = "cadastrar.html";
}