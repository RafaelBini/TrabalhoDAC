/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import beans.Intimacao;
import beans.Usuario;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * REST Web Service
 *
 * @author thyag
 */
@Path("/intimacoes")
public class IntimacaoResource {

    @Context
    private UriInfo context;

    public IntimacaoResource() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criarIntimacao(Intimacao i) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        i.setStatus("Não efetuada");
        i.setDtIntimacao(Date.from(Instant.now()));
        
        Usuario oficial = Usuario.findById(i.getOficial().getId());
        i.setOficial(oficial);
        
        session.save(i);
        session.getTransaction().commit();
        session.close();

        return Response.created(URI.create("")).build();
    }

}
