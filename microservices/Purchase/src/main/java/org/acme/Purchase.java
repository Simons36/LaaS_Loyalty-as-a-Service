package org.acme;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.sql.Timestamp;

public class Purchase 
{
    private Long id;
    private java.time.LocalDateTime timestamp;
    private Float price;
    private String product;
    private String supplier;
    private String shop_name;    
    private Long loyaltyCard_id;
    
    @Nullable
    private Long discountCoupon_id;

    public Purchase(Long id, java.time.LocalDateTime timestamp, Float price, String product, String supplier, String shop_name, Long loyaltyCard_id, Long discountCoupon_id) {
        this.id = id;        
        this.timestamp = timestamp;
        this.price = price;
        this.product = product;
        this.supplier = supplier;
        this.shop_name = shop_name;
        this.loyaltyCard_id = loyaltyCard_id;
        this.discountCoupon_id = discountCoupon_id;
    }
    
    public Purchase() {
    }

    
    
    private static Purchase from(Row row) {
        return new Purchase(row.getLong("id"), 
        row.getLocalDateTime("DateTime"),
        row.getFloat("Price") ,
        row.getString("Product"),
        row.getString("Supplier"),
        row.getString("shopname"), 
        row.getLong("loyaltycardid"),
        row.getLong("discountcouponid"));
    }
    
    public static Multi<Purchase> findAll(MySQLPool client) {
        return client.query("SELECT id, DateTime, Price, Product , Supplier, shopname, loyaltycardid, discountcouponid FROM Purchases ORDER BY id ASC").execute()
        .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
        .onItem().transform(Purchase::from);
    }
    
    public static Uni<Purchase> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, DateTime, Price, Product , Supplier, shopname, loyaltycardid, discountcouponid FROM Purchases WHERE id = ?").execute(Tuple.of(id)) 
        .onItem().transform(RowSet::iterator) 
        .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null); 
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public java.time.LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(java.time.LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public Float getPrice() {
        return price;
    }
    public void setPrice(Float price) {
        this.price = price;
    }
    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public String getSupplier() {
        return supplier;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    public String getShop_name() {
        return shop_name;
    }
    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
    public Long getLoyaltyCard_id() {
        return loyaltyCard_id;
    }
    public void setLoyaltyCard_id(Long loyaltyCard_id) {
        this.loyaltyCard_id = loyaltyCard_id;
    }
    public Long getDiscountCoupon_id() {
        return discountCoupon_id;
    }
    public void setDiscountCoupon_id(Long discountCoupon_id) {
        this.discountCoupon_id = discountCoupon_id;
    }
    
    @Override
    public String toString() {
        return "{id=" + id + ", timestamp=" + timestamp.toString() + ", price=" + price + ", product=" + product + ", supplier="
                + supplier + ", shop_name=" + shop_name + ", loyaltyCard_id=" + loyaltyCard_id + ", discountCoupon_id=" + discountCoupon_id + "}";
    }
}