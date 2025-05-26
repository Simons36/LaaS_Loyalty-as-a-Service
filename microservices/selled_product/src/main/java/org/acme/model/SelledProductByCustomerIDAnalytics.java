package org.acme.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Tuple;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

public class SelledProductByCustomerIDAnalytics {
    
    private Long id;

    private String customerID;
    
    private LocalDateTime calculatedAt;

    private int numberOfTotalPurchases;
    
    private String product;

    private int numberOfPurchasesIncludingProduct;

    private Float totalRevenue;

    private String description;

    public SelledProductByCustomerIDAnalytics() {
    }

    public SelledProductByCustomerIDAnalytics(Long id, String customerID, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description) {
        this.id = id;
        this.customerID = customerID;
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

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
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

    private static SelledProductByCustomerIDAnalytics from(Row row){
        SelledProductByCustomerIDAnalytics analytics = new SelledProductByCustomerIDAnalytics();
        analytics.setId(row.getLong("id"));
        analytics.setCustomerID(row.getString("customer_id"));
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

    public static Multi<SelledProductByCustomerIDAnalytics> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM SelledProductsByCustomerID ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductByCustomerIDAnalytics::from);
    }

    public static Multi<SelledProductByCustomerIDAnalytics> findByID(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM SelledProductsByCustomerID WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductByCustomerIDAnalytics::from);
    }

    public Uni<Boolean> save(MySQLPool client, String customerID, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description) {
        Tuple tuple = Tuple.tuple()
                .addString(customerID)
                .addLocalDateTime(calculatedAt)
                .addInteger(numberOfTotalPurchases)
                .addString(product)
                .addInteger(numberOfPurchasesIncludingProduct)
                .addFloat(totalRevenue)
                .addString(description);

        return client.preparedQuery("INSERT INTO SelledProductsByCustomerID (customer_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) VALUES (?, ?, ?, ?, ?, ?, ?)")
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
        return "SelledProductByCustomerIDAnalytics{" +
                "id=" + id +
                ", customerID='" + customerID + '\'' +
                ", calculatedAt=" + calculatedAt +
                ", numberOfTotalPurchases=" + numberOfTotalPurchases +
                ", product='" + product + '\'' +
                ", numberOfPurchasesIncludingProduct=" + numberOfPurchasesIncludingProduct +
                ", totalRevenue=" + totalRevenue +
                ", description='" + description + '\'' +
                '}';
    }



}
