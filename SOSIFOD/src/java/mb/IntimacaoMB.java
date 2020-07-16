/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Fase;
import beans.Intimacao;
import beans.Processo;
import beans.Usuario;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * @author mlcab
 */
@Named(value = "intimacaoMB")
@ConversationScoped()
public class IntimacaoMB implements Serializable {

    private Intimacao intimacao;
    private String status;
    private List<Intimacao> intimacoes;
    private List<Usuario> oficial;
    private String cpfLabelMsg;
    private String intimacaoLabelMsg;
    private List<String> nomeOficial;
    private List<Usuario> users;
    private int oficialSelecionado;

    @Inject
    private Conversation conversation;

    @PostConstruct
    public void init() {

        // Inicializa o escopo
        conversation.begin();

        // Instancia o usuario
        this.intimacao = new Intimacao();
        this.intimacao.setOficial(new Usuario());
        // Busca os oficiais
        users = buscaOficiais();
        loadIntimacoes();
    }

    public String loadIntimacoes() {
        // Recebe o usuario logado
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario loggedUser = (Usuario) context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{loginMB.loggedUser}", Usuario.class)
                .getValue(context.getELContext());

        // Abre sessao
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        if ("Admin".equals(loggedUser.getTipo())) {
            // Recebe todas as intimações
            Query query;
            query = session.createQuery("FROM Intimacao order by id_intimacao");
            this.intimacoes = query.list();
        }

        if ("Oficial".equals(loggedUser.getTipo())) {
            // Recebe as intimações ligadas ao oficial
            Query query;
            query = session.createQuery("FROM Intimacao WHERE oficial.id = :id order by id_intimacao");
            query.setLong("id", loggedUser.getId());
            this.intimacoes = query.list();

        }

        session.getTransaction().commit();
        session.close();

        return "intimacoes";
    }

    public List<Usuario> buscaOficiais() {
        List<Usuario> oficiais;
        List<String> nOficial = new ArrayList<String>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query;
        query = session.createQuery("FROM Usuario WHERE tipo = :tipo");
        query.setString("tipo", "Oficial");
        oficiais = query.list();

        session.getTransaction().commit();
        session.close();

        return oficiais;
    }

