/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Cidade;
import beans.Fase;
import beans.Intimacao;
import beans.Oficial;
import beans.Processo;
import beans.Usuario;
import com.fasterxml.jackson.core.JsonFactory;

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
import java.util.ArrayList;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import util.HibernateUtil;

/**
 * @author rfabini
 */
@Named(value = "faseMB")
@ConversationScoped()
public class FaseMB implements Serializable {

    private Processo processo;
    private Fase novaFase;
    private UploadedFile file;
    private Fase faseEscolhida;
    private String resposta;
    private String justificativa;
    private String parecerFinal;
    private String vencedor;
    private List<Oficial> oficiais;
    private Long oficial;
    private String intimado;

    @Inject
    private Conversation conversation;

    @PostConstruct
    public void init() {
        // Inicializa o escopo
        conversation.begin();
        this.novaFase = new Fase();
        this.novaFase.setTipo("Informativa");

    }

    public String goDeliberar(Fase umaFase) {
        this.faseEscolhida = umaFase;
        
        Client client = ClientBuilder.newClient();
        this.oficiais = client
                .target("http://localhost:8080/SOSIFOD/webresources/oficiais")
                .request(MediaType.APPLICATION_JSON + ";charset=utf-8")
                .get(new GenericType<ArrayList<Oficial>>() {});

        this.processo = umaFase.getProcesso();
        
        return "deliberar";
    }

    public Fase getUltimaFase(Processo p) {
        Fase f;

        // Abre sessao Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("FROM Fase a WHERE a.dtCriacao = (SELECT MAX(b.dtCriacao) FROM Fase b WHERE b.processo.id = :id) AND a.processo.id = :id");
        query.setInteger("id", p.getId());
        f = (Fase) query.list().get(0);

        // Fecha sessao hiber
        session.getTransaction().commit();
        session.close();

        return f;
    }

    public String responder() {
        // Abre sessao Hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Aceito
        if ("Aceito".equals(this.resposta)) {
            // Atualiza o processo para aberto
            Processo p = (Processo) session.get(Processo.class, this.faseEscolhida.getProcesso().getId());
            p.setStatus("Aberto");
            session.save(p);

            // Atualiza a fase com uma resposta
            Fase f = (Fase) session.get(Fase.class, this.faseEscolhida.getId());
            f.setResposta("Pedido Aceito");
            f.setJustificativaResposta("-");
            session.save(f);

        } // Negado        
        else if ("Negado".equals(this.resposta)) {
            // Atualiza o processo para aberto
            Processo p = (Processo) session.get(Processo.class, this.faseEscolhida.getProcesso().getId());
            p.setStatus("Aberto");
            session.save(p);

            // Atualiza a fase com uma resposta
            Fase f = (Fase) session.get(Fase.class, this.faseEscolhida.getId());
            f.setResposta("Pedido Negado");
            f.setJustificativaResposta(this.justificativa);
            session.save(f);
        } // Intimado
        else if ("Intimado".equals(this.resposta)) {
            Processo p = (Processo) session.get(Processo.class, this.faseEscolhida.getProcesso().getId());
            Usuario u = intimado.equals("Promovente") ? p.getPromovente() : p.getPromovida();

            String endereco = u.getRua() + ", " + u.getNumero() + " - " + u.getCidade().getNome() + ", " + u.getCidade().getEstado().getAbreviatura();
            
            Oficial o = new Oficial();
            o.setId(oficial);

            Intimacao i = new Intimacao(u.getNome(), u.getCpf(), endereco, o, p.getId());

            Client client = ClientBuilder.newClient();

            Response r = client
                    .target("http://localhost:8080/SOSIFOD/webresources/intimacoes")
                    .request(MediaType.APPLICATION_JSON + ";charset=utf-8")
                    .post(Entity.json(i));

            if (r.getStatus() == 201) {
                Fase f = (Fase) session.get(Fase.class, this.faseEscolhida.getId());
                f.setResposta("Intimação");
                f.setJustificativaResposta("Aguardando execução de intimação pelo Oficial de Justiça");
                session.save(f);
            }

        } // Encerrado
        else if ("Encerrado".equals(this.resposta)) {
            // Atualiza o processo para encerrado e insere um vencedor
            Processo p = (Processo) session.get(Processo.class, this.faseEscolhida.getProcesso().getId());
            p.setStatus("Encerrado");
            p.setVencedor(this.vencedor);
            session.save(p);

            // Atualiza a fase com um parecer final
            Fase f = (Fase) session.get(Fase.class, this.faseEscolhida.getId());
            f.setResposta("Processo Encerrado");
            f.setJustificativaResposta(this.parecerFinal);
            session.save(f);
        }

        // Fecha sessao hiber
        session.getTransaction().commit();
        session.close();

        // Finaliza escopo
        conversation.end();

        // Volta pra pagina de processos
        return "juiz";
    }

