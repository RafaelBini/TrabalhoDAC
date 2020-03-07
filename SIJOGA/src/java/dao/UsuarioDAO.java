/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import beans.Usuario;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rfabini
 */

public class UsuarioDAO {    
 
    Connection con;
    public UsuarioDAO(Connection con) {       
        this.con = con;
    }
    
    public Boolean isPassCorrect(int cpf, String senha) throws SQLException, ClassNotFoundException, Exception{

                    
        PreparedStatement st = con.prepareStatement("select cpf from usuario where cpf = ? and senha = ?");
        st.setInt(1,cpf);
        st.setString(2, senha);
        
        ResultSet rs = st.executeQuery();        

        return rs.next();
    
    }
    
    public String getNome(int cpf){
        String nome = "";   
        try{
                    
            PreparedStatement st = con.prepareStatement("select nome from usuario where cpf = ?");
            st.setInt(1,cpf);

            ResultSet rs = st.executeQuery();  
            rs.next();
            nome = rs.getString("nome");
        }
        catch(Exception ex){
            return ex.getMessage();
        }        
        return nome;
    }    
    
    
    public Boolean insert(Usuario usuario) throws SQLException, ClassNotFoundException{
        int afRows = Executar("insert into tb_usuario (login_usuario,senha_usuario,nome_usuario) values (?,?,?)",new String[]{usuario.getUser(),usuario.getSenha(),usuario.getNome()});
        if (afRows >0)
            return true;
        else
            return false;        
    }
    
    public ResultSet Consultar(String query, String[] params) throws SQLException, ClassNotFoundException {       
        
        PreparedStatement st = con.prepareStatement(query);
        if (params != null)
            for(int i=0; i<params.length; i++){
                st.setString(i+1, params[i]);
            }        
        
        ResultSet rs = st.executeQuery();        

        return rs;
    }
    
    public Integer Executar(String query, String[] params) throws SQLException, ClassNotFoundException {
        
        PreparedStatement st = con.prepareStatement(query);
        if (params != null)
            for(int i=0; i<params.length; i++){
                st.setString(i+1, params[i]);
            }        
        
        int res = st.executeUpdate();        

        return res;
    }
    
    public Integer getResultSetSize(ResultSet resultSet) throws SQLException{
        int size = 0;

        while(resultSet.next()){
            size++;
        }            
        return size;       
        
    }

}
