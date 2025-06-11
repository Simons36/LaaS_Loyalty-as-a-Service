package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByCustomerID extends SelledProductAnalytics {

    private String customerID;

    private int numberOfTotalPurchasesOfCustomer;
    private int numberOfTotalPurchasesIncludingProductOfCustomer;

    private float totalRevenueOfProduct;
    private float totalRevenueOfCustomer;
    private float totalRevenueOfCustomerWithProduct;

    public SelledProductAnalyticsByCustomerID() {
    }

    public SelledProductAnalyticsByCustomerID(Long id, String product, LocalDateTime calculatedAt,
            int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct,
            int numberOfTotalPurchasesOfCustomer, int numberOfTotalPurchasesIncludingProductOfCustomer,
            float totalRevenue, float totalRevenueOfProduct, float totalRevenueOfCustomer,
            float totalRevenueOfCustomerWithProduct,
            String description, String customerID) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue,
                description);
        this.customerID = customerID;
        this.numberOfTotalPurchasesOfCustomer = numberOfTotalPurchasesOfCustomer;
        this.numberOfTotalPurchasesIncludingProductOfCustomer = numberOfTotalPurchasesIncludingProductOfCustomer;
        this.totalRevenueOfProduct = totalRevenueOfProduct;
        this.totalRevenueOfCustomer = totalRevenueOfCustomer;
        this.totalRevenueOfCustomerWithProduct = totalRevenueOfCustomerWithProduct;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public int getNumberOfTotalPurchasesOfCustomer() {
        return numberOfTotalPurchasesOfCustomer;
    }

    public void setNumberOfTotalPurchasesOfCustomer(int numberOfTotalPurchasesOfCustomer) {
        this.numberOfTotalPurchasesOfCustomer = numberOfTotalPurchasesOfCustomer;
    }

    public int getNumberOfTotalPurchasesIncludingProductOfCustomer() {
        return numberOfTotalPurchasesIncludingProductOfCustomer;
    }

    public void setNumberOfTotalPurchasesIncludingProductOfCustomer(
            int numberOfTotalPurchasesIncludingProductOfCustomer) {
        this.numberOfTotalPurchasesIncludingProductOfCustomer = numberOfTotalPurchasesIncludingProductOfCustomer;
    }

    public float getTotalRevenueOfProduct() {
        return totalRevenueOfProduct;
    }

    public void setTotalRevenueOfProduct(float totalRevenueOfProduct) {
        this.totalRevenueOfProduct = totalRevenueOfProduct;
    }

    public float getTotalRevenueOfCustomer() {
        return totalRevenueOfCustomer;
    }

    public void setTotalRevenueOfCustomer(float totalRevenueOfCustomer) {
        this.totalRevenueOfCustomer = totalRevenueOfCustomer;
    }

    public float getTotalRevenueOfCustomerWithProduct() {
        return totalRevenueOfCustomerWithProduct;
    }

    public void setTotalRevenueOfCustomerWithProduct(float totalRevenueOfCustomerWithProduct) {
        this.totalRevenueOfCustomerWithProduct = totalRevenueOfCustomerWithProduct;
    }

    private static SelledProductAnalyticsByCustomerID from(Row row) {
        return new SelledProductAnalyticsByCustomerID(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_total_purchases_including_product"),
                row.getInteger("number_of_total_purchases_of_customer"),
                row.getInteger("number_of_total_purchases_including_product_of_customer"),
                row.getFloat("total_revenue"),
                row.getFloat("total_revenue_of_product"),
                row.getFloat("total_revenue_of_customer"),
                row.getFloat("total_revenue_of_customer_with_product"),
                row.getString("description"),
                row.getString("customer_id"));
    }

    public static Multi<SelledProductAnalyticsByCustomerID> findAll(MySQLPool client) {
        return client.preparedQuery("SELECT * FROM SelledProductByCustomerID ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByCustomerID::from);
    }

    public static Multi<SelledProductAnalyticsByCustomerID> findByID(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT * FROM SelledProductByCustomerID WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByCustomerID::from);
    }

    public Uni<Boolean> save(MySQLPool client) {
        Tuple tuple = Tuple.tuple()
                .addString(this.getProduct()) // product
                .addString(this.getCustomerID()) // customer_id
                .addLocalDateTime(this.getCalculatedAt()) // calculated_at
                .addInteger(this.getNumberOfTotalPurchases()) // number_of_total_purchases
                .addInteger(this.getNumberOfTotalPurchasesIncludingProduct()) // number_of_total_purchases_including_product
                .addInteger(this.getNumberOfTotalPurchasesOfCustomer()) // number_of_total_purchases_of_customer
                .addInteger(this.getNumberOfTotalPurchasesIncludingProductOfCustomer()) // number_of_total_purchases_including_product_of_customer
                .addFloat(this.getTotalRevenue()) // total_revenue
                .addFloat(this.getTotalRevenueOfProduct()) // total_revenue_of_product
                .addFloat(this.getTotalRevenueOfCustomer()) // total_revenue_of_customer
                .addFloat(this.getTotalRevenueOfCustomerWithProduct()) // total_revenue_of_customer_with_product
                .addString(this.getDescription()); // description

        return client.preparedQuery("INSERT INTO SelledProductByCustomerID (" +
                "product, customer_id, calculated_at, " +
                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                "number_of_total_purchases_of_customer, number_of_total_purchases_including_product_of_customer, " +
                "total_revenue, total_revenue_of_product, total_revenue_of_customer, total_revenue_of_customer_with_product, description"
                +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByCustomerID> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getProduct()) // product
                .addString(analytics.getCustomerID()) // customer_id
                .addLocalDateTime(analytics.getCalculatedAt()) // calculated_at
                .addInteger(analytics.getNumberOfTotalPurchases()) // number_of_total_purchases
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProduct()) // number_of_total_purchases_including_product
                .addInteger(analytics.getNumberOfTotalPurchasesOfCustomer()) // number_of_total_purchases_of_customer
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProductOfCustomer()) // number_of_total_purchases_including_product_of_customer
                .addFloat(analytics.getTotalRevenue()) // total_revenue
                .addFloat(analytics.getTotalRevenueOfProduct()) // total_revenue_of_product
                .addFloat(analytics.getTotalRevenueOfCustomer()) // total_revenue_of_customer
                .addFloat(analytics.getTotalRevenueOfCustomerWithProduct()) // total_revenue_of_customer_with_product
                .addString(analytics.getDescription()) // description
        ).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByCustomerID (" +
                        "product, customer_id, calculated_at, " +
                        "number_of_total_purchases, number_of_total_purchases_including_product, " +
                        "number_of_total_purchases_of_customer, number_of_total_purchases_including_product_of_customer, "
                        +
                        "total_revenue, total_revenue_of_product, total_revenue_of_customer, total_revenue_of_customer_with_product, description"
                        +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .executeBatch(tuples)
                .onItem().transform(pgRowSet -> {
                    System.out.println("[DEBUG] saveAll(): batch executed for " + tuples.size() + " tuples.");
                    return true; // assume batch is OK if no exception was thrown
                });

    }

    @Override
    public String toString() {
        return "SelledProductAnalyticsByCustomerID [customerID=" + customerID +
                ", id=" + getId() +
                ", product=" + getProduct() +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfTotalPurchasesIncludingProduct() +
                ", numberOfTotalPurchasesOfCustomer=" + numberOfTotalPurchasesOfCustomer +
                ", numberOfTotalPurchasesIncludingProductOfCustomer=" + numberOfTotalPurchasesIncludingProductOfCustomer
                +
                ", totalRevenue=" + getTotalRevenue() +
                ", totalRevenueOfProduct=" + totalRevenueOfProduct +
                ", totalRevenueOfCustomer=" + totalRevenueOfCustomer +
                ", totalRevenueOfCustomerWithProduct=" + totalRevenueOfCustomerWithProduct +
                ", description=" + getDescription() + "]";
    }
}
