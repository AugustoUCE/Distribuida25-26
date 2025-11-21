package com.programacion.distribuida.books.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
@Getter

@Setter
@ToString
public class BookDto {
    private String isbn;
    private String title;
    private Double price;

    private Integer InventorySold;
    private Integer inventorySupplied;

    private List<AuthorDto> authors;
}
