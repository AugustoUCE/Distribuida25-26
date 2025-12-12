package com.programacion.distribuida.authors.servicios;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.InetAddress;
import java.util.UUID;


@ApplicationScoped
public class AuthorsLifecycle {
    @Inject
    @ConfigProperty(name = "consul.host", defaultValue = "127.0.0.1")
    String consulHost;
    @Inject
    @ConfigProperty(name = "consul.port", defaultValue = "8500")
    Integer consulPort;
    @Inject
    Vertx vertx;

    @Inject
    @ConfigProperty(name="quarkus.http.port" ,  defaultValue = "8070")
    Integer appPort;
    String serviceId;



    public void init(@Observes StartupEvent event, Vertx vertx) {
        System.out.println("*****AuthorsLifecycle.init()");
        System.out.println(vertx);
        try{
        ConsulClientOptions options = new ConsulClientOptions()
                        .setHost(consulHost)
                        .setPort(consulPort);

        ConsulClient consulClient = ConsulClient.create(vertx, options);

        serviceId= UUID.randomUUID().toString();
        var ipAddress= InetAddress.getLocalHost().getHostAddress();
        var urlcheck =String.format("http://%s:%d/ping",ipAddress,appPort);
        var checkOptions = new CheckOptions()
                .setHttp(urlcheck)
                .setInterval("10s")
                .setDeregisterAfter("10s")
                ;

        ServiceOptions serviceOptions = new ServiceOptions()
                .setName("app-autor 1")
                .setId(serviceId)
                .setAddress("127.0.0.1")
                .setPort(appPort)
                .setCheckOptions(checkOptions)

                ;


        consulClient.registerService(serviceOptions).subscribe().with(
                ok -> System.out.println("Servicio registrado en Consul"),
                err -> System.out.println("ERROR registrando: " + err));

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
