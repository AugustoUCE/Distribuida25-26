package com.programacion.distribuida.books.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
@Entity
@Table(name = "books")
@Getter
@Setter
@ToString(exclude = {"inventory"})
public class Book {

    @Id
    private String isbn;

    private String title;

    private Double price;

    // Relación uno a uno inversa (no propietaria)
    //@OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OneToOne(mappedBy = "book")
    private Inventory inventory;

    private Integer version;
}
