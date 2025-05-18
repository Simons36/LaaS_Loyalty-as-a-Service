package org.acme;

import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.net.URI;

import io.vertx.mutiny.mysqlclient.MySQLPool;

import jakarta.inject.Inject;


import org.acme.model.CrossSellingRecomendation;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;


@Path("/CrossSellingRecomendation")
public class CrossSellingRecomendationResource {
    
    @Inject
    MySQLPool client;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String kafkaServers;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }

        // Initialize Kafka producer
        DynamicTopicProducer.initialize(kafkaServers);
    }


    private void initdb(){
        client.query("DROP TABLE IF EXISTS CrossSellingRecomendations").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE CrossSellingRecomendations (" +
                "id SERIAL PRIMARY KEY, " +
                "expiration DATETIME NOT NULL, " +
                "loyaltyCard_id BIGINT UNSIGNED NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "origin_shop VARCHAR(255) NOT NULL, " +
                "destination_shop VARCHAR(255) NOT NULL)").execute())
            .flatMap(r -> client.query(
                "INSERT INTO CrossSellingRecomendations (expiration, loyaltyCard_id, product, origin_shop, destination_shop) " +
                "VALUES ('2038-01-19 03:14:07', 1, 'Beef', 'ShopA', 'ShopB')").execute())
            .await().indefinitely();
    }

    @GET
    public Multi<CrossSellingRecomendation> get() {
        return CrossSellingRecomendation.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return CrossSellingRecomendation.findById(client, id)
            .onItem().transform(crossSellingRecomendation -> crossSellingRecomendation != null ? Response.ok(crossSellingRecomendation) : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(CrossSellingRecomendation crossSellingRecomendation){
        Uni<Response> response = crossSellingRecomendation.save(client, 
                                                                crossSellingRecomendation.getExpiration(),
                                                                crossSellingRecomendation.getLoyaltyCard_id(),
                                                                crossSellingRecomendation.getProduct(),
                                                                crossSellingRecomendation.getOriginShop(),
                                                                crossSellingRecomendation.getDestinationShop())
                                .onItem().transform(id -> URI.create("/crosssellingrecomendation/" + id))
                                .onItem().transform(uri -> Response.created(uri).build());
        
        // Send to kafka
        String topic = crossSellingRecomendation.getOriginShop() + "-" + crossSellingRecomendation.getDestinationShop();
        String key = "placeholder";
        String message = crossSellingRecomendation.toJsonString();

        DynamicTopicProducer.send(topic, key, message);

        return response;
        
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return CrossSellingRecomendation.delete(client, id)
            .onItem().transform(deleted -> deleted ? Response.noContent() : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, CrossSellingRecomendation discountCoupon) {
        return CrossSellingRecomendation.update(client, id, 
                                                                discountCoupon.getExpiration(),
                                                                discountCoupon.getLoyaltyCard_id(),
                                                                discountCoupon.getProduct(),
                                                                discountCoupon.getOriginShop(),
                                                                discountCoupon.getDestinationShop())
            .onItem().transform(updated -> updated ? Response.noContent() : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }
}
