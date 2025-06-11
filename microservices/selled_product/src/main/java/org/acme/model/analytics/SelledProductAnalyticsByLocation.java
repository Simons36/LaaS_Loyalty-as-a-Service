package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByLocation extends SelledProductAnalytics {

    private String location;

    private int numberOfTotalPurchasesInLocation;
    private int numberOfTotalPurchasesIncludingProductInLocation;

    private float totalRevenueOfProduct;
    private float totalRevenueInLocation;
    private float totalRevenueInLocationWithProduct;

    public SelledProductAnalyticsByLocation() {
    }

    public SelledProductAnalyticsByLocation(Long id, String product, LocalDateTime calculatedAt,
            int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct,
            int numberOfTotalPurchasesInLocation, int numberOfTotalPurchasesIncludingProductInLocation,
            float totalRevenue, float totalRevenueOfProduct, float totalRevenueInLocation,
            float totalRevenueInLocationWithProduct,
            String description, String location) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue,
                description);
        this.location = location;
        this.numberOfTotalPurchasesInLocation = numberOfTotalPurchasesInLocation;
        this.numberOfTotalPurchasesIncludingProductInLocation = numberOfTotalPurchasesIncludingProductInLocation;
        this.totalRevenueOfProduct = totalRevenueOfProduct;
        this.totalRevenueInLocation = totalRevenueInLocation;
        this.totalRevenueInLocationWithProduct = totalRevenueInLocationWithProduct;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNumberOfTotalPurchasesInLocation() {
        return numberOfTotalPurchasesInLocation;
    }

    public void setNumberOfTotalPurchasesInLocation(int numberOfTotalPurchasesInLocation) {
        this.numberOfTotalPurchasesInLocation = numberOfTotalPurchasesInLocation;
    }

    public int getNumberOfTotalPurchasesIncludingProductInLocation() {
        return numberOfTotalPurchasesIncludingProductInLocation;
    }

    public void setNumberOfTotalPurchasesIncludingProductInLocation(
            int numberOfTotalPurchasesIncludingProductInLocation) {
        this.numberOfTotalPurchasesIncludingProductInLocation = numberOfTotalPurchasesIncludingProductInLocation;
    }

    public float getTotalRevenueOfProduct() {
        return totalRevenueOfProduct;
    }

    public void setTotalRevenueOfProduct(float totalRevenueOfProduct) {
        this.totalRevenueOfProduct = totalRevenueOfProduct;
    }

    public float getTotalRevenueInLocation() {
        return totalRevenueInLocation;
    }

    public void setTotalRevenueInLocation(float totalRevenueInLocation) {
        this.totalRevenueInLocation = totalRevenueInLocation;
    }

    public float getTotalRevenueInLocationWithProduct() {
        return totalRevenueInLocationWithProduct;
    }

    public void setTotalRevenueInLocationWithProduct(float totalRevenueInLocationWithProduct) {
        this.totalRevenueInLocationWithProduct = totalRevenueInLocationWithProduct;
    }

    private static SelledProductAnalyticsByLocation from(Row row) {
        return new SelledProductAnalyticsByLocation(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_total_purchases_including_product"),
                row.getInteger("number_of_total_purchases_in_location"),
                row.getInteger("number_of_total_purchases_including_product_in_location"),
                row.getFloat("total_revenue"),
                row.getFloat("total_revenue_of_product"),
                row.getFloat("total_revenue_in_location"),
                row.getFloat("total_revenue_in_location_with_product"),
                row.getString("description"),
                row.getString("location"));
    }

    public static Multi<SelledProductAnalyticsByLocation> findAll(MySQLPool client) {
        return client.preparedQuery("SELECT * FROM SelledProductByLocation ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLocation::from);
    }

    public static Multi<SelledProductAnalyticsByLocation> findByID(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT * FROM SelledProductByLocation WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLocation::from);
    }

    public Uni<Boolean> save(MySQLPool client) {
        Tuple tuple = Tuple.tuple()
                .addString(this.getProduct())
                .addString(this.getLocation())
                .addLocalDateTime(this.getCalculatedAt())
                .addInteger(this.getNumberOfTotalPurchases())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(this.getNumberOfTotalPurchasesInLocation())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProductInLocation())
                .addFloat(this.getTotalRevenue())
                .addFloat(this.getTotalRevenueOfProduct())
                .addFloat(this.getTotalRevenueInLocation())
                .addFloat(this.getTotalRevenueInLocationWithProduct())
                .addString(this.getDescription());

        return client.preparedQuery("INSERT INTO SelledProductByLocation (" +
                "product, location, calculated_at, " +
                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                "number_of_total_purchases_in_location, number_of_total_purchases_including_product_in_location, " +
                "total_revenue, total_revenue_of_product, total_revenue_in_location, total_revenue_in_location_with_product, "
                +
                "description" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByLocation> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getProduct())
                .addString(analytics.getLocation())
                .addLocalDateTime(analytics.getCalculatedAt())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(analytics.getNumberOfTotalPurchasesInLocation())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProductInLocation())
                .addFloat(analytics.getTotalRevenue())
                .addFloat(analytics.getTotalRevenueOfProduct())
                .addFloat(analytics.getTotalRevenueInLocation())
                .addFloat(analytics.getTotalRevenueInLocationWithProduct())
                .addString(analytics.getDescription())).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByLocation (" +
                        "product, location, calculated_at, " +
                        "number_of_total_purchases, number_of_total_purchases_including_product, " +
                        "number_of_total_purchases_in_location, number_of_total_purchases_including_product_in_location, "
                        +
                        "total_revenue, total_revenue_of_product, total_revenue_in_location, total_revenue_in_location_with_product, "
                        +
                        "description" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .executeBatch(tuples)
                .onItem().transform(pgRowSet -> {
                    System.out.println("[DEBUG] saveAll(): batch executed for " + tuples.size() + " tuples.");
                    return true; // assume batch is OK if no exception was thrown
                });

    }

    @Override
    public String toString() {
        return "SelledProductAnalyticsByLocation{" +
                "id=" + getId() +
                ", product='" + getProduct() + '\'' +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfTotalPurchasesIncludingProduct() +
                ", numberOfTotalPurchasesInLocation=" + numberOfTotalPurchasesInLocation +
                ", numberOfTotalPurchasesIncludingProductInLocation=" + numberOfTotalPurchasesIncludingProductInLocation
                +
                ", totalRevenue=" + getTotalRevenue() +
                ", totalRevenueOfProduct=" + totalRevenueOfProduct +
                ", totalRevenueInLocation=" + totalRevenueInLocation +
                ", totalRevenueInLocationWithProduct=" + totalRevenueInLocationWithProduct +
                ", description='" + getDescription() + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
