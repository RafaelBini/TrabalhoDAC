/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Intimacao;
import beans.Usuario;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import sun.invoke.empty.Empty;
import util.HibernateUtil;

/**
 *
 * @author mlcab
 */
@Named(value = "intimacaoMB")
@ConversationScoped()
public class IntimacaoMB implements Serializable{
    private Intimacao intimacao;
    private String status;
    private List<Intimacao> intimacoes;
    private List<Usuario> oficial;
    private String cpfLabelMsg;
    //private List<Usuario> oficial;
    
    @Inject
    private Conversation conversation;
    
    @PostConstruct
    public void init(){
        
        // Inicializa o escopo
        conversation.begin();
        
        loadIntimacoes();
    }
    
    public String loadIntimacoes(){
        // Recebe o usuario logado
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario loggedUser = (Usuario) context.getApplication().getExpressionFactory()
        .createValueExpression(context.getELContext(), "#{loginMB.loggedUser}", Usuario.class)
        .getValue(context.getELContext());
        
        // Abre sessao
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();
        
        if("Admin".equals(loggedUser.getTipo())){
            // Recebe todas as intimações
            Query query;
            query = session.createQuery("FROM Intimacao order by id_intimacao");                   
            this.intimacoes = query.list();
        }
        
        if("Oficial".equals(loggedUser.getTipo())){
            // Recebe as intimações ligadas ao oficial
            Query query;      
            query = session.createQuery("FROM Intimacao WHERE oficial.id = :id order by id_intimacao");                   
            query.setLong("id", loggedUser.getId());
            this.intimacoes = query.list();
            
        }
        
        
        session.getTransaction().commit();           
        session.close(); 
        
        // Retorna as intimações
        //return intimacoes;
        return "intimacoes";
    }
    
    /*public List<Intimacao> getIntimacoesOficial(Usuario oficial){
        List<Intimacao> intimacoes;
        
        // Abre sessao
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();
        
        // Recebe o oficial
        Usuario of = (Usuario)session.get(Usuario.class, oficial.getId()); 
        
        // Recebe as intimações ligadas ao oficial
        Query query;      
        query = session.createQuery("FROM Intimacao WHERE oficial = :id");                   
        query.setLong("id", of.getId());
        intimacoes = query.list();  
        
        session.getTransaction().commit();           
        session.close(); 
        
        // Retorna as intimações
        return intimacoes;
    }*/
    
