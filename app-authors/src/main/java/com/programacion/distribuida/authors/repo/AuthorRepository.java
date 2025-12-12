package com.programacion.distribuida.authors.repo;


import com.programacion.distribuida.authors.db.Author;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class AuthorRepository implements PanacheRepositoryBase<Author,Integer> {
    public List<Author> findByBook(String isbn){
        // Query Author entities whose id appears in the books_authors join table
        return this.list(
            "select a from Author a where a.id in (select ba.id.authorId from BookAuthor ba where ba.id.bookIsbn = ?1)",
            isbn
        );
    }
}
