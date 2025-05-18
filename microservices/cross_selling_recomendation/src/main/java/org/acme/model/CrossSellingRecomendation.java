package org.acme.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class CrossSellingRecomendation {

    // id, expiration, loyaltyCard_id, product, originShop, destinationShop

    private Long id;

    @JsonProperty("Expiration")
    private LocalDateTime expiration;

    @JsonProperty("LoyaltyCard_ID")
    private Long loyaltyCard_id;

    @JsonProperty("Product")
    private String product;

    @JsonProperty("OriginShop")
    private String originShop;

    @JsonProperty("DestinationShop")
    private String destinationShop;

    public CrossSellingRecomendation() {
    }

    public CrossSellingRecomendation(Long id, LocalDateTime expiration, Long loyaltyCard_id, String product, String originShop, String destinationShop) {
        this.id = id;
        this.expiration = expiration;
        this.loyaltyCard_id = loyaltyCard_id;
        this.product = product;
        this.originShop = originShop;
        this.destinationShop = destinationShop;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getOriginShop() {
        return originShop;
    }

    public void setOriginShop(String originShop) {
        this.originShop = originShop;
    }

    public String getDestinationShop() {
        return destinationShop;
    }

    public void setDestinationShop(String destinationShop) {
        this.destinationShop = destinationShop;
    }

    private static CrossSellingRecomendation from(Row row){
        return new CrossSellingRecomendation(
            row.getLong("id"),
            row.getLocalDateTime("expiration"),
            row.getLong("loyaltyCard_id"),
            row.getString("product"),
            row.getString("origin_shop"),
            row.getString("destination_shop")
        );
    }

    
    public static Multi<CrossSellingRecomendation> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM CrossSellingRecomendations ORDER BY id ASC")
        .execute()
        .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
        .onItem().transform(CrossSellingRecomendation::from);
    }

    public static Uni<CrossSellingRecomendation> findById(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM CrossSellingRecomendations ORDER BY id ASC")
        .execute(Tuple.of(id))
        .onItem().transform(RowSet::iterator)
        .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Boolean> save(MySQLPool client, LocalDateTime expiration, Long loyaltyCard_id, String product, String originShop, String destinationShop){
        return client.preparedQuery("INSERT INTO CrossSellingRecomendations (expiration, loyaltyCard_id, product, origin_shop, destination_shop) VALUES (?, ?, ?, ?, ?)")
        .execute(Tuple.of(expiration, loyaltyCard_id, product, originShop, destinationShop))
        .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(MySQLPool client, Long id){
        return client.preparedQuery("DELETE FROM CrossSellingRecomendations WHERE id = ?")
        .execute(Tuple.of(id))
        .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> update(MySQLPool client, Long id, LocalDateTime expiration, Long loyaltyCard_id, String product, String originShop, String destinationShop){
        return client.preparedQuery("UPDATE CrossSellingRecomendations SET expiration = ?, loyaltyCard_id = ?, product = ?, origin_shop = ?, destination_shop = ? WHERE id = ?")
        .execute(Tuple.of(expiration, loyaltyCard_id, product, originShop, destinationShop, id))
        .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    
    public String toJsonString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Re to JSON", e);
        }
    }

    @Override
    public String toString() {
        return "CrossSellingRecomendation{" +
        "id=" + id +
        ", expiration='" + expiration + '\'' +
        ", loyaltyCard_id=" + loyaltyCard_id +
        ", product='" + product + '\'' +
        ", originShop='" + originShop + '\'' +
        ", destinationShop='" + destinationShop + '\'' +
        '}';
    }
    
    
}
