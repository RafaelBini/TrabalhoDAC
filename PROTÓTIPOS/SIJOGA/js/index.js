function entrar() {
    const user = document.getElementById("login").value;
    if (user.toUpperCase().includes("J")) {
        window.location.href = "juiz.html";
    }
    else if (user.toUpperCase().includes("ADV")) {
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