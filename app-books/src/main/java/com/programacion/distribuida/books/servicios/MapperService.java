package com.programacion.distribuida.books.servicios;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.modelmapper.ModelMapper;

public class MapperService {

    @Produces
    @ApplicationScoped
    public ModelMapper mapper() {
       return  new ModelMapper() ;
    }
}
