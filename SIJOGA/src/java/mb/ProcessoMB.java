/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Cidade;
import beans.Processo;
import beans.Usuario;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * @author rfabini
 */
@Named(value = "processoMB")
@RequestScoped()
public class ProcessoMB implements Serializable {
    private String clienteSelecionado;
    private String promovidaSelecionada;
    private List<Usuario> promovidos;


    @PostConstruct
    public void init() {


    }

    public String goRelatorios() {
        return "relatorios";
    }

    public String goAddProcesso() {
        return "novoProcesso";
    }

    public List<Usuario> getClientes(Usuario advogado) {
        // Abre sessao
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Recebe o advogado
        Usuario adv = (Usuario) session.get(Usuario.class, advogado.getId());


        session.getTransaction().commit();
        session.close();

        // Retorna os clientes
        return adv.getClientes();
    }

    public List<Usuario> getPromoviveis(Usuario advogado) {

        List<Usuario> promoviveis;

        // Abre sessao
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Recebe o advogado
        Usuario adv = (Usuario) session.get(Usuario.class, advogado.getId());

        // Recebe os promoviveis
        Query query;
        query = session.createQuery("FROM Usuario WHERE tipo = 'Parte' and advogado <> :id");
        query.setLong("id", adv.getId());
        promoviveis = query.list();


        session.getTransaction().commit();
        session.close();

        // Retorna os clientes
        return promoviveis;
    }

    public String getClienteSelecionado() {
        return clienteSelecionado;
    }

    public void setClienteSelecionado(String clienteSelecionado) {
        this.clienteSelecionado = clienteSelecionado;
    }


    public String cancel() {

        return "advogado";
    }

    public String criar() {
        // Abre sessao hibernate
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Instancia um novo processo
        Processo processo = new Processo();

        // Recebe o dia de hj na criacao
        processo.setDtCriacao(new Date());

        // Recebe juiz com menos processos
        Query queryJuiz;
        queryJuiz = session.createQuery("SELECT u, COUNT(p) FROM Usuario u LEFT JOIN u.juizProcessos p WHERE u.tipo = 'Juíz' GROUP BY u.id ORDER BY COUNT(p)");
        Object[] retorno = (Object[]) queryJuiz.list().get(0);
        processo.setJuiz((Usuario) retorno[0]);

        // Recebe promovente escolhida
        Usuario promovente = (Usuario) session.get(Usuario.class, Long.valueOf(this.clienteSelecionado));
        processo.setPromovente(promovente);

        // Recebe promovida escolhida
        Usuario promovida = (Usuario) session.get(Usuario.class, Long.valueOf(this.promovidaSelecionada));
        processo.setPromovida(promovida);

        // Seta status
        processo.setStatus("Aberto");

        // Salva
        session.save(processo);

        // Fecha sessaao Hibernate
        session.getTransaction().commit();
        session.close();

        // Informa
        FacesMessage msg = new FacesMessage("Processo Criado!");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        // Direciona para advogado
        return "advogado";
    }

    public List<Usuario> getPromovidos() {
        return promovidos;
    }

    public void setPromovidos(List<Usuario> promovidos) {
        this.promovidos = promovidos;
    }

    public String getPromovidaSelecionada() {
        return promovidaSelecionada;
    }

    public void setPromovidaSelecionada(String promovidaSelecionada) {
        this.promovidaSelecionada = promovidaSelecionada;
    }


}
