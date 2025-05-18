package org.acme;

import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.net.URI;

import org.acme.model.DiscountCoupon;
import io.vertx.mutiny.mysqlclient.MySQLPool;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;

@Path("/Discountcoupon")
public class DiscountCouponResource {

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

    private void initdb() {
        // In a production environment this configuration SHOULD NOT be used
        client.query("DROP TABLE IF EXISTS DiscountCoupons").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE DiscountCoupons (" +
                "id SERIAL PRIMARY KEY, " +
                "expiration DATETIME NOT NULL, " +
                "loyaltyCard_id BIGINT UNSIGNED NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "discount_percentage FLOAT NOT NULL)").execute())
            .flatMap(r -> client.query(
                "INSERT INTO DiscountCoupons (expiration, loyaltyCard_id, product, discount_percentage) " +
                "VALUES ('2038-01-19 03:14:07', 1, 'Beef', 10.0)").execute())
            .await().indefinitely();
    }

    @GET
    public Multi<DiscountCoupon> get() {
        return DiscountCoupon.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return DiscountCoupon.findById(client, id)
            .onItem().transform(discountCoupon -> discountCoupon != null ? Response.ok(discountCoupon) : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(DiscountCoupon discountCoupon) {
        Uni<Response> response =  discountCoupon.save(client, discountCoupon.getExpiration(), discountCoupon.getLoyaltyCard_id(), discountCoupon.getDiscountType())
            .onItem().transform(id -> URI.create("/discountcoupon/" + id))
            .onItem().transform(uri -> Response.created(uri).build());

        // Send message to Kafka topic

        String topic = String.valueOf(discountCoupon.getLoyaltyCard_id());
        String key = "placeholder";
        String message = discountCoupon.toJsonString();

        DynamicTopicProducer.send(topic, key, message);

        return response;
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return DiscountCoupon.delete(client, id)
            .onItem().transform(deleted -> deleted ? Response.noContent() : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, DiscountCoupon discountCoupon) {
        return DiscountCoupon.update(client, id, discountCoupon.getExpiration(), discountCoupon.getLoyaltyCard_id(), discountCoupon.getDiscountType())
            .onItem().transform(updated -> updated ? Response.noContent() : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }


    
}
