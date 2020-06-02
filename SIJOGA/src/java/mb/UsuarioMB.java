/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.hibernate.Query;
import org.hibernate.Session;
import java.util.List;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import util.HibernateUtil;

/**
 *
 * @author rfabini
 */
@Named(value="usuarioMB")
@ConversationScoped()
public class UsuarioMB implements Serializable{
    
    private Usuario usuario;
    private List<Estado> estados;
    private List<Cidade> cidades;
    private String estadoSelecionado = "1";    
    private String cidadeSelecionada;
    private String loginLabelMsg;
    private String cpfLabelMsg;

    @Inject
    private Conversation conversation;
    
    @PostConstruct
    public void init(){
        // Inicializa o escopo
        conversation.begin();
        
        // Instancia o usuario
        this.usuario = new Usuario();
        
        // Abre sessao Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();          
        
        // Recebe todos os estados
        Query query = session.createQuery("from Estado");        
        this.estados = query.list();
        
        // Recebe as cidades
        this.loadCidades();
        
        session.getTransaction().commit();           
        session.close();
    }
    
    public void loadCidades(){
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();    
        
        Query query;
        

        query = session.createQuery("SELECT DISTINCT c FROM Cidade c JOIN c.estado e WHERE e.id = :id ORDER BY c.nome");            

        
        query.setInteger("id",Integer.parseInt(this.estadoSelecionado));
        
        
        this.cidades = query.list();
        
        session.getTransaction().commit();           
        session.close(); 
    }
    
    public void validaUser(){
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();    
        
        Query query;
        

        query = session.createQuery("FROM Usuario WHERE login = :username");            

        
        query.setString("username",this.usuario.getLogin());
        
        
        List<Usuario> users = query.list();
        
        if (users.isEmpty()){
            this.loginLabelMsg = "";
        }
        else{            
            this.loginLabelMsg = "Esse usuário já está sendo usado!";
        }
        
        session.getTransaction().commit();           
        session.close();        
    }
    
    public void validaCpf(){
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();    
        
        Query query;
        

        query = session.createQuery("FROM Usuario WHERE cpf = :cpf");            

        
        query.setString("cpf",this.usuario.getCpf());
        
        
        List<Usuario> users = query.list();
        
        if (users.isEmpty()){
            this.cpfLabelMsg = "";
        }
        else{            
            this.cpfLabelMsg = "Esse CPF já foi cadastrado!";
        }
        
        session.getTransaction().commit();           
        session.close();     
    }
    
    public String cadastrar(){
        try{
            Session session = HibernateUtil.getSessionFactory().openSession();        
            session.beginTransaction();    

            // Recebe a cidade
            Cidade cidade = (Cidade)session.get(Cidade.class, Integer.parseInt(this.cidadeSelecionada));

            // Seta a Cidade
            this.usuario.setCidade(cidade);

            // Criptografa a senha      
            String s= this.usuario.getSenha();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            this.usuario.setSenha(new BigInteger(1,m.digest()).toString(16));       

            // Salva no bd
            session.save(this.usuario);

            session.getTransaction().commit();           
            session.close(); 

            conversation.end();
            
            FacesMessage msg = new FacesMessage("Usuário Cadastrado!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            
            return "index";
        }
        catch(Exception hex){

            FacesMessage msg = new FacesMessage("Não foi possivel criar o usuário. Veja os erros sinalziados");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);        
            
            return null;
        }
        
        
    }
    
    public String goCadastrar(){
        return "cadastro";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public void setEstados(List<Estado> estados) {
        this.estados = estados;
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }

    public String getEstadoSelecionado() {
        return estadoSelecionado;
    }

    public void setEstadoSelecionado(String estadoSelecionado) {
        this.estadoSelecionado = estadoSelecionado;
    }

    public String getCidadeSelecionada() {
        return cidadeSelecionada;
    }

    public void setCidadeSelecionada(String cidadeSelecionada) {
        this.cidadeSelecionada = cidadeSelecionada;
    }  

    public String getLoginLabelMsg() {
        return loginLabelMsg;
    }

    public void setLoginLabelMsg(String loginLabelMsg) {
        this.loginLabelMsg = loginLabelMsg;
    }

    public String getCpfLabelMsg() {
        return cpfLabelMsg;
    }

    public void setCpfLabelMsg(String cpfLabelMsg) {
        this.cpfLabelMsg = cpfLabelMsg;
    }

    

    
}
