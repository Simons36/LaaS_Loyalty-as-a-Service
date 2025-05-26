package org.acme.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductByDiscountCouponAnalytics {
    
    private Long id;

    private LocalDateTime calculatedAt;

    private int numberOfTotalPurchases;

    private String product;

    private int numberOfPurchasesIncludingProduct;

    private int couponsUsed;

    private Float discountedValue;

    private String description;

    public SelledProductByDiscountCouponAnalytics() {
    }

    public SelledProductByDiscountCouponAnalytics(Long id, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, int couponsUsed, Float discountedValue, String description) {
        this.id = id;
        this.calculatedAt = calculatedAt;
        this.numberOfTotalPurchases = numberOfTotalPurchases;
        this.product = product;
        this.numberOfPurchasesIncludingProduct = numberOfPurchasesIncludingProduct;
        this.couponsUsed = couponsUsed;
        this.discountedValue = discountedValue;
        this.description = description;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getCouponsUsed() {
        return couponsUsed;
    }

    public void setCouponsUsed(int couponsUsed) {
        this.couponsUsed = couponsUsed;
    }

    public Float getDiscountedValue() {
        return discountedValue;
    }

    public void setDiscountedValue(Float discountedValue) {
        this.discountedValue = discountedValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    private static SelledProductByDiscountCouponAnalytics from(Row row){
        SelledProductByDiscountCouponAnalytics analytics = new SelledProductByDiscountCouponAnalytics();
        analytics.setId(row.getLong("id"));
        analytics.setCalculatedAt(row.getLocalDateTime("calculated_at"));
        analytics.setNumberOfTotalPurchases(row.getInteger("number_of_total_purchases"));
        analytics.setProduct(row.getString("product"));
        analytics.setNumberOfPurchasesIncludingProduct(row.getInteger("number_of_purchases_including_product"));
        analytics.setCouponsUsed(row.getInteger("coupons_used"));
        analytics.setDiscountedValue(row.getFloat("discounted_value"));
        analytics.setDescription(row.getString("description"));
        return analytics;
    }

    public static Multi<SelledProductByDiscountCouponAnalytics> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM SelledProductsByDiscountCoupon ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(SelledProductByDiscountCouponAnalytics::from);
    }

    public static Uni<SelledProductByDiscountCouponAnalytics> findByID(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM SelledProductsByDiscountCoupon WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Boolean> save(MySQLPool client, String calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, int couponsUsed, Float discountedValue, String description) {
        Tuple tuple = Tuple.tuple()
                .addString(calculatedAt)
                .addInteger(numberOfTotalPurchases)
                .addString(product)
                .addInteger(numberOfPurchasesIncludingProduct)
                .addInteger(couponsUsed)
                .addFloat(discountedValue)
                .addString(description);

        return client.preparedQuery("INSERT INTO SelledProductsByDiscountCoupon (calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, coupons_used, discounted_value, description) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    @Override
    public String toString() {
        return "SelledProductByDiscountCoupon{" +
                "id=" + id +
                ", calculatedAt='" + calculatedAt + '\'' +
                ", numberOfTotalPurchases=" + numberOfTotalPurchases +
                ", product='" + product + '\'' +
                ", numberOfPurchasesIncludingProduct=" + numberOfPurchasesIncludingProduct +
                ", couponsUsed=" + couponsUsed +
                ", discountedValue=" + discountedValue +
                ", description='" + description + '\'' +
                '}';
    }
    
}
