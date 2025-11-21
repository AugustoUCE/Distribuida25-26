package com.programacion.distribuida.clients;
import com.programacion.distribuida.books.dto.AuthorDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/authors")

public interface AuthorRestClient {

    @GET
    @Path("/find/{isbn}")
    List<AuthorDto> findbyBook(@PathParam("isbn") String isbn);

    
}
