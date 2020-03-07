/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;

/**
 *
 * @author rfabini
 */
public class LoginBean implements Serializable{
    private int cpf;
    private String nome;

    public int getCpf() {
        return cpf;
    }

    public void setCpf(int id) {
        this.cpf= id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
