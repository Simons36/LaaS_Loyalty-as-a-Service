package org.acme;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Purchase {
    private Long id;
    private java.time.LocalDateTime timestamp;
    private Float price;
    private String product;
    private String supplier;
    private String shop_name;
    private Long loyaltyCard_id;

    @Nullable
    private Long discountCoupon_id;

    public Purchase(Long id, java.time.LocalDateTime timestamp, Float price, String product, String supplier,
            String shop_name, Long loyaltyCard_id, Long discountCoupon_id) {
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
                row.getFloat("Price"),
                row.getString("Product"),
                row.getString("Supplier"),
                row.getString("shopname"),
                row.getLong("loyaltycardid"),
                row.getLong("discountcouponid"));
    }

    public static Multi<Purchase> findAll(MySQLPool client) {
        return client.query(
                "SELECT id, DateTime, Price, Product , Supplier, shopname, loyaltycardid, discountcouponid FROM Purchases ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Purchase::from);
    }

    public static Uni<Purchase> findById(MySQLPool client, Long id) {
        return client.preparedQuery(
                "SELECT id, DateTime, Price, Product , Supplier, shopname, loyaltycardid, discountcouponid FROM Purchases WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public static Multi<Purchase> findByLoyaltyCardId(io.vertx.mutiny.mysqlclient.MySQLPool client,
            Long loyaltyCardId) {
        return client.preparedQuery(
                "SELECT id, DateTime, Price, Product, Supplier, shopname, loyaltycardid, discountcouponid " +
                        "FROM Purchases WHERE loyaltycardid = ? ORDER BY id ASC")
                .execute(io.vertx.mutiny.sqlclient.Tuple.of(loyaltyCardId))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Purchase::from); // assuming you already have Purchase::from
    }

    public static Multi<Purchase> findByMinDate(MySQLPool client, LocalDateTime minDate) {
        String sql = "SELECT * FROM Purchases WHERE DateTime >= ?";
        String formattedDate = minDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return client.preparedQuery(sql)
                .execute(Tuple.of(formattedDate))
                .onItem()
                .transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem()
                .transform(Purchase::from);
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

    public static Uni<String> findTopProductByLoyaltyCardId(io.vertx.mutiny.mysqlclient.MySQLPool client,
            Long loyaltyCardId) {
        String query = "SELECT product, COUNT(*) AS cnt " +
                "FROM Purchases " +
                "WHERE loyaltycardid = ? " +
                "GROUP BY product " +
                "ORDER BY cnt DESC " +
                "LIMIT 1";

        return client.preparedQuery(query)
                .execute(io.vertx.mutiny.sqlclient.Tuple.of(loyaltyCardId))
                .onItem().transform(pgRowSet -> {
                    if (pgRowSet.rowCount() == 0) {
                        return null;
                    } else {
                        io.vertx.mutiny.sqlclient.Row row = pgRowSet.iterator().next();
                        return row.getString("product");
                    }
                });
    }

    @Override
    public String toString() {
        return "{id=" + id + ", timestamp=" + timestamp.toString() + ", price=" + price + ", product=" + product
                + ", supplier="
                + supplier + ", shop_name=" + shop_name + ", loyaltyCard_id=" + loyaltyCard_id + ", discountCoupon_id="
                + discountCoupon_id + "}";
    }
}