package org.acme;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.model.SelledProduct;
import org.acme.model.analytics.*;
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

    private static final String TOPIC_DISCOUNT_COUPON_ANALYTICS = "selled_product_by_discount_coupon";
    private static final String TOPIC_CUSTOMER_ID_ANALYTICS = "selled_product_by_customer_id";
    private static final String TOPIC_LOCATION_ANALYTICS = "selled_product_by_location";
    private static final String TOPIC_LOYALTY_CARD_ID_ANALYTICS = "selled_product_by_loyalty_card_id";
    private static final String TOPIC_SHOP_ID_ANALYTICS = "selled_product_by_shop_id";


    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }

        // Initialize Kafka producer
        DynamicTopicProducer.initialize(kafkaServers);
    }

    // 5 tables:
    // SelledProductByDiscountCoupon
    // SelledProductByCustomerID
    // SelledProductsByLocation
    // SelledProductByLoyaltyCardID
    // SelledProductByShopID
    private void initdb(){
        client.query("DROP TABLE IF EXISTS SelledProductByDiscountCoupon").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductByDiscountCoupon (" +
                "id SERIAL PRIMARY KEY, " +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "coupons_used INT NOT NULL, " +
                "value_discounted FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductByDiscountCoupon (calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, coupons_used, value_discounted, description) " +
                "VALUES (NOW(), 5, 'Beef', 4, 3, 5.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByCustomerID").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductByCustomerID (" +
                "id SERIAL PRIMARY KEY, " +
                "customer_id VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductByCustomerID (customer_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES (1, NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByLocation").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductByLocation (" +
                "id SERIAL PRIMARY KEY, " +
                "location VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductByLocation (location, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES ('Lisbon', NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByLoyaltyCardID").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductByLoyaltyCardID (" +
                "id SERIAL PRIMARY KEY, " +
                "loyalty_card_id VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductByLoyaltyCardID (loyalty_card_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES (1, NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByShopID").execute()
            .flatMap(r -> client.query(
                "CREATE TABLE SelledProductByShopID (" +
                "id SERIAL PRIMARY KEY, " +
                "shop_id VARCHAR(255) NOT NULL," +
                "calculated_at DATETIME NOT NULL, " +
                "number_of_total_purchases INT NOT NULL, " +
                "product VARCHAR(255) NOT NULL, " +
                "number_of_purchases_including_product INT NOT NULL, " +
                "total_revenue FLOAT NOT NULL," +
                "description TEXT)" ).execute())
            .flatMap(r -> client.query(
                "INSERT INTO SelledProductByShopID (shop_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) " +
                "VALUES (1, NOW(), 5, 'Beef', 3, 75.0, 'Analysis of 5 purchases.')").execute())
            .await().indefinitely();

        
    }


    @GET
    @Path("DiscountCoupon")
    public Multi<SelledProductAnalyticsByDiscountCoupon> getAllDiscountCoupon() {
        return SelledProductAnalyticsByDiscountCoupon.findAll(client);
    }

    @GET
    @Path("DiscountCoupon/{id}")
    public Uni<Response> getDiscountCouponByID(Long id) {
        return SelledProductAnalyticsByDiscountCoupon.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("CustomerID")
    public Multi<SelledProductAnalyticsByDiscountCoupon> getAllCustomerID() {
        return SelledProductAnalyticsByDiscountCoupon.findAll(client);
    }

    @GET
    @Path("CustomerID/{id}")
    public Uni<Response> getCustomerIDByID(Long id) {
        return SelledProductAnalyticsByDiscountCoupon.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("Location")
    public Multi<SelledProductAnalyticsByDiscountCoupon> getAllLocation() {
        return SelledProductAnalyticsByDiscountCoupon.findAll(client);
    }

    @GET
    @Path("Location/{id}")
    public Uni<Response> getLocationByID(Long id) {
        return SelledProductAnalyticsByDiscountCoupon.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("LoyaltyCardID")
    public Multi<SelledProductAnalyticsByDiscountCoupon> getAllLoyaltyCardID() {
        return SelledProductAnalyticsByDiscountCoupon.findAll(client);
    }

    @GET
    @Path("LoyaltyCardID/{id}")
    public Uni<Response> getLoyaltyCardIDByID(Long id) {
        return SelledProductAnalyticsByDiscountCoupon.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("ShopID")
    public Multi<SelledProductAnalyticsByDiscountCoupon> getAllShopID() {
        return SelledProductAnalyticsByDiscountCoupon.findAll(client);
    }

    @GET
    @Path("ShopID/{id}")
    public Uni<Response> getShopIDByID(Long id) {
        return SelledProductAnalyticsByDiscountCoupon.findByID(client, id)
                .onItem().transform(analytics -> analytics != null ? Response.ok(analytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }


    @POST
    public Uni<Response> create(List<SelledProduct> selledProductList){
        
        // Calculate all analytics
        List<SelledProductAnalyticsByDiscountCoupon> discountCouponAnalytics = AnalyticsCalculation.calculateDiscountCouponsAnalytics(selledProductList);
        List<SelledProductAnalyticsByCustomerID> customerIDAnalytics = AnalyticsCalculation.calculateCustomerIDAnalytics(selledProductList);
        List<SelledProductAnalyticsByLocation> locationAnalytics = AnalyticsCalculation.calculateLocationAnalytics(selledProductList);
        List<SelledProductAnalyticsByLoyaltyCardID> loyaltyCardIDAnalytics = AnalyticsCalculation.calculateLoyaltyCardIDAnalytics(selledProductList);
        List<SelledProductAnalyticsByShopID> shopIDAnalytics = AnalyticsCalculation.calculateShopIDAnalytics(selledProductList);

        // Save all analytics to the database
        Uni<Boolean> discountCouponSave = SelledProductAnalyticsByDiscountCoupon.saveAll(client, discountCouponAnalytics);
        Uni<Boolean> customerIDSave = SelledProductAnalyticsByCustomerID.saveAll(client, customerIDAnalytics);
        Uni<Boolean> locationSave = SelledProductAnalyticsByLocation.saveAll(client, locationAnalytics);
        Uni<Boolean> loyaltyCardIDSave = SelledProductAnalyticsByLoyaltyCardID.saveAll(client, loyaltyCardIDAnalytics);
        Uni<Boolean> shopIDSave = SelledProductAnalyticsByShopID.saveAll(client, shopIDAnalytics);

        // Group all analytics into a single list for Kafka
        List<SelledProductAnalytics> allAnalytics = List.of(
                discountCouponAnalytics,
                customerIDAnalytics,
                locationAnalytics,
                loyaltyCardIDAnalytics,
                shopIDAnalytics
            ).stream()
            .flatMap(list -> list.stream())
            .collect(Collectors.toList());


        // save to kafka
        sendMultipleToKafka(allAnalytics);

        return Uni.combine().all().unis(discountCouponSave, customerIDSave, locationSave, loyaltyCardIDSave, shopIDSave)
                .combinedWith(results -> {
                    boolean allSaved = true;
                    for (Object result : results) {
                        if (!(result instanceof Boolean) || !(Boolean) result) {
                            allSaved = false;
                            break;
                        }
                    }
                    return allSaved ? Response.ok("All analytics saved successfully").build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to save some analytics").build();
                });
        

    }

    private void sendMultipleToKafka(List<? extends SelledProductAnalytics> analyticsList){
        if (analyticsList == null || analyticsList.isEmpty()) {
            return;
        }

        for (SelledProductAnalytics analytics : analyticsList) {
            String topic = getTopicForAnalytics(analytics);
            DynamicTopicProducer.send(topic, analytics.getProduct(), analytics.toJsonString());
        }
    }

    private static String getTopicForAnalytics(SelledProductAnalytics analytics) {
        if (analytics instanceof SelledProductAnalyticsByDiscountCoupon) {
            return TOPIC_DISCOUNT_COUPON_ANALYTICS;
        } else if (analytics instanceof SelledProductAnalyticsByCustomerID) {
            return TOPIC_CUSTOMER_ID_ANALYTICS;
        } else if (analytics instanceof SelledProductAnalyticsByLocation) {
            return TOPIC_LOCATION_ANALYTICS;
        } else if (analytics instanceof SelledProductAnalyticsByLoyaltyCardID) {
            return TOPIC_LOYALTY_CARD_ID_ANALYTICS;
        } else if (analytics instanceof SelledProductAnalyticsByShopID) {
            return TOPIC_SHOP_ID_ANALYTICS;
        }
        throw new IllegalArgumentException("Unknown analytics type: " + analytics.getClass().getName());
    }



}
