/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.dateTime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author mlcab
 */

@Entity
@Table(name = "tb_intimacao")
public class Intimacao {

    private int id;
    private Date dtIntimacao;
    private String cpf;
    private String nome;
    private String endereco;
    private Date dtExecucao;
    private String status;
    private Usuario oficial;
    private int numProcesso;

    @Id
    @Column(name = "id_intimacao")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "dt_intimacao")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtIntimacao() {
        return dtIntimacao;
    }

    public void setDtIntimacao(Date dtIntimacao) {
        this.dtIntimacao = dtIntimacao;
    }

    @Column(name = "cpf_intimado")
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Column(name = "nome_intimado")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "endereco_intimado")
    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Column(name = "dt_execucao")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtExecucao() {
        return dtExecucao;
    }

    public void setDtExecucao(Date dtExecucao) {
        this.dtExecucao = dtExecucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_oficial", updatable = true)
    public Usuario getOficial() {
        return oficial;
    }

    public void setOficial(Usuario oficial) {
        this.oficial = oficial;
    }

    @Column(name = "num_processo")
    public int getNumProcesso() {
        return numProcesso;
    }

    public void setNumProcesso(int numProcesso) {
        this.numProcesso = numProcesso;
    }


}
