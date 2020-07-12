/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Usuario;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * @author mlcab
 */
@Named(value = "loginMB")
@SessionScoped
public class LoginMB implements Serializable {
    private String login;
    private String senha;
    private Usuario loggedUser;

    @PostConstruct
    public void init() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Usuario loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String entrar() {
        try {
            // Abre sessão Hibernate
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            // Criptografa a senha
            String s = this.getSenha();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            this.setSenha(new BigInteger(1, m.digest()).toString(16));

            // Monta a query
            Query query;
            query = session.createQuery("FROM Usuario WHERE login = :username AND senha = :senha");
            query.setString("username", this.getLogin());
            query.setString("senha", this.getSenha());

            List<Usuario> users = query.list();

            // Fecha sesao do Hibernate
            session.getTransaction().commit();
            session.close();

            // Se não existe a combinação,
            if (users.isEmpty()) {
                // Informa e finaliza
                FacesMessage msg = new FacesMessage("Usuário e/ou Senha incorreta!");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, msg);

                return null;
            }

            // Recebe as info do usuario
            this.setLoggedUser(users.get(0));

            // Se é Admin
            if ("Admin".equals(users.get(0).getTipo())) {
                // Entra na tela do Admin
                return "admin";
            }

            // Se é Oficial
            if ("Oficial".equals(users.get(0).getTipo())) {
                // Entra na tela do Oficial
                return "intimacoes";
            }


        } catch (Exception e) {
            // Informa e finaliza
            FacesMessage msg = new FacesMessage(e.getMessage());
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            return null;
        }

        return null;
    }

    public String sair() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index";
    }
}
