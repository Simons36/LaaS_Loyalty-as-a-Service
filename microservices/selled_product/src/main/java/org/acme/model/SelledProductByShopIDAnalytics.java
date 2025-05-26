package org.acme.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductByShopIDAnalytics {
    private Long id;

    private String shopID;
    
    private LocalDateTime calculatedAt;

    private int numberOfTotalPurchases;
    
    private String product;

    private int numberOfPurchasesIncludingProduct;

    private Float totalRevenue;

    private String description;

    public SelledProductByShopIDAnalytics() {
    }

    public SelledProductByShopIDAnalytics(Long id, String shopID, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description) {
        this.id = id;
        this.shopID = shopID;
        this.calculatedAt = calculatedAt;
        this.numberOfTotalPurchases = numberOfTotalPurchases;
        this.product = product;
        this.numberOfPurchasesIncludingProduct = numberOfPurchasesIncludingProduct;
        this.totalRevenue = totalRevenue;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public int getNumberOfTotalPurchases() {
        return numberOfTotalPurchases;
    }

    public void setNumberOfTotalPurchases(int numberOfTotalPurchases) {
        this.numberOfTotalPurchases = numberOfTotalPurchases;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getNumberOfPurchasesIncludingProduct() {
        return numberOfPurchasesIncludingProduct;
    }

    public void setNumberOfPurchasesIncludingProduct(int numberOfPurchasesIncludingProduct) {
        this.numberOfPurchasesIncludingProduct = numberOfPurchasesIncludingProduct;
    }

    public Float getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getDescription() {
        return description;
    }

    private static SelledProductByShopIDAnalytics from(Row row){
        SelledProductByShopIDAnalytics analytics = new SelledProductByShopIDAnalytics();
        analytics.setId(row.getLong("id"));
        analytics.setShopID(row.getString("shop_id"));
        analytics.setCalculatedAt(row.getLocalDateTime("calculated_at"));
        analytics.setNumberOfTotalPurchases(row.getInteger("number_of_total_purchases"));
        analytics.setProduct(row.getString("product"));
        analytics.setNumberOfPurchasesIncludingProduct(row.getInteger("number_of_purchases_including_product"));
        analytics.setTotalRevenue(row.getFloat("total_revenue"));
        analytics.setDescription(row.getString("description"));
        return analytics;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Multi<SelledProductByShopIDAnalytics> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM SelledProductsByShopID ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductByShopIDAnalytics::from);
    }

    public static Multi<SelledProductByShopIDAnalytics> findByID(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM SelledProductsByShopID WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductByShopIDAnalytics::from);
    }

    public Uni<Boolean> save(MySQLPool client, String shopID, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description) {
        Tuple tuple = Tuple.tuple()
                .addString(shopID)
                .addLocalDateTime(calculatedAt)
                .addInteger(numberOfTotalPurchases)
                .addString(product)
                .addInteger(numberOfPurchasesIncludingProduct)
                .addFloat(totalRevenue)
                .addString(description);

        return client.preparedQuery("INSERT INTO SelledProductsByShopID (shop_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public String toJsonString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DiscountCoupon to JSON", e);
        }
    }


    @Override
    public String toString() {
        return "SelledProductByShopIDAnalytics{" +
                "id=" + id +
                ", shopID='" + shopID + '\'' +
                ", calculatedAt=" + calculatedAt +
                ", numberOfTotalPurchases=" + numberOfTotalPurchases +
                ", product='" + product + '\'' +
                ", numberOfPurchasesIncludingProduct=" + numberOfPurchasesIncludingProduct +
                ", totalRevenue=" + totalRevenue +
                ", description='" + description + '\'' +
                '}';
    }
}
