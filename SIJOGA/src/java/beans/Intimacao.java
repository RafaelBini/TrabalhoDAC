package beans;

import java.io.Serializable;

public class Intimacao implements Serializable{

    private String nome;
    private String cpf;
    private String endereco;
    private Oficial oficial;
    private int numProcesso;

    public Intimacao() {
    }

    public Intimacao(String nome, String cpf, String endereco, Oficial oficial, int numProcesso) {
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.oficial = oficial;
        this.numProcesso = numProcesso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getNumProcesso() {
        return numProcesso;
    }

    public void setNumProcesso(int numProcesso) {
        this.numProcesso = numProcesso;
    }

    public Oficial getOficial() {
        return oficial;
    }

    public void setOficial(Oficial oficial) {
        this.oficial = oficial;
    }

}
