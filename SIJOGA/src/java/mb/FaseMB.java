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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
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
    private UploadedFile file;
    
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
    
    public StreamedContent getFile(String arquivo) throws FileNotFoundException {

        StreamedContent fileToDownload;        
        
        InputStream stream = new FileInputStream(arquivo); //Caminho onde está salvo o arquivo.
        fileToDownload = new DefaultStreamedContent(stream, "application/pdf", new File(arquivo).getName());  

        return fileToDownload;  
    } 
    
    public String cadastrar(Usuario criador){
        
        

        // Salva o arquivo no servidor   
        File theFile = null;
        if(!this.file.getFileName().isEmpty()){      

           
            try { 

                String destination = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/") + "/anexos/" + new Date().getTime() + "/";
                new File(destination).mkdirs();
                theFile = new File(destination + this.file.getFileName());
                InputStream in = this.file.getInputstream();

                // write the inputStream to a FileOutputStream
                OutputStream out = new FileOutputStream(theFile);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                in.close();
                out.flush();
                out.close();


            } catch (IOException e) {
                FacesMessage msg = new FacesMessage("Problema ao tentar fazer upload do arquivo: " + e.getMessage());
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, msg);      
            }           
        }
   
        
        // Abre sessao Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();        
        session.beginTransaction();       
        
        // Recebe o arquivo
        if (theFile != null){
            this.novaFase.setCaminhoArquivo(theFile.getAbsolutePath());
        }
        
        // Recebe a data de criação
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        Date hoje = cal.getTime();
        this.novaFase.setDtCriacao(hoje);
        
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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
    
    
    
}
