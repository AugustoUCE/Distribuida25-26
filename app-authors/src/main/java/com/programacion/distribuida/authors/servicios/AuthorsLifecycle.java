package com.programacion.distribuida.authors.servicios;

import io.quarkus.runtime.ShutdownEvent;
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
import java.util.List;
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
    ConsulClient consulClient;



    public void init(@Observes StartupEvent event, Vertx vertx) {
        System.out.println("*****AuthorsLifecycle.init() - Registrando servicio en Consul");
        System.out.println(vertx);
        try{
        ConsulClientOptions options = new ConsulClientOptions()
                        .setHost(consulHost)
                        .setPort(consulPort);

        this.consulClient = ConsulClient.create(vertx, options);

        serviceId= UUID.randomUUID().toString();
        var ipAddress= InetAddress.getLocalHost().getHostAddress();
        var urlcheck =String.format("http://%s:%d/ping",ipAddress,appPort);
        var checkOptions = new CheckOptions()
                .setHttp(urlcheck)
                .setInterval("10s")
                .setDeregisterAfter("10s")
                ;

//        var tags = List.of(
//                "traefik.enable=true",
//                "traefik.http.routers.authors.rule=PathPrefix(`/app-authors`)",
//                "traefik.http.middlewaresauthors-stripprefix.stripPrefix.prefixes=/app-authors",
//                "traefik.http.routers.authors.middlewares=authors-stripprefix"
//
//
//        );

            var tags = List.of(
                    "traefik.enable=true",
                    "traefik.http.routers.authors.rule=PathPrefix(`/app-authors`)",
                    "traefik.http.middlewares.authors-stripprefix.stripPrefix.prefixes=/app-authors",
                    "traefik.http.routers.authors.middlewares=authors-stripprefix"
            );
        ServiceOptions serviceOptions = new ServiceOptions()
                .setName("app-authors")
                .setId(serviceId)
                .setAddress(ipAddress)
                .setPort(appPort)
                .setCheckOptions(checkOptions)
                .setTags(tags)
                ;

        consulClient.registerService(serviceOptions).subscribe().with(
                ok -> System.out.println("✓ Servicio app-authors registrado en Consul"),
                err -> System.out.println("✗ ERROR registrando app-authors: " + err));

        }
        catch(Exception e){
            System.err.println("Error en AuthorsLifecycle.init(): ");
            e.printStackTrace();
        }
    }

    public void shutdown(@Observes ShutdownEvent event) {
        System.out.println("*****AuthorsLifecycle.shutdown() - Desregistrando servicio");
        if (consulClient != null && serviceId != null) {
            consulClient.deregisterService(serviceId).subscribe().with(
                    ok -> System.out.println("✓ Servicio app-authors desregistrado de Consul"),
                    err -> System.out.println("✗ ERROR desregistrando app-authors: " + err));
        }
    }
}
