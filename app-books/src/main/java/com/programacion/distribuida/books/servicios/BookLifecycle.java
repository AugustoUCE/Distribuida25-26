package com.programacion.distribuida.books.servicios;


import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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

@ApplicationScoped
public class BookLifecycle {

    @Inject
    @ConfigProperty(name = "consul.host", defaultValue = "127.0.0.1")
    String consulHost;

    @Inject
    @ConfigProperty(name = "consul.port", defaultValue = "8500")
    Integer consulPort;

    @Inject
    Vertx vertx;

    @Inject
    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8030")
    Integer appPort;
    
    String serviceId;
    ConsulClient consulClient;

    public void init(@Observes StartupEvent event, Vertx vertx) {
        System.out.println("*****BookLifecycle.init() - Registrando servicio en Consul");
        System.out.println(vertx);
        try {
            ConsulClientOptions options = new ConsulClientOptions()
                    .setHost(consulHost)
                    .setPort(consulPort);

            this.consulClient = ConsulClient.create(vertx, options);

            serviceId = UUID.randomUUID().toString();
            var ipAddress = InetAddress.getLocalHost().getHostAddress();
            var urlcheck = String.format("http://%s:%d/ping", ipAddress, appPort);
            var checkOptions = new CheckOptions()
                    .setHttp(urlcheck)
                    .setInterval("10s")
                    .setDeregisterAfter("10s");
            var tags = List.of(
                    "traefik.enable=true",
                    "traefik.http.routers.books.rule=PathPrefix(`/app-books`)",
                    "traefik.http.middlewares.books-stripprefix.stripPrefix.prefixes=/app-books",
                    "traefik.http.routers.books.middlewares=books-stripprefix"
            );

            ServiceOptions serviceOptions = new ServiceOptions()
                    .setName("app-books")
                    .setId(serviceId)
                    .setAddress(ipAddress)
                    .setPort(appPort)
                    .setCheckOptions(checkOptions)
                    .setTags(tags);

            consulClient.registerService(serviceOptions).subscribe().with(
                    ok -> System.out.println("✓ Servicio app-books registrado en Consul"),
                    err -> System.out.println("✗ ERROR registrando app-books: " + err));

        } catch (Exception e) {
            System.err.println("Error en BookLifecycle.init(): ");
            e.printStackTrace();
        }
    }

    public void shutdown(@Observes ShutdownEvent event) {
        System.out.println("*****BookLifecycle.shutdown() - Desregistrando servicio");
        if (consulClient != null && serviceId != null) {
            consulClient.deregisterService(serviceId).subscribe().with(
                    ok -> System.out.println("✓ Servicio app-books desregistrado de Consul"),
                    err -> System.out.println("✗ ERROR desregistrando app-books: " + err));
        }
    }
}