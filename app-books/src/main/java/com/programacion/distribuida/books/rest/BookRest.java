package com.programacion.distribuida.books.rest;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.programacion.distribuida.books.db.Book;
import com.programacion.distribuida.books.dto.AuthorDto;
import com.programacion.distribuida.books.dto.BookDto;
import com.programacion.distribuida.books.repo.BookRepository;

import com.programacion.distribuida.books.clients.AuthorRestClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.stork.Stork;
import io.smallrye.stork.api.Service;
import io.smallrye.stork.api.ServiceInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/books")
@ApplicationScoped
public class BookRest {

    @Inject
    BookRepository bookRepository;

    @Inject
    @RestClient
    AuthorRestClient client;



//    @PostConstruct
//    public void init() {
//        var authorsServer = "http://localhost:8070";
//        client = RestClientBuilder.newBuilder()
//                .baseUri(authorsServer)
//                .build(AuthorRestClient.class);
//    }

    // Devuelve un libro por ISBN con autores
    @GET
    @Path("/{isbn}")
    public Response findByIsbn(@PathParam("isbn") String isbn){
        return bookRepository.findByIdOptional(isbn)
                .map(book -> {
                    var authors = client.findbyBook(isbn);
                    var dto = toDto(book, authors);
                    return Response.ok(dto).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // Devuelve todos los libros con autores
    @GET
    public List<BookDto> findAll() {
        return bookRepository.listAll().stream()
                .map(book -> {
                    var authors = client.findbyBook(book.getIsbn());
                    return toDto(book, authors);
                })
                .toList();
    }

    // Convierte Book a BookDto incluyendo autores
    private BookDto toDto(Book book, List<AuthorDto> authors) {
        BookDto dto = new BookDto();
        if(book == null) return dto;
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setPrice(book.getPrice());
        if(book.getInventory() != null){
            dto.setInventorySold(book.getInventory().getSold());
            dto.setInventorySupplied(book.getInventory().getSupplied());
        }
        dto.setAuthors(authors != null ? authors : List.of());
        return dto;
    }

//    public Response test() {
//        var stork = Stork.getInstance();
//        Map<String, Service> services = stork.getServices();
//
//        services.entrySet()
//                .stream()
//                .forEach(
//                        it -> {
//                            String key = it.getKey();
//                            Service service = it.getValue();
//                            System.out.println("--group" + key);
//                            Multi<ServiceInstance> instancias = service.getInstances()
//                                    .onItem()
//                                    .transformToMulti(items -> Multi.createFrom().iterable(items));
//
//                            instancias.subscribe()
//                                    .with(item -> {
//                                        System.out.println(" " + item.getHost() + "  " + item.getPort());
//                                    });
//
//                        }
//                        );
//        //buscar un serviceio seleccionar isntancias, balancear
//
////        Service service =stork.getService("authors-api");
////        List<ServiceInstance> instancias = service.getInstances().await().indefinitely();
////
////        int curIndex = index.getAndIncrement()% instancias.size();
////        var instancia = instancias.get(curIndex);
////        System.out.println("inovando auhtors -api"+instancia.getHost()+":"+ instancia.getPort());
////        return Response.ok("ok").build();
//
//    }
}
