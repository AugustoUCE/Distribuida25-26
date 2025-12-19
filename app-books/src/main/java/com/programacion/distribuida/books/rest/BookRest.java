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
import org.jboss.logging.Logger;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/books")
@ApplicationScoped
public class BookRest {

    private static final Logger LOG = Logger.getLogger(BookRest.class);

    @Inject
    BookRepository bookRepository;

    @Inject
    @RestClient
    AuthorRestClient client;


    // Devuelve un libro por ISBN con autores
    @GET
    @Path("/{isbn}")
    public Response findByIsbn(@PathParam("isbn") String isbn){
        return bookRepository.findByIdOptional(isbn)
                .map(book -> {
                    var authors = safeFindAuthors(isbn);
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
                    var authors = safeFindAuthors(book.getIsbn());
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

    // Si authors-api no está disponible, retornamos lista vacía para no romper /books ni /books/{isbn}
    private List<AuthorDto> safeFindAuthors(String isbn) {
        try {
            return client.findbyBook(isbn);
        } catch (Exception ex) {
            LOG.warnf(ex, "No se pudo obtener autores para ISBN %s (authors-api no disponible)", isbn);
            return List.of();
        }
    }


}
