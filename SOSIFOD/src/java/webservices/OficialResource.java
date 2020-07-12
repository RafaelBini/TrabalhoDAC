/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import beans.Usuario;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author thyag
 */
@Path("/oficiais")
public class OficialResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of OficialResource
     */
    public OficialResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Usuario> listarOficiais() {
        ArrayList<Usuario> oficiais = Usuario.listarOficiais();
        return oficiais;
    }
}
