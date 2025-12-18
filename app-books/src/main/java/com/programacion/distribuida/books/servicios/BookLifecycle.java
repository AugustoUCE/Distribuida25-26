package com.programacion.distribuida.books.servicios;


import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.mutiny.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;




import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

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

    public void init(@Observes StartupEvent event, Vertx vertx) {
        System.out.println("*****AuthorsLifecycle.init() books");
        System.out.println(vertx);
        try {
            ConsulClientOptions options = new ConsulClientOptions()
                    .setHost(consulHost)
                    .setPort(consulPort);

            ConsulClient consulClient = ConsulClient.create(vertx, options);

            serviceId = UUID.randomUUID().toString();
            var ipAddress = InetAddress.getLocalHost().getHostAddress();
            var urlcheck = String.format("http://%s:%d/health", ipAddress, appPort);
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
                    ok -> System.out.println("Servicio registrado en Consul"),
                    err -> System.out.println("ERROR registrando: " + err));

        } catch (Exception e) {
            e.printStackTrace();


        }

    }
}