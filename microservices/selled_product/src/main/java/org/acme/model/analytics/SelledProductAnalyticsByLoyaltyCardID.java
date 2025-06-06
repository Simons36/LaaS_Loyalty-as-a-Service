package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByLoyaltyCardID extends SelledProductAnalytics{
    
    private String loyaltyCardID;

    public SelledProductAnalyticsByLoyaltyCardID() {
    }

    public SelledProductAnalyticsByLoyaltyCardID(Long id, String product, LocalDateTime calculatedAt, int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description, String loyaltyCardID) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue, description);
        this.loyaltyCardID = loyaltyCardID;
    }

    public String getLoyaltyCardID() {
        return loyaltyCardID;
    }

    public void setLoyaltyCardID(String loyaltyCardID) {
        this.loyaltyCardID = loyaltyCardID;
    }

    private static SelledProductAnalyticsByLoyaltyCardID from(Row row) {
        return new SelledProductAnalyticsByLoyaltyCardID(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_purchases_including_product"),
                row.getFloat("total_revenue"),
                row.getString("description"),
                row.getString("loyalty_card_id")
        );
    }

    public static Multi<SelledProductAnalyticsByLoyaltyCardID> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM SelledProductByLoyaltyCardID ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLoyaltyCardID::from);
    }

    public static Multi<SelledProductAnalyticsByLoyaltyCardID> findByID(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM SelledProductByLoyaltyCardID WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLoyaltyCardID::from);
    }

    public Uni<Boolean> save(MySQLPool client, String loyaltyCardID, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description) {
        Tuple tuple = Tuple.tuple()
                .addString(loyaltyCardID)
                .addLocalDateTime(calculatedAt)
                .addInteger(numberOfTotalPurchases)
                .addString(product)
                .addInteger(numberOfPurchasesIncludingProduct)
                .addFloat(totalRevenue)
                .addString(description);

        return client.preparedQuery("INSERT INTO SelledProductByLoyaltyCardID (loyaltyCard_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByLoyaltyCardID> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getLoyaltyCardID())
                .addLocalDateTime(analytics.getCalculatedAt())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addString(analytics.getProduct())
                .addInteger(analytics.getNumberOfPurchasesIncludingProduct())
                .addFloat(analytics.getTotalRevenue())
                .addString(analytics.getDescription())
        ).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByLoyaltyCardID (loyaltyCard_id, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) VALUES (?, ?, ?, ?, ?, ?, ?)"
            )
            .executeBatch(tuples)
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == analyticsList.size());
    }

    @Override
    public String toString() {
        return "SelledProductAnalyticsByLoyaltyCardID{" +
                "id=" + getId() +
                ", product='" + getProduct() + '\'' +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfPurchasesIncludingProduct() +
                ", totalRevenue=" + getTotalRevenue() +
                ", description='" + getDescription() + '\'' +
                ", loyaltyCardID='" + loyaltyCardID + '\'' +
                '}';
    }

}
