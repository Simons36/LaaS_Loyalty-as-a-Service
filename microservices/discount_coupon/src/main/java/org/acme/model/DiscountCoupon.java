package org.acme.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;

public class DiscountCoupon {

    private Long id;

    @JsonProperty("Expiration")
    private LocalDateTime expiration;

    @JsonProperty("LoyaltyCard_ID")
    private Long loyaltyCard_id;

    @JsonProperty("DiscountType")
    private DiscountType discountType;

    public DiscountCoupon() {
    }

    public DiscountCoupon(Long id, LocalDateTime expiration, Long loyaltyCard_id, DiscountType discountType) {
        this.id = id;
        this.expiration = expiration;
        this.loyaltyCard_id = loyaltyCard_id;
        this.discountType = discountType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public Long getLoyaltyCard_id() {
        return loyaltyCard_id;
    }

    public void setLoyaltyCard_id(Long loyaltyCard_id) {
        this.loyaltyCard_id = loyaltyCard_id;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    private static DiscountCoupon from(Row row) {
        return new DiscountCoupon(
                row.getLong("id"),
                row.getLocalDateTime("expiration"),
                row.getLong("loyaltyCard_id"),
                new DiscountType(
                        row.getString("product"),
                        row.getFloat("discount_percentage")
                )
        );
    }

    public static Multi<DiscountCoupon> findAll(MySQLPool client) {
        return client.preparedQuery("SELECT * FROM DiscountCoupons ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(DiscountCoupon::from);
    }

    public static Uni<DiscountCoupon> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT * FROM DiscountCoupons WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    //save, delete, update

    public Uni<Boolean> save(MySQLPool client, LocalDateTime expiration, Long loyaltyCard_id, DiscountType discountType) {
        return client.preparedQuery("INSERT INTO DiscountCoupons (expiration, loyaltyCard_id, product, discount_percentage) VALUES (?, ?, ?, ?)")
                .execute(Tuple.of(expiration, loyaltyCard_id, discountType.getProduct(), discountType.getDiscountPercentage()))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1 );
    }

    public static Uni<Boolean> delete(MySQLPool client, Long id) {
        return client.preparedQuery("DELETE FROM DiscountCoupons WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> update(MySQLPool client, Long id, LocalDateTime expiration, Long loyaltyCard_id, DiscountType discountType) {
        return client.preparedQuery("UPDATE DiscountCoupons SET expiration = ?, loyaltyCard_id = ?, product = ?, discount_percentage = ? WHERE id = ?")
                .execute(Tuple.of(expiration, loyaltyCard_id, discountType.getProduct(), discountType.getDiscountPercentage(), id))
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
        return "DiscountCoupon [id=" + id + ", expiration=" + expiration + ", loyaltyCard_id=" + loyaltyCard_id
                + ", discountType=" + discountType + "]";
    }
}
