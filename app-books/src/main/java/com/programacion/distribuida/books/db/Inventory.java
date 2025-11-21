package com.programacion.distribuida.books.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Objects;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@ToString(exclude = {"book"})
public class Inventory {

    @Id
    @Column(name = "book_isbn")
    private String isbn;

    @OneToOne
    @MapsId
    @JoinColumn(name = "book_isbn")
    private Book book;

    private Integer sold;
    private Integer supplied;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory)) return false;
        Inventory other = (Inventory) o;
        String isbnA = this.book != null ? this.book.getIsbn() : null;
        String isbnB = other.book != null ? other.book.getIsbn() : null;
        return Objects.equals(isbnA, isbnB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book != null ? book.getIsbn() : null);
    }

}
