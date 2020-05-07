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
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author rfabini
 */

public class UsuarioDAO {    
 
    Connection con;
    public UsuarioDAO() {       

    }
    
    public static Usuario getUsuario(int cpf, String senha){
        
        // Instancia e inicia a sessao Hibernate
        Session session = HibernateUtil.getSessionFactory().
        openSession();

        // Inicia a transação
        session.beginTransaction();
        
        // Recebe o usuario com esse CPF e senha
        List<Usuario> usuarios = session.createQuery("from Usuario where cpf="+cpf+" and senha='"+senha+"'").list();
                      
        // Finaliza a transação
        session.getTransaction().commit();
        
        // Finaliza sessão
        session.close();
        
        // Retorna o primeiro usuario que achar
        return usuarios.size() > 0 ? usuarios.get(0) : null;

    }

}
