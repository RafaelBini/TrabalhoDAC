/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Teste;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.hibernate.Session;
import util.HibernateUtil;
import beans.Usuario;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rfabini
 */
public class TesteHibernate {
    
    public TesteHibernate() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hiber() {
        Usuario u = new Usuario();
        u.setNome("Bini");
        u.setCpf(321);
        u.setSenha("asd");
        u.setTipo(2);
        
        Session session = HibernateUtil.getSessionFactory().
        openSession();

        session.beginTransaction();
        session.save(u);
        session.getTransaction().commit();
        System.out.println(u.getNome() + " Inserido com sucesso.");
    }
}
