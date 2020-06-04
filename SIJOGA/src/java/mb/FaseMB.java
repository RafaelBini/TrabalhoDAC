/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Cidade;
import beans.Fase;
import beans.Processo;
import beans.Usuario;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author rfabini
 */
@Named(value="faseMB")
@ConversationScoped()
public class FaseMB implements Serializable {
    private Processo processo;    
    private Fase novaFase;
    
    @Inject
    private Conversation conversation;
    
    @PostConstruct
    public void init(){
        // Inicializa o escopo
        conversation.begin();
        this.novaFase = new Fase();
        this.novaFase.setTipo("Informativa");
        
    }
    
    public String goFases(Processo p){
        this.processo = p;
        
        return "fases";
    }
    
    public String goNovaFase(){
        return "novaFase";
    }
    
    public String backHome(Usuario userLogado){
        
        // Fecha o escopo
        conversation.end();
        
        if ("Advogado".equals(userLogado.getTipo())){
            return "advogado";
        }
        else if ("Juíz".equals(userLogado.getTipo())){
            return "juiz";
        }
        else if ("Parte".equals(userLogado.getTipo())){
            return "parte";
        }
        return null;
    }
    
    public String cadastrar(Usuario criador){
        // Abre sessao Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();       
        
        // Recebe a data de criação
        this.novaFase.setDtCriacao(new Date());
        
        // Recebe o criador
        Usuario c = (Usuario)session.get(Usuario.class, criador.getId());
        this.novaFase.setCriador(c);
        
        // Recebe  o processo
        Processo p = (Processo)session.get(Processo.class, this.processo.getId());
        this.novaFase.setProcesso(p);
        
        // Salva a nova fase
        session.save(this.novaFase);       
        
        session.getTransaction().commit();           
        session.close();
        
        FacesMessage msg = new FacesMessage("Nova fase criada!");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);        
        
        // Abre outra sessao Hibernate
        Session session2 = HibernateUtil.getSessionFactory().openSession();        
        session2.beginTransaction();     
        
        // Recebe o processo atualizadinho
        this.processo = (Processo)session2.get(Processo.class, this.processo.getId()); 
        
        // Se a nova fase é deliberativa, seta o status do processo para "Aguardando Juíz"
        if ("Deliberativa".equals(this.novaFase.getTipo())){
            this.processo.setStatus("Aguardando Juíz");
        }

        // Salva o novo processo
        session2.save(this.processo);
        
        // Fecha sessao 2
        session2.getTransaction().commit();           
        session2.close();           

        this.processo.getFases().sort(new Comparator<Fase>(){
            @Override
            public int compare(Fase a, Fase b) {
              return a.getId().compareTo(b.getId());
            }
        });
        
        return "fases";
    }
    
    public boolean podeCriarFase(){
        if ("Aberto".equals(this.processo.getStatus())){
            return true;
        }
        else{
            return false;
        }
        
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }


    public Fase getNovaFase() {
        return novaFase;
    }

    public void setNovaFase(Fase novaFase) {
        this.novaFase = novaFase;
    }
    
    
    
}
