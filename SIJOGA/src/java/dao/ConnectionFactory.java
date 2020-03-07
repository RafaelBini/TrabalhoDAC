/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rfabini
 */
public class ConnectionFactory {
    public static Connection getConnection() {
    try {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/SIJOGA","postgres", "senha123");
    }catch(Exception e) {
            throw new RuntimeException(e);
    }

    }
    
    public static ResultSet Consultar(String query, String[] params) throws SQLException, ClassNotFoundException {       
        
        PreparedStatement st = ConnectionFactory.getConnection().prepareStatement(query);
        if (params != null)
            for(int i=0; i<params.length; i++){
                st.setString(i+1, params[i]);
            }        
        
        ResultSet rs = st.executeQuery();        

        return rs;
    }
}
