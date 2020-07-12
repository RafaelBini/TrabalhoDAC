function entrar() {
    const user = document.getElementById("login").value;
    if (user.toUpperCase().includes("ADM")) {
        window.location.href = "admin.html";
    } else {
        window.location.href = "listarIntimacoesOficial.html";
    }
    return false;
}
