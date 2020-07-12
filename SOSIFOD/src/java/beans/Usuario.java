/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * @author mlcab
 */

@Entity
@Table(
        name = "tb_usuario",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"login"}),
                @UniqueConstraint(columnNames = {"cpf"}),
                @UniqueConstraint(columnNames = {"email"})
        }
)
public class Usuario {
    private Long id;
    private String login;
    private String senha;
    private String tipo;
    private String cpf;
    private String nome;
    private String email;
    //private List<Usuario> usuarios = new ArrayList<Usuario>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    @NotNull()
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

    @NotNull()
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Column(unique = true)
    @NotNull()
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @NotNull()
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static ArrayList<Usuario> listarOficiais() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT id, nome, email FROM Usuario WHERE tipo = ?");

        query.setString(0, "Oficial");

        List<Usuario> oficiais = new ArrayList<>();

        query.list().stream().forEach(it -> {
            Usuario u = new Usuario();
            u.setId((Long) ((Object[]) it)[0]);
            u.setNome((String) ((Object[]) it)[1]);
            u.setEmail((String) ((Object[]) it)[2]);
            oficiais.add(u);
        });

        return (ArrayList)oficiais;
    }
    
    public static Usuario findById (long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Usuario WHERE id = ?");
        query.setLong(0, id);
        
        return (Usuario) query.uniqueResult();
    }
}
