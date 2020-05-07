package servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import DAO.ConnectionFactory;
import beans.Usuario;
import DAO.UsuarioDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rfabini
 */
@WebServlet(urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            int cpf = Integer.valueOf(request.getParameter("user").replace(".", "").replace("-", ""));
            String senha = request.getParameter("pass");
            
            
            // Faz a validação do usuario e senha
            try{


                // Criptografa a senha 
                MessageDigest algoritmo = MessageDigest.getInstance("MD5");
                byte messageDigest[] = algoritmo.digest(senha.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for(byte b : messageDigest){
                    hexString.append(String.format("%02X", 0xFF & b));
                }                
                senha = hexString.toString().toLowerCase();
                
                // Recebe o usuario
                Usuario u = UsuarioDAO.getUsuario(cpf, senha);
                
                // Se o usuário foi encontrado com essa senha,
                if (u != null){
                    // Salva sessão
                    HttpSession session = request.getSession();
                    session.setAttribute("u", u);
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/portal.jsp");
                    rd.forward(request, response);
                    return;          
                }
                // Se o usuário não foi encontrado com essa senha,
                else{
                    // Redireciona para pagina de login e informa usu ou senha incorreta
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
                    request.setAttribute("msg", "Usuário ou Senha incorreta!");
                    request.setAttribute("page", "index.jsp");
                    rd.forward(request, response);
                    return;                    
                }            
            }
            catch(Exception ex){
                out.print("<br />Falha ao tentar verificar a senha / usuario: " + ex.getMessage());
            }

        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
