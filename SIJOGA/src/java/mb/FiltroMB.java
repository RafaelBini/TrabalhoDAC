/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Fase;
import beans.Processo;
import beans.Usuario;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * @author rfabini
 */
@Named(value = "filtroMB")
@ViewScoped()
public class FiltroMB implements Serializable {
    private String filtroStatus;
    private String filtroAtuacao;
    private String filtroResultado;
    private List<Processo> processos;

    @PostConstruct
    public void init() {

        loadProcessos();
    }


    public void loadProcessos() {
        // Recebe o usuario logado
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario loggedUser = (Usuario) context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{loginMB.loggedUser}", Usuario.class)
                .getValue(context.getELContext());


        // Abre sessao
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Se é parte,
        if ("Parte".equals(loggedUser.getTipo())) {
            // Recebe todos os processos
            Query query = session.createQuery("FROM Processo p WHERE p.promovente.id = :id OR p.promovida.id = :id ORDER BY p.id");
            query.setLong("id", loggedUser.getId());
            this.processos = query.list();
        }
        // Se é juiz,
        if ("Juíz".equals(loggedUser.getTipo())) {
            // Recebe todos os processos
            Query query = session.createQuery("FROM Processo p WHERE p.juiz.id = :id ORDER BY p.id");
            query.setLong("id", loggedUser.getId());
            this.processos = query.list();
        }
        // Se é adovogado,
        else if ("Advogado".equals(loggedUser.getTipo())) {

            // Inicializa os filtros
            String fs = "";
            String fa = "";
            String fr = "";

            // Filtra Status
            if (!"Todos".equals(this.filtroStatus)) {
                if ("Aberto".equals(this.filtroStatus)) {
                    fs = " AND p.status != 'Encerrado' ";
                } else if ("Encerrado".equals(this.filtroStatus)) {
                    fs = " AND p.status = 'Encerrado' ";
                }
            }

            // Filtra Atuação
            if (!"Todos".equals(this.filtroAtuacao)) {
                if ("Promovente".equals(this.filtroAtuacao)) {
                    fa = " AND p.promovente.advogado.id = :id ";
                } else if ("Promovido".equals(this.filtroAtuacao)) {
                    fa = " AND p.promovida.advogado.id = :id ";
                }
            }

            // Filtra Resultado
            if (!"Todos".equals(this.filtroResultado)) {
                if ("Ganhei".equals(this.filtroResultado)) {
                    fr = " AND ((p.promovente.advogado.id = :id AND p.vencedor = 'Promovente') OR (p.promovida.advogado.id = :id AND p.vencedor = 'Promovido')) ";
                } else if ("Perdi".equals(this.filtroResultado)) {
                    fr = " AND ((p.promovente.advogado.id = :id AND p.vencedor = 'Promovido') OR (p.promovida.advogado.id = :id AND p.vencedor = 'Promovente')) ";
                }
            }

            // Recebe todos os processos
            Query query = session.createQuery("FROM Processo p WHERE (p.promovente.advogado.id = :id OR p.promovida.advogado.id = :id) " + fs + fa + fr + " ORDER BY p.id");
            query.setLong("id", loggedUser.getId());
            this.processos = query.list();

        }

        // Fecha sessao
        session.getTransaction().commit();
        session.close();
    }

    public String getResultado(Processo p) {
        // Recebe o usuario logado
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario loggedUser = (Usuario) context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{loginMB.loggedUser}", Usuario.class)
                .getValue(context.getELContext());

        // Se sou Advogado,
        if ("Advogado".equals(loggedUser.getTipo())) {
            // Se venci
            if ((p.getPromovente().getAdvogado().getId() == loggedUser.getId()
                    && "Promovente".equals(p.getVencedor()))
                    || (p.getPromovida().getAdvogado().getId() == loggedUser.getId()
                    && "Promovido".equals(p.getVencedor()))) {

                return "Ganhei";

            } else if ((p.getPromovente().getAdvogado().getId() == loggedUser.getId()
                    && "Promovido".equals(p.getVencedor()))
                    || (p.getPromovida().getAdvogado().getId() == loggedUser.getId()
                    && "Promovente".equals(p.getVencedor()))) {
                return "Perdi";
            }
        }
        // Se sou parte
        else if ("Parte".equals(loggedUser.getTipo())) {
            // Se venci
            if ((p.getPromovente().getId() == loggedUser.getId()
                    && "Promovente".equals(p.getVencedor()))
                    || (p.getPromovida().getId() == loggedUser.getId()
                    && "Promovido".equals(p.getVencedor()))) {

                return "Ganhei";

            } else if ((p.getPromovente().getId() == loggedUser.getId()
                    && "Promovido".equals(p.getVencedor()))
                    || (p.getPromovida().getId() == loggedUser.getId()
                    && "Promovente".equals(p.getVencedor()))) {
                return "Perdi";
            }
        }


        return "-";

    }

    public String getFiltroStatus() {
        return filtroStatus;
    }

    public void setFiltroStatus(String filtroStatus) {
        this.filtroStatus = filtroStatus;
    }

    public String getFiltroAtuacao() {
        return filtroAtuacao;
    }

    public void setFiltroAtuacao(String filtroAtuacao) {
        this.filtroAtuacao = filtroAtuacao;
    }

    public String getFiltroResultado() {
        return filtroResultado;
    }

    public void setFiltroResultado(String filtroResultado) {
        this.filtroResultado = filtroResultado;
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Processo> processos) {
        this.processos = processos;
    }


}
