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
    private void initdb() {
        client.query("DROP TABLE IF EXISTS SelledProductByDiscountCoupon")
                .execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE SelledProductByDiscountCoupon (" +
                                "id SERIAL PRIMARY KEY, " +
                                "calculated_at DATETIME NOT NULL, " +
                                "product VARCHAR(255) NOT NULL, " +
                                "number_of_total_purchases INT NOT NULL, " +
                                "number_of_total_purchases_including_product INT NOT NULL, " +
                                "total_coupons_used INT NOT NULL, " +
                                "coupons_used_on_product INT NOT NULL, " +
                                "total_value_discounted FLOAT NOT NULL, " +
                                "value_discounted_on_product FLOAT NOT NULL, " +
                                "description TEXT" +
                                ")")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO SelledProductByDiscountCoupon (" +
                                "calculated_at, product, " +
                                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                                "total_coupons_used, coupons_used_on_product, " +
                                "total_value_discounted, value_discounted_on_product, " +
                                "description" +
                                ") VALUES (" +
                                "NOW(), 'Beef', " +
                                "5, 4, " +
                                "6, 3, " +
                                "10.0, 5.0, " +
                                "'Analysis of 5 purchases.'" +
                                ")")
                        .execute())
                .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByCustomerID")
                .execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE SelledProductByCustomerID (" +
                                "id SERIAL PRIMARY KEY, " +
                                "product VARCHAR(255) NOT NULL, " +
                                "customer_id VARCHAR(255) NOT NULL, " +
                                "calculated_at DATETIME NOT NULL, " +
                                "number_of_total_purchases INT NOT NULL, " +
                                "number_of_total_purchases_including_product INT NOT NULL, " +
                                "number_of_total_purchases_of_customer INT NOT NULL, " +
                                "number_of_total_purchases_including_product_of_customer INT NOT NULL, " +
                                "total_revenue FLOAT NOT NULL, " +
                                "total_revenue_of_product FLOAT NOT NULL, " +
                                "total_revenue_of_customer FLOAT NOT NULL, " +
                                "total_revenue_of_customer_with_product FLOAT NOT NULL, " +
                                "description TEXT" +
                                ")")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO SelledProductByCustomerID (" +
                                "product, customer_id, calculated_at, " +
                                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                                "number_of_total_purchases_of_customer, number_of_total_purchases_including_product_of_customer, "
                                +
                                "total_revenue, total_revenue_of_product, total_revenue_of_customer, total_revenue_of_customer_with_product, description"
                                +
                                ") VALUES (" +
                                "'Beef', 'customer1', NOW(), " +
                                "5, 3, 2, 1, " +
                                "100.0, 60.0, 40.0, 20.0, " +
                                "'Sample analytics for customer1'" +
                                ")")
                        .execute())
                .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByLocation")
                .execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE SelledProductByLocation (" +
                                "id SERIAL PRIMARY KEY, " +
                                "product VARCHAR(255) NOT NULL, " +
                                "location VARCHAR(255) NOT NULL, " +
                                "calculated_at DATETIME NOT NULL, " +
                                "number_of_total_purchases INT NOT NULL, " +
                                "number_of_total_purchases_including_product INT NOT NULL, " +
                                "number_of_total_purchases_in_location INT NOT NULL, " +
                                "number_of_total_purchases_including_product_in_location INT NOT NULL, " +
                                "total_revenue FLOAT NOT NULL, " +
                                "total_revenue_of_product FLOAT NOT NULL, " +
                                "total_revenue_in_location FLOAT NOT NULL, " +
                                "total_revenue_in_location_with_product FLOAT NOT NULL, " +
                                "description TEXT" +
                                ")")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO SelledProductByLocation (" +
                                "product, location, calculated_at, " +
                                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                                "number_of_total_purchases_in_location, number_of_total_purchases_including_product_in_location, "
                                +
                                "total_revenue, total_revenue_of_product, total_revenue_in_location, total_revenue_in_location_with_product, "
                                +
                                "description" +
                                ") VALUES (" +
                                "'Beef', 'Lisbon', NOW(), " +
                                "5, 3, " +
                                "4, 2, " +
                                "75.0, 60.0, 100.0, 45.0, " +
                                "'Analysis of 5 purchases in Lisbon.'" +
                                ")")
                        .execute())
                .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByLoyaltyCardID")
                .execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE SelledProductByLoyaltyCardID (" +
                                "id SERIAL PRIMARY KEY, " +
                                "product VARCHAR(255) NOT NULL, " +
                                "loyalty_card_id VARCHAR(255) NOT NULL, " +
                                "calculated_at DATETIME NOT NULL, " +
                                "number_of_total_purchases INT NOT NULL, " +
                                "number_of_total_purchases_including_product INT NOT NULL, " +
                                "number_of_total_purchases_with_loyalty_card INT NOT NULL, " +
                                "number_of_total_purchases_including_product_with_loyalty_card INT NOT NULL, " +
                                "total_revenue FLOAT NOT NULL, " +
                                "total_revenue_of_product FLOAT NOT NULL, " +
                                "total_revenue_with_loyalty_card FLOAT NOT NULL, " +
                                "total_revenue_with_loyalty_card_with_product FLOAT NOT NULL, " +
                                "description TEXT" +
                                ")")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO SelledProductByLoyaltyCardID (" +
                                "product, loyalty_card_id, calculated_at, " +
                                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                                "number_of_total_purchases_with_loyalty_card, number_of_total_purchases_including_product_with_loyalty_card, "
                                +
                                "total_revenue, total_revenue_of_product, total_revenue_with_loyalty_card, total_revenue_with_loyalty_card_with_product, "
                                +
                                "description" +
                                ") VALUES (" +
                                "'Beef', '123456789', NOW(), " +
                                "5, 3, " +
                                "4, 2, " +
                                "75.0, 60.0, 90.0, 45.0, " +
                                "'Analysis of 5 purchases with loyalty card.'" +
                                ")")
                        .execute())
                .await().indefinitely();

        client.query("DROP TABLE IF EXISTS SelledProductByShopID")
                .execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE SelledProductByShopID (" +
                                "id SERIAL PRIMARY KEY, " +
                                "product VARCHAR(255) NOT NULL, " +
                                "shop_id VARCHAR(255) NOT NULL, " +
                                "calculated_at DATETIME NOT NULL, " +
                                "number_of_total_purchases INT NOT NULL, " +
                                "number_of_total_purchases_including_product INT NOT NULL, " +
                                "number_of_total_purchases_in_shop INT NOT NULL, " +
                                "number_of_total_purchases_including_product_in_shop INT NOT NULL, " +
                                "total_revenue FLOAT NOT NULL, " +
                                "total_revenue_of_product FLOAT NOT NULL, " +
                                "total_revenue_in_shop FLOAT NOT NULL, " +
                                "total_revenue_in_shop_with_product FLOAT NOT NULL, " +
                                "description TEXT" +
                                ")")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO SelledProductByShopID (" +
                                "product, shop_id, calculated_at, " +
                                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                                "number_of_total_purchases_in_shop, number_of_total_purchases_including_product_in_shop, "
                                +
                                "total_revenue, total_revenue_of_product, total_revenue_in_shop, total_revenue_in_shop_with_product, "
                                +
                                "description" +
                                ") VALUES (" +
                                "'Beef', 'shop123', NOW(), " +
                                "5, 3, " +
                                "4, 2, " +
                                "75.0, 60.0, 100.0, 45.0, " +
                                "'Analysis of 5 purchases in shop.'" +
                                ")")
                        .execute())
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
                .onItem()
                .transform(analytics -> analytics != null ? Response.ok(analytics)
                        : Response.status(Response.Status.NOT_FOUND))
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
                .onItem()
                .transform(analytics -> analytics != null ? Response.ok(analytics)
                        : Response.status(Response.Status.NOT_FOUND))
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
                .onItem()
                .transform(analytics -> analytics != null ? Response.ok(analytics)
                        : Response.status(Response.Status.NOT_FOUND))
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
                .onItem()
                .transform(analytics -> analytics != null ? Response.ok(analytics)
                        : Response.status(Response.Status.NOT_FOUND))
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
                .onItem()
                .transform(analytics -> analytics != null ? Response.ok(analytics)
                        : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(List<SelledProduct> selledProductList) {

        // Calculate all analytics
        List<SelledProductAnalyticsByDiscountCoupon> discountCouponAnalytics = AnalyticsCalculation
                .calculateDiscountCouponsAnalytics(selledProductList);
        List<SelledProductAnalyticsByCustomerID> customerIDAnalytics = AnalyticsCalculation
                .calculateCustomerIDAnalytics(selledProductList);
        List<SelledProductAnalyticsByLocation> locationAnalytics = AnalyticsCalculation
                .calculateLocationAnalytics(selledProductList);
        List<SelledProductAnalyticsByLoyaltyCardID> loyaltyCardIDAnalytics = AnalyticsCalculation
                .calculateLoyaltyCardIDAnalytics(selledProductList);
        List<SelledProductAnalyticsByShopID> shopIDAnalytics = AnalyticsCalculation
                .calculateShopIDAnalytics(selledProductList);

        // Save all analytics to the database
        Uni<Boolean> discountCouponSave = SelledProductAnalyticsByDiscountCoupon.saveAll(client,
                discountCouponAnalytics);
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
                shopIDAnalytics).stream()
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());

        // save to kafka
        sendMultipleToKafka(allAnalytics);

        return Uni.combine().all().unis(discountCouponSave, customerIDSave, locationSave, loyaltyCardIDSave, shopIDSave)
                .combinedWith(results -> {

                    boolean discountSaved = (Boolean) results.get(0);
                    boolean customerSaved = (Boolean) results.get(1);
                    boolean locationSaved = (Boolean) results.get(2);
                    boolean loyaltySaved = (Boolean) results.get(3);
                    boolean shopSaved = (Boolean) results.get(4);

                    if (!discountSaved) {
                        System.err.println("⚠️ Failed to save DiscountCoupon analytics.");
                    }
                    if (!customerSaved) {
                        System.err.println("⚠️ Failed to save CustomerID analytics.");
                    }
                    if (!locationSaved) {
                        System.err.println("⚠️ Failed to save Location analytics.");
                    }
                    if (!loyaltySaved) {
                        System.err.println("⚠️ Failed to save LoyaltyCardID analytics.");
                    }
                    if (!shopSaved) {
                        System.err.println("⚠️ Failed to save ShopID analytics.");
                    }

                    boolean allSaved = discountSaved && customerSaved && locationSaved && loyaltySaved && shopSaved;

                    return allSaved
                            ? Response.ok("All analytics saved successfully").build()
                            : Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity("Failed to save some analytics").build();
                });

    }

    private void sendMultipleToKafka(List<? extends SelledProductAnalytics> analyticsList) {
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
