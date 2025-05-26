package org.acme;

import java.util.List;

import org.acme.model.SelledProduct;
import org.acme.model.SelledProductByDiscountCouponAnalytics;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("SelledProduct")
public class SelledProductResource {

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

    // 5 tables:
    // SelledProductsByDiscountCoupon
    // SelledProductsByCustomerID
    // SelledProductsByLocation
    // SelledProductsByLoyaltyCardID
    // SelledProductsByShopID
    private void initdb(){
        client.query("DROP TABLE IF EXISTS SelledProductsByDiscountCoupon").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductsByDiscountCoupon (" +
                "id SERIAL PRIMARY KEY, " +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "coupons_used INT NOT NULL, " +
                "value_discounted FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductsByDiscountCoupon (calculated_at, number_of_purchases, product, coupons_used, value_discounted, description) " +
                "VALUES (NOW(), 5, 'Beef', 4, 3, 5.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductsByCustomerID").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductsByCustomerID (" +
                "id SERIAL PRIMARY KEY, " +
                "customer_id VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductsByCustomerID (customer_id, calculated_at, number_of_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES (1, NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductsByLocation").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductsByLocation (" +
                "id SERIAL PRIMARY KEY, " +
                "location VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductsByLocation (location, calculated_at, number_of_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES ('Lisbon', NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductsByLoyaltyCardID").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductsByLoyaltyCardID (" +
                "id SERIAL PRIMARY KEY, " +
                "loyalty_card_id VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductsByLoyaltyCardID (loyalty_card_id, calculated_at, number_of_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES (1, NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductsByShopID").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductsByShopID (" +
                "id SERIAL PRIMARY KEY, " +
                "shop_id VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductsByShopID (shop_id, calculated_at, number_of_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES (1, NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        
    }


    @GET
    @Path("DiscountCoupon")
    public Multi<SelledProductByDiscountCouponAnalytics> getAllDiscountCoupon() {
        return SelledProductByDiscountCouponAnalytics.findAll(client);
    }

    @GET
    @Path("DiscountCoupon/{id}")
    public Uni<Response> getDiscountCouponByID(Long id) {
        return SelledProductByDiscountCouponAnalytics.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("CustomerID")
    public Multi<SelledProductByDiscountCouponAnalytics> getAllCustomerID() {
        return SelledProductByDiscountCouponAnalytics.findAll(client);
    }

    @GET
    @Path("CustomerID/{id}")
    public Uni<Response> getCustomerIDByID(Long id) {
        return SelledProductByDiscountCouponAnalytics.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("Location")
    public Multi<SelledProductByDiscountCouponAnalytics> getAllLocation() {
        return SelledProductByDiscountCouponAnalytics.findAll(client);
    }

    @GET
    @Path("Location/{id}")
    public Uni<Response> getLocationByID(Long id) {
        return SelledProductByDiscountCouponAnalytics.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("LoyaltyCardID")
    public Multi<SelledProductByDiscountCouponAnalytics> getAllLoyaltyCardID() {
        return SelledProductByDiscountCouponAnalytics.findAll(client);
    }

    @GET
    @Path("LoyaltyCardID/{id}")
    public Uni<Response> getLoyaltyCardIDByID(Long id) {
        return SelledProductByDiscountCouponAnalytics.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("ShopID")
    public Multi<SelledProductByDiscountCouponAnalytics> getAllShopID() {
        return SelledProductByDiscountCouponAnalytics.findAll(client);
    }

    @GET
    @Path("ShopID/{id}")
    public Uni<Response> getShopIDByID(Long id) {
        return SelledProductByDiscountCouponAnalytics.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }


    // @POST
    // public Uni<Response> create(List<SelledProduct> SelledProductList){
        
    // }



}
