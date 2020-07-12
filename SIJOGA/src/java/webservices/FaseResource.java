/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import beans.Fase;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
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
@Path("/fases")
public class FaseResource {

    @Context
    private UriInfo context;

    public FaseResource() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response criarFase(Fase fase) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        fase.setDtCriacao(Date.from(Instant.now()));
        
        session.save(fase);
        
        session.getTransaction().commit();
        session.close();
        
        return Response.created(URI.create("")).build();
    }

}
