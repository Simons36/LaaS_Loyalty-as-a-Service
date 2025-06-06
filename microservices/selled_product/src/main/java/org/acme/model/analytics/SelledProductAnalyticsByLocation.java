package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByLocation extends SelledProductAnalytics {

    private String location;

    public SelledProductAnalyticsByLocation() {
    }

    public SelledProductAnalyticsByLocation(Long id, String product, LocalDateTime calculatedAt, int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description, String location) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue, description);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private static SelledProductAnalyticsByLocation from(Row row) {
        return new SelledProductAnalyticsByLocation(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_purchases_including_product"),
                row.getFloat("total_revenue"),
                row.getString("description"),
                row.getString("location")
        );
    }

    public static Multi<SelledProductAnalyticsByLocation> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM SelledProductByLocation ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLocation::from);
    }

    public static Multi<SelledProductAnalyticsByLocation> findByID(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM SelledProductByLocation WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLocation::from);
    }
    
    public Uni<Boolean> save(MySQLPool client, String location, LocalDateTime calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description) {
        Tuple tuple = Tuple.tuple()
                .addString(location)
                .addLocalDateTime(calculatedAt)
                .addInteger(numberOfTotalPurchases)
                .addString(product)
                .addInteger(numberOfPurchasesIncludingProduct)
                .addFloat(totalRevenue)
                .addString(description);

        return client.preparedQuery("INSERT INTO SelledProductByLocation (location, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByLocation> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getLocation())
                .addLocalDateTime(analytics.getCalculatedAt())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addString(analytics.getProduct())
                .addInteger(analytics.getNumberOfPurchasesIncludingProduct())
                .addFloat(analytics.getTotalRevenue())
                .addString(analytics.getDescription())
        ).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByLocation (location, calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, total_revenue, description) VALUES (?, ?, ?, ?, ?, ?, ?)"
            )
            .executeBatch(tuples)
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == analyticsList.size());
    }


    @Override
    public String toString() {
        return "SelledProductAnalyticsByLocation{" +
                "id=" + getId() +
                ", product='" + getProduct() + '\'' +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfPurchasesIncludingProduct() +
                ", totalRevenue=" + getTotalRevenue() +
                ", description='" + getDescription() + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
