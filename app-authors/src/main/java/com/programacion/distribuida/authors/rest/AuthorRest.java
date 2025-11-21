package com.programacion.distribuida.authors.rest;

import com.programacion.distribuida.authors.db.Author;
import com.programacion.distribuida.authors.repo.AuthorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.flogger.Flogger;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Path("/authors")
public class AuthorRest {
    @Inject
    AuthorRepository authorRepository;

    //configuracion DI
    @Inject
    @ConfigProperty(name="quarkus.http.port")
    private Integer httpPort;


    @GET
    public List<Author> findAll() {
        return authorRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Integer id) {

        /*var obj = authorRepository.findByIdOptional(id);
        if (obj.isPresent()){
            return Response.ok(obj.get()).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }*/
        return authorRepository.findByIdOptional(id)
                .map(obj ->{

                    obj.setName(obj.getName() +" "+ httpPort);
                    return obj;
                })
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();



    }


    @GET
    @Path("/find/{isbn}")
    public List<Author> findByBook(@PathParam("isbn") String isbn) {
        return authorRepository.findByBook(isbn).stream()
                .map(obj ->{

                    var newName = String.format("%s (%s)",
                            obj.getName(), httpPort);
                            obj.setName(newName);
                    return obj;

                })
                .toList();


    }

    @GET
    @Path("/test")
    public String test(){
        Config config = ConfigProvider.getConfig();
        config.getConfigSources().forEach(
                obj ->{
                    System.out.printf("%d -> %s\n", obj.getOrdinal(),obj.getName());
                }
                );

        // recuperar el valor de configuracion

        String url = config.getValue("quarkus.datasource.jdbc.url",String.class);
        Integer puerto= config.getValue("quarkus.http.port",Integer.class);

        System.out.println( "---------------");
        System.out.println( url );
        System.out.println( puerto );

        return "ok";
    }

}
