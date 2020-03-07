<%-- 
    Document   : index
    Created on : 07/03/2020, 09:31:14
    Author     : rfabini
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Portal Tads - Login</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="estilo.css" type="text/css" />
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">

    </head>
    <body>
        <form action="LoginServlet" method="post">
            <table class="tabelaLogin">
                <c:if test="${msg != null}" >
                    <tr><td style='color: red;'><c:out value="${msg}" /></td></tr>
                </c:if>
                <tr><td colspan="2">SIJOGA</td></tr>
                <tr><td> <input autofocus type="text" name="user" placeholder='CPF'/></td></tr>
                <tr><td><input type="password" name="pass" placeholder='Senha' /></td></tr>
                <tr><td colspan="2"><br /><input class="btn btn-primary" type="submit" value="Logar"/></td></tr>
            </table>
        </form>
                
    </body>
</html>
