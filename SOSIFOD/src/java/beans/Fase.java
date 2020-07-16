package beans;

import java.io.Serializable;
import java.util.Set;

public class Fase implements Serializable {

    private Processo processo;
    private String titulo;
    private String descricao;
    private String tipo;
    
    public Fase() {
    }

    public Fase(Processo processo, String titulo, String descricao, String tipo) {
        this.processo = processo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;

        
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    
    

}