    public void executar(Intimacao i){
        this.intimacao = i;
        List<Intimacao> buscarIntimacoes;
        
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();
        
        Query query;      
        query = session.createQuery("FROM Intimacao WHERE id = :id"); 
        query.setLong("id", i.getId());
        buscarIntimacoes = query.list();
        Intimacao intimacao = buscarIntimacoes.get(0);
        
        if(intimacao.getStatus().equals("Não efetuada")){
            intimacao.setStatus("Efetuada");
            //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            intimacao.setDtExecucao(date);
            //intimacao.setDtExecucao = dateFormat.format(date);
            session.update(intimacao);
            
            session.getTransaction().commit();           
            session.close(); 
            
            conversation.end();
            
            FacesMessage msg = new FacesMessage("Intimação Executada!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }else{
            session.getTransaction().commit();           
            session.close(); 
            
            conversation.end();
            
            FacesMessage msg = new FacesMessage("A intimação já foi executada!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        
        
    }
    
    public boolean executada(Intimacao i){
        this.intimacao = i;
        List<Intimacao> buscarIntimacoes;
        
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();
        
        Query query;      
        query = session.createQuery("FROM Intimacao WHERE id = :id"); 
        query.setLong("id", i.getId());
        buscarIntimacoes = query.list();
        Intimacao intimacao = buscarIntimacoes.get(0);
        
        session.getTransaction().commit();           
        session.close();
        
        return (intimacao.getStatus().equals("Efetuada")); 
    }
    
    public void excluir(Intimacao i){
        this.intimacao = i;
        List<Intimacao> buscarIntimacoes;
        
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();
        
        Query query;
        query = session.createQuery("DELETE FROM Intimacao WHERE id = :id");
        query.setLong("id", i.getId());
        int rows = query.executeUpdate();
        
        
        for(Intimacao intima : this.intimacoes){
            if(intima.getId() == i.getId()){
                this.intimacoes.remove(i);
                break;
            }
        }
        
        
        session.getTransaction().commit();           
        session.close(); 

        conversation.end();

        FacesMessage msg = new FacesMessage("Intimação Excluída!");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public String goVisualizar(Intimacao i){
        this.intimacao = i;
        return "visualizar";
    }
    
    public String goEditar(Intimacao i){
        this.intimacao = i;
        return "editar";
    }
    
    public String voltar(){
        conversation.end();
        return "intimacoes";
    }
    
    public String editar() throws Exception{
        try{
            // Valida o CPF
            if (!this.isCpfValid(this.intimacao.getCpf())){
                throw new Exception("CPF inválido!");
            }
            
            if(this.intimacao.getStatus().equals("Efetuada")){
                // Busca as intimações no banco
                List<Intimacao> buscarIntimacoes;
                
                Session session = HibernateUtil.getSessionFactory().openSession();        
                session.beginTransaction();

                // Query para alteração do status
                Query editaStatus;
                editaStatus = session.createQuery("FROM Intimacao WHERE id = :id");
                editaStatus.setLong("id", this.intimacao.getId());
                buscarIntimacoes = editaStatus.list();
                Intimacao intimacaoBanco = buscarIntimacoes.get(0);

                // Se o status da Intimação for alterado para "Executada"
                if(intimacaoBanco.getStatus().equals("Não efetuada") && this.intimacao.getStatus().equals("Efetuada")){
                    Date date = new Date();
                    this.intimacao.setDtExecucao(date);
                }
                /*if(intimacaoBanco.getStatus().equals("Efetuada") && this.intimacao.getStatus().equals("Nao-efetuada")){
                    Date date = new Date();
                    date = null;
                    this.intimacao.setDtExecucao(date);
                    
                }*/
                
                session.getTransaction().commit();           
                session.close();
            }
            
            Session session = HibernateUtil.getSessionFactory().openSession();        
            session.beginTransaction();
            
            
            // Query para alteração de oficial
            Query query;      
            query = session.createQuery("FROM Usuario WHERE nome = :nome");                   
            query.setString("nome", this.intimacao.getOficial().getNome());
            this.oficial = query.list();
            this.intimacao.setOficial(this.oficial.get(0));
            //this.intimacao.setOficial(oficial);
            
            // Salva no bd
            if(this.oficial == null){
                throw new Exception("Oficial não existe. Insira um Oficial válido!");
            }else{
                session.update(this.intimacao);
                //session.save(this.intimacao);
            }

            session.getTransaction().commit();           
            session.close(); 
            
            conversation.end();
            
            FacesMessage msg = new FacesMessage("Intimação Atualizada!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            
            return "intimacoes";
        }
        catch(HibernateException hex){
            FacesMessage msg = new FacesMessage("Não foi possivel editar a intimação. Veja os erros sinalizados");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);        
            return null;
        }
    }
    
    public void validaCpf(){
        this.cpfLabelMsg = "";
        /*Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction(); 
        Query query;       
        query = session.createQuery("FROM Intimacao WHERE cpf = :cpf");                   
        query.setString("cpf",this.intimacao.getCpf());
        List<Intimacao> intimacoes = query.list();
        if (intimacoes.isEmpty()){
            this.cpfLabelMsg = "";
        }
        else{            
            this.cpfLabelMsg = "Esse CPF já foi cadastrado!";
        }
        session.getTransaction().commit();           
        session.close();*/
        
        // Valida o CPF
        if(!isCpfValid(this.intimacao.getCpf())){
            this.cpfLabelMsg = "CPF inválido";
        }
    }
    
    public boolean isCpfValid(String strCpf){
        strCpf = strCpf.replace(".", "").replace("-", "");
        
        if (strCpf.equals("")) {  
            return false;  
        }  
        int d1, d2;  
        int digito1, digito2, resto;  
        int digitoCPF;  
        String nDigResult;  
  
        d1 = d2 = 0;  
        digito1 = digito2 = resto = 0;  
  
        for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {  
            digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();  
  
            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
            d1 = d1 + (11 - nCount) * digitoCPF;  
  
            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
            d2 = d2 + (12 - nCount) * digitoCPF;  
        }  
  
        //Primeiro resto da divisão por 11.  
        resto = (d1 % 11);  
  
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
        if (resto < 2) {  
            digito1 = 0;  
        } else {  
            digito1 = 11 - resto;  
        }  
  
        d2 += 2 * digito1;  
  
        //Segundo resto da divisão por 11.  
        resto = (d2 % 11);  
  
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
        if (resto < 2) {  
            digito2 = 0;  
        } else {  
            digito2 = 11 - resto;  
        }  
  
        //Digito verificador do CPF que está sendo validado.  
        String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());  
  
        //Concatenando o primeiro resto com o segundo.  
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);  
  
        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.  
        return nDigVerific.equals(nDigResult); 
    }
        
    public Intimacao getIntimacao() {
        return intimacao;
    }

    public void setIntimacao(Intimacao intimacao) {
        this.intimacao = intimacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Intimacao> getIntimacoes() {
        return intimacoes;
    }

    public void setIntimacoes(List<Intimacao> intimacoes) {
        this.intimacoes = intimacoes;
    }

    public List<Usuario> getOficial() {
        return oficial;
    }

    public void setOficial(List<Usuario> oficial) {
        this.oficial = oficial;
    }

    public String getCpfLabelMsg() {
        return cpfLabelMsg;
    }

    public void setCpfLabelMsg(String cpfLabelMsg) {
        this.cpfLabelMsg = cpfLabelMsg;
    }
    
}
