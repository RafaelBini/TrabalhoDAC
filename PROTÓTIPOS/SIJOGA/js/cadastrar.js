function efetuarCadastro() {
    const user = document.getElementById("tipo").value;
    if (user.includes("Juíz")) {
        window.location.href = "juiz.html";
    }
    else if (user.includes("Advogado")) {
        window.location.href = "advogado.html";
    }
    else {
        window.location.href = "parte.html";
    }

    return false;
}