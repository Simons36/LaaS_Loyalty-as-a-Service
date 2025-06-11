package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByLoyaltyCardID extends SelledProductAnalytics {

    private String loyaltyCardID;

    private int numberOfTotalPurchasesWithLoyaltyCard;
    private int numberOfTotalPurchasesIncludingProductWithLoyaltyCard;

    private float totalRevenueOfProduct;
    private float totalRevenueWithLoyaltyCard;
    private float totalRevenueWithLoyaltyCardWithProduct;

    public SelledProductAnalyticsByLoyaltyCardID() {
    }

    public SelledProductAnalyticsByLoyaltyCardID(Long id, String product, LocalDateTime calculatedAt,
            int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct,
            int numberOfTotalPurchasesWithLoyaltyCard, int numberOfTotalPurchasesIncludingProductWithLoyaltyCard,
            float totalRevenue, float totalRevenueOfProduct, float totalRevenueWithLoyaltyCard,
            float totalRevenueWithLoyaltyCardWithProduct,
            String description, String loyaltyCardID) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue,
                description);
        this.loyaltyCardID = loyaltyCardID;
        this.numberOfTotalPurchasesWithLoyaltyCard = numberOfTotalPurchasesWithLoyaltyCard;
        this.numberOfTotalPurchasesIncludingProductWithLoyaltyCard = numberOfTotalPurchasesIncludingProductWithLoyaltyCard;
        this.totalRevenueOfProduct = totalRevenueOfProduct;
        this.totalRevenueWithLoyaltyCard = totalRevenueWithLoyaltyCard;
        this.totalRevenueWithLoyaltyCardWithProduct = totalRevenueWithLoyaltyCardWithProduct;
    }

    public String getLoyaltyCardID() {
        return loyaltyCardID;
    }

    public void setLoyaltyCardID(String loyaltyCardID) {
        this.loyaltyCardID = loyaltyCardID;
    }

    public int getNumberOfTotalPurchasesWithLoyaltyCard() {
        return numberOfTotalPurchasesWithLoyaltyCard;
    }

    public void setNumberOfTotalPurchasesWithLoyaltyCard(int numberOfTotalPurchasesWithLoyaltyCard) {
        this.numberOfTotalPurchasesWithLoyaltyCard = numberOfTotalPurchasesWithLoyaltyCard;
    }

    public int getNumberOfTotalPurchasesIncludingProductWithLoyaltyCard() {
        return numberOfTotalPurchasesIncludingProductWithLoyaltyCard;
    }

    public void setNumberOfTotalPurchasesIncludingProductWithLoyaltyCard(
            int numberOfTotalPurchasesIncludingProductWithLoyaltyCard) {
        this.numberOfTotalPurchasesIncludingProductWithLoyaltyCard = numberOfTotalPurchasesIncludingProductWithLoyaltyCard;
    }

    public float getTotalRevenueOfProduct() {
        return totalRevenueOfProduct;
    }

    public void setTotalRevenueOfProduct(float totalRevenueOfProduct) {
        this.totalRevenueOfProduct = totalRevenueOfProduct;
    }

    public float getTotalRevenueWithLoyaltyCard() {
        return totalRevenueWithLoyaltyCard;
    }

    public void setTotalRevenueWithLoyaltyCard(float totalRevenueWithLoyaltyCard) {
        this.totalRevenueWithLoyaltyCard = totalRevenueWithLoyaltyCard;
    }

    public float getTotalRevenueWithLoyaltyCardWithProduct() {
        return totalRevenueWithLoyaltyCardWithProduct;
    }

    public void setTotalRevenueWithLoyaltyCardWithProduct(float totalRevenueWithLoyaltyCardWithProduct) {
        this.totalRevenueWithLoyaltyCardWithProduct = totalRevenueWithLoyaltyCardWithProduct;
    }

    private static SelledProductAnalyticsByLoyaltyCardID from(Row row) {
        return new SelledProductAnalyticsByLoyaltyCardID(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_total_purchases_including_product"),
                row.getInteger("number_of_total_purchases_with_loyalty_card"),
                row.getInteger("number_of_total_purchases_including_product_with_loyalty_card"),
                row.getFloat("total_revenue"),
                row.getFloat("total_revenue_of_product"),
                row.getFloat("total_revenue_with_loyalty_card"),
                row.getFloat("total_revenue_with_loyalty_card_with_product"),
                row.getString("description"),
                row.getString("loyalty_card_id"));
    }

    public static Multi<SelledProductAnalyticsByLoyaltyCardID> findAll(MySQLPool client) {
        return client.preparedQuery("SELECT * FROM SelledProductByLoyaltyCardID ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLoyaltyCardID::from);
    }

    public static Multi<SelledProductAnalyticsByLoyaltyCardID> findByID(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT * FROM SelledProductByLoyaltyCardID WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByLoyaltyCardID::from);
    }

    public Uni<Boolean> save(MySQLPool client) {
        Tuple tuple = Tuple.tuple()
                .addString(this.getProduct())
                .addString(this.getLoyaltyCardID())
                .addLocalDateTime(this.getCalculatedAt())
                .addInteger(this.getNumberOfTotalPurchases())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(this.getNumberOfTotalPurchasesWithLoyaltyCard())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProductWithLoyaltyCard())
                .addFloat(this.getTotalRevenue())
                .addFloat(this.getTotalRevenueOfProduct())
                .addFloat(this.getTotalRevenueWithLoyaltyCard())
                .addFloat(this.getTotalRevenueWithLoyaltyCardWithProduct())
                .addString(this.getDescription());

        return client.preparedQuery("INSERT INTO SelledProductByLoyaltyCardID (" +
                "product, loyalty_card_id, calculated_at, " +
                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                "number_of_total_purchases_with_loyalty_card, number_of_total_purchases_including_product_with_loyalty_card, "
                +
                "total_revenue, total_revenue_of_product, total_revenue_with_loyalty_card, total_revenue_with_loyalty_card_with_product, "
                +
                "description" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByLoyaltyCardID> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getProduct())
                .addString(analytics.getLoyaltyCardID())
                .addLocalDateTime(analytics.getCalculatedAt())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(analytics.getNumberOfTotalPurchasesWithLoyaltyCard())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProductWithLoyaltyCard())
                .addFloat(analytics.getTotalRevenue())
                .addFloat(analytics.getTotalRevenueOfProduct())
                .addFloat(analytics.getTotalRevenueWithLoyaltyCard())
                .addFloat(analytics.getTotalRevenueWithLoyaltyCardWithProduct())
                .addString(analytics.getDescription())).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByLoyaltyCardID (" +
                        "product, loyalty_card_id, calculated_at, " +
                        "number_of_total_purchases, number_of_total_purchases_including_product, " +
                        "number_of_total_purchases_with_loyalty_card, number_of_total_purchases_including_product_with_loyalty_card, "
                        +
                        "total_revenue, total_revenue_of_product, total_revenue_with_loyalty_card, total_revenue_with_loyalty_card_with_product, "
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
        return "SelledProductAnalyticsByLoyaltyCardID{" +
                "id=" + getId() +
                ", product='" + getProduct() + '\'' +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfTotalPurchasesIncludingProduct() +
                ", numberOfTotalPurchasesWithLoyaltyCard=" + numberOfTotalPurchasesWithLoyaltyCard +
                ", numberOfTotalPurchasesIncludingProductWithLoyaltyCard="
                + numberOfTotalPurchasesIncludingProductWithLoyaltyCard +
                ", totalRevenue=" + getTotalRevenue() +
                ", totalRevenueOfProduct=" + totalRevenueOfProduct +
                ", totalRevenueWithLoyaltyCard=" + totalRevenueWithLoyaltyCard +
                ", totalRevenueWithLoyaltyCardWithProduct=" + totalRevenueWithLoyaltyCardWithProduct +
                ", description='" + getDescription() + '\'' +
                ", loyaltyCardID='" + loyaltyCardID + '\'' +
                '}';
    }
}
