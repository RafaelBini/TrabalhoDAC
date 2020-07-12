/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * @author rfabini
 */
@Entity
@Table(
        name = "tb_usuario",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"login"}),
                @UniqueConstraint(columnNames = {"cpf"})
        }
)
public class Usuario {

    private Long id;
    private String login;
    private String nome;
    private String senha;
    private String tipo;
    private String cpf;
    private String email;
    private String rua;
    private Long numero;
    private Cidade cidade;
    private Usuario advogado;
    private List<Usuario> clientes;
    private List<Processo> promovidaProcessos;
    private List<Processo> promoventeProcessos;
    private List<Processo> juizProcessos;
    private List<Fase> fasesCriadas;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull()
    public String getNome() {
        return nome;
    }

    @Column(unique = true)
    @NotNull()
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @NotNull()
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    @ManyToOne
    @JoinColumn(name = "cidade_id")
    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    @Column(unique = true)
    @NotNull()
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "advogado_id", updatable = true)
    public Usuario getAdvogado() {
        return advogado;
    }

    public void setAdvogado(Usuario advogado) {
        this.advogado = advogado;
    }

    @OneToMany(mappedBy = "advogado", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Usuario> getClientes() {
        return clientes;
    }

    public void setClientes(List<Usuario> clientes) {
        this.clientes = clientes;
    }

    @OneToMany(mappedBy = "promovida", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Processo> getPromovidaProcessos() {
        return promovidaProcessos;
    }

    public void setPromovidaProcessos(List<Processo> promovidaProcessos) {
        this.promovidaProcessos = promovidaProcessos;
    }

    @OneToMany(mappedBy = "promovente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Processo> getPromoventeProcessos() {
        return promoventeProcessos;
    }

    public void setPromoventeProcessos(List<Processo> promoventeProcessos) {
        this.promoventeProcessos = promoventeProcessos;
    }

    @OneToMany(mappedBy = "juiz", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Processo> getJuizProcessos() {
        return juizProcessos;
    }

    public void setJuizProcessos(List<Processo> juizProcessos) {
        this.juizProcessos = juizProcessos;
    }

    @OneToMany(mappedBy = "criador", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Fase> getFasesCriadas() {
        return fasesCriadas;
    }

    public void setFasesCriadas(List<Fase> fasesCriadas) {
        this.fasesCriadas = fasesCriadas;
    }


}
