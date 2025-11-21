package com.programacion.distribuida.books.rest;

import java.util.List;

import com.programacion.distribuida.books.db.Book;
import com.programacion.distribuida.books.dto.AuthorDto;
import com.programacion.distribuida.books.dto.BookDto;
import com.programacion.distribuida.books.repo.BookRepository;

import com.programacion.distribuida.clients.AuthorRestClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import java.net.URI;
//
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//@Path("/books")
//@ApplicationScoped
//
//public class BookRest {
//
//
//    @Inject
//    BookRepository bookRepository;
//
//    private AuthorRestClient client;
//
//    @PostConstruct
//    public void init() {
//        var authorsServer = "http://localhost:8070";
//
//         client = RestClientBuilder.newBuilder()
//                 .baseUri(authorsServer)
//                 .build(AuthorRestClient.class);
//    }
//
//    @GET
//    @Path("/{isbn}")
//    public Response findByIsbn(@PathParam("isbn") String isbn){
////        if(isbn==null) {
////            return Response.status(Response.Status.BAD_REQUEST).build();
////        }
////        String normalized = isbn.replace("-", "").trim();
////        var obj = bookRepository.findByIdOptional(isbn);
////        if(obj.isEmpty() && !normalized.equals(isbn)){
////            obj = bookRepository.findByIdOptional(normalized);
////        }
////        if(obj.isEmpty()){
////            return Response.status(Response.Status.NOT_FOUND).build();
////        }
////        Book book = obj.get();
////        BookDto ret = toDto(book);
////        return Response.ok(ret).build();
//
////         var client = RestClientBuilder.newBuilder()
////             .baseUri("http://localhost:8070/")
////             .build(AuthorRestClient.class);
//
//             return bookRepository.findByIdOptional(isbn)
//             .map(
//                     book -> {
//                 var authors = client.findbyBook(isbn);
//                 var dto= toDto(book);
//                 dto.setAuthors(authors);
//                 return Response.ok(dto).build();
//
//             })
//             .orElse(Response.status(Response.Status.NOT_FOUND).build());
//
//
//
//
//    }
//    @GET
//    public List<BookDto> findAll(){
//        return bookRepository.listAll().stream()
//                .map(this::toDto)
//                .toList();
//    }
//
//    private BookDto toDto(Book book){
//        BookDto dto = new BookDto();
//        if(book==null) return dto;
//        dto.setIsbn(book.getIsbn());
//        dto.setTitle(book.getTitle());
//        dto.setPrice(book.getPrice());
//        if(book.getInventory()!=null){
//            dto.setInventorySold(book.getInventory().getSold());
//            dto.setInventorySupplied(book.getInventory().getSupplied());
//        }
//        dto.setAuthors(List.of());
//        return dto;
//    }
//}


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/books")
@ApplicationScoped
public class BookRest {

    @Inject
    BookRepository bookRepository;

    private AuthorRestClient client;

    @PostConstruct
    public void init() {
        var authorsServer = "http://localhost:8070";
        client = RestClientBuilder.newBuilder()
                .baseUri(authorsServer)
                .build(AuthorRestClient.class);
    }

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
}
