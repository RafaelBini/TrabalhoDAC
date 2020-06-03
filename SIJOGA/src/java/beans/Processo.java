/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author rfabini
 */

@Entity
@Table(name="tb_processo")
public class Processo {
    private Integer id;
    private String status;
    private String vencedor;
    private Usuario juiz;
    private Usuario promovida;
    private Usuario promovente;

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
    
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="juiz_id", updatable=true)  
    public Usuario getJuiz() {
        return juiz;
    }

    public void setJuiz(Usuario juiz) {
        this.juiz = juiz;
    }

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="parte_promovida_id", updatable=true)    
    public Usuario getPromovida() {
        return promovida;
    }

    public void setPromovida(Usuario promovida) {
        this.promovida = promovida;
    }

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="parte_promovente_id", updatable=true)    
    public Usuario getPromovente() {
        return promovente;
    }

    public void setPromovente(Usuario promovente) {
        this.promovente = promovente;
    }
    
    
}
