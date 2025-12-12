package com.programacion.distribuida.authors.dto;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/authors")
public interface AuthorRestClient {
    
    @GET
    @Path("/find/{isbn}")
    public Response findById(@PathParam("isbn") String isbn);
}