    public void executar(Intimacao i) {
        this.intimacao = i;
        List<Intimacao> buscarIntimacoes;

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query;
        query = session.createQuery("FROM Intimacao WHERE id = :id");
        query.setLong("id", i.getId());
        buscarIntimacoes = query.list();
        Intimacao intimacao = buscarIntimacoes.get(0);

        if (intimacao.getStatus().equals("Não efetuada")) {
            
            Processo p = new Processo();
            p.setId(intimacao.getNumProcesso());
            
            Fase f = new Fase(
                    p,
                    "Intimacao executada",
                    "Fase gerada automaticamente a partir da execucao da intimacao via SOSIFOD.",
                    "Informativa"
            );
            

            Client client = ClientBuilder.newClient();

            Response response = client
                    .target("http://localhost:8080/SIJOGA/webresources/fases")
                    .request(MediaType.APPLICATION_JSON + ";charset=utf-8")
                    .post(Entity.json(f));

            if (response.getStatus() == 201) {
                intimacao.setStatus("Efetuada");
                Date date = new Date();
                intimacao.setDtExecucao(date);
                session.update(intimacao);

                session.getTransaction().commit();
                session.close();

                conversation.end();

                FacesMessage msg = new FacesMessage("Intimação Executada!");
                msg.setSeverity(FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage("Erro ao executar intimação.");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            session.getTransaction().commit();
            session.close();

            conversation.end();

            FacesMessage msg = new FacesMessage("A intimação já foi executada!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public boolean executada(Intimacao i) {
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

    public void excluir(Intimacao i) {
        this.intimacao = i;
        List<Intimacao> buscarIntimacoes;

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query;
        query = session.createQuery("DELETE FROM Intimacao WHERE id = :id");
        query.setLong("id", i.getId());
        int rows = query.executeUpdate();

        for (Intimacao intima : this.intimacoes) {
            if (intima.getId() == i.getId()) {
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

    public String goVisualizar(Intimacao i) {
        this.intimacao = i;
        return "visualizar";
    }

    public String goEditar(Intimacao i) {
        this.intimacao = i;
        return "editar";
    }

    public String goCadastrar() {
        return "novaIntimacao";
    }

    public String voltar() {
        conversation.end();
        return "intimacoes";
    }

    public String cadastrar() throws Exception {
        try {
            // Valida o CPF
            if (!this.isCpfValid(this.intimacao.getCpf())) {
                throw new Exception("CPF inválido!");
            }

            List<Intimacao> buscarIntimacoes;
            List<Usuario> ofi;

            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            // Query para alteração de oficial
            Query query;
            query = session.createQuery("FROM Usuario WHERE id = :id");
            query.setLong("id", oficialSelecionado);
            ofi = query.list();
            this.intimacao.setOficial(ofi.get(0));

            Date date = new Date();
            // Se o status da Intimação for alterado para "Executada"
            if (this.intimacao.getStatus().equals("Efetuada")) {
                this.intimacao.setDtIntimacao(date);
                this.intimacao.setDtExecucao(date);
            } else {
                this.intimacao.setDtIntimacao(date);
            }

            // Salva no bd
            session.save(this.intimacao);

            this.intimacoes.add(this.intimacao);

            session.getTransaction().commit();
            session.close();

            conversation.end();

            FacesMessage msg = new FacesMessage("Intimação Criada Com Sucesso!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            return "intimacoes";
        } catch (HibernateException hex) {
            FacesMessage msg = new FacesMessage("Não foi possivel criar a intimação. Veja os erros sinalizados");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    public String editar() throws Exception {
        try {
            // Valida o CPF
            if (!this.isCpfValid(this.intimacao.getCpf())) {
                throw new Exception("CPF inválido!");
            }

            if (this.intimacao.getStatus().equals("Efetuada")) {
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
                if (intimacaoBanco.getStatus().equals("Não efetuada") && this.intimacao.getStatus().equals("Efetuada")) {
                    Date date = new Date();
                    this.intimacao.setDtExecucao(date);
                }

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

            // Salva no bd
            if (this.oficial == null) {
                throw new Exception("Oficial não existe. Insira um Oficial válido!");
            } else {
                session.update(this.intimacao);
            }

            session.getTransaction().commit();
            session.close();

            conversation.end();

            FacesMessage msg = new FacesMessage("Intimação Atualizada!");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            return "intimacoes";
        } catch (HibernateException hex) {
            FacesMessage msg = new FacesMessage("Não foi possivel editar a intimação. Veja os erros sinalizados");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    public void validaCpf() {
        this.cpfLabelMsg = "";

        // Valida o CPF
        if (!isCpfValid(this.intimacao.getCpf())) {
            this.cpfLabelMsg = "CPF inválido";
        }
    }

    public void validaIntimacao() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query;
        query = session.createQuery("FROM Intimacao WHERE cpf = :cpf AND numProcesso = :num");
        query.setString("cpf", this.intimacao.getCpf());
        query.setInteger("num", this.intimacao.getNumProcesso());
        List<Intimacao> vIntimacoes = query.list();
        if (vIntimacoes.isEmpty()) {
            this.intimacaoLabelMsg = "";
        } else {
            this.intimacaoLabelMsg = "Já existe uma intimação com o mesmo CPF e número do processo!";
        }
        session.getTransaction().commit();
        session.close();
    }

    public boolean isCpfValid(String strCpf) {
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

    public List<String> getNomeOficial() {
        return nomeOficial;
    }

    public void setNomeOficial(List<String> nomeOficial) {
        this.nomeOficial = nomeOficial;
    }

    public List<Usuario> getUsers() {
        return users;
    }

    public void setUsers(List<Usuario> users) {
        this.users = users;
    }

    public int getOficialSelecionado() {
        return oficialSelecionado;
    }

    public void setOficialSelecionado(int oficialSelecionado) {
        this.oficialSelecionado = oficialSelecionado;
    }

    public String getIntimacaoLabelMsg() {
        return intimacaoLabelMsg;
    }

    public void setIntimacaoLabelMsg(String intimacaoLabelMsg) {
        this.intimacaoLabelMsg = intimacaoLabelMsg;
    }

}
