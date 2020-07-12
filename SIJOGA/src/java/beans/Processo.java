/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * @author rfabini
 */

@Entity
@Table(name = "tb_processo")
public class Processo {
    private Integer id;
    private String status;
    private String vencedor;
    private Usuario juiz;
    private Usuario promovida;
    private Usuario promovente;
    private List<Fase> fases;
    private Date dtCriacao;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVencedor() {
        return vencedor;
    }

    public void setVencedor(String vencedor) {
        this.vencedor = vencedor;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "juiz_id", updatable = true)
    public Usuario getJuiz() {
        return juiz;
    }

    public void setJuiz(Usuario juiz) {
        this.juiz = juiz;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parte_promovida_id", updatable = true)
    public Usuario getPromovida() {
        return promovida;
    }

    public void setPromovida(Usuario promovida) {
        this.promovida = promovida;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parte_promovente_id", updatable = true)
    public Usuario getPromovente() {
        return promovente;
    }

    public void setPromovente(Usuario promovente) {
        this.promovente = promovente;
    }

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy
    public List<Fase> getFases() {
        return fases;
    }

    public void setFases(List<Fase> fases) {
        this.fases = fases;
    }

    @Column(name = "dt_criacao")
    @Temporal(TemporalType.DATE)
    public Date getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }


}
