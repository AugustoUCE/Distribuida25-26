package com.programacion.distribuida.books.clients;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.programacion.distribuida.books.dto.AuthorDto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/authors")
@RegisterRestClient(baseUri="stork://authors-api")
public interface AuthorRestClient {

    @GET
    @Path("/find/{isbn}")
    List<AuthorDto> findbyBook(@PathParam("isbn") String isbn);



    
}