    public String goFases(Processo p) {
        this.processo = p;

        return "fases";
    }

    public String goNovaFase() {
        return "novaFase";
    }

    public String backHome(Usuario userLogado) {

        // Fecha o escopo
        conversation.end();

        if ("Advogado".equals(userLogado.getTipo())) {
            return "advogado";
        } else if ("Juíz".equals(userLogado.getTipo())) {
            return "juiz";
        } else if ("Parte".equals(userLogado.getTipo())) {
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

    public String cadastrar(Usuario criador) {

        // Salva o arquivo no servidor   
        File theFile = null;
        if (!this.file.getFileName().isEmpty()) {

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
        if (theFile != null) {
            this.novaFase.setCaminhoArquivo(theFile.getAbsolutePath());
        }

        // Recebe a data de criação
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        Date hoje = cal.getTime();
        this.novaFase.setDtCriacao(hoje);

        // Recebe o criador
        Usuario c = (Usuario) session.get(Usuario.class, criador.getId());
        this.novaFase.setCriador(c);

        // Recebe  o processo
        Processo p = (Processo) session.get(Processo.class, this.processo.getId());
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
        this.processo = (Processo) session2.get(Processo.class, this.processo.getId());

        // Se a nova fase é deliberativa, seta o status do processo para "Aguardando Juíz"
        if ("Deliberativa".equals(this.novaFase.getTipo())) {
            this.processo.setStatus("Aguardando Juíz");
        }

        // Salva o novo processo
        session2.save(this.processo);

        // Fecha sessao 2
        session2.getTransaction().commit();
        session2.close();

        this.processo.getFases().sort(new Comparator<Fase>() {
            @Override
            public int compare(Fase a, Fase b) {
                return a.getId().compareTo(b.getId());
            }
        });

        return "fases";
    }

    public boolean podeCriarFase() {
        if ("Aberto".equals(this.processo.getStatus())) {
            return true;
        } else {
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

    public Fase getFaseEscolhida() {
        return faseEscolhida;
    }

    public void setFaseEscolhida(Fase faseEscolhida) {
        this.faseEscolhida = faseEscolhida;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getParecerFinal() {
        return parecerFinal;
    }

    public void setParecerFinal(String parecerFinal) {
        this.parecerFinal = parecerFinal;
    }

    public String getVencedor() {
        return vencedor;
    }

    public void setVencedor(String vencedor) {
        this.vencedor = vencedor;
    }

    public List<Oficial> getOficiais() {
        return oficiais;
    }

    public void setOficiais(List<Oficial> oficiais) {
        this.oficiais = oficiais;
    }

    public Long getOficial() {
        return oficial;
    }

    public void setOficial(Long oficial) {
        this.oficial = oficial;
    }

    public String getIntimado() {
        return intimado;
    }

    public void setIntimado(String intimado) {
        this.intimado = intimado;
    }

}
