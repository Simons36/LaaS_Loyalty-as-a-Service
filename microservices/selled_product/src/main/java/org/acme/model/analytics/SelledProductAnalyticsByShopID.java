package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByShopID extends SelledProductAnalytics {

    private String shopID;

    private int numberOfTotalPurchasesInShop;
    private int numberOfTotalPurchasesIncludingProductInShop;

    private float totalRevenueOfProduct;
    private float totalRevenueInShop;
    private float totalRevenueInShopWithProduct;

    public SelledProductAnalyticsByShopID() {
    }

    public SelledProductAnalyticsByShopID(Long id, String product, LocalDateTime calculatedAt,
            int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct,
            int numberOfTotalPurchasesInShop, int numberOfTotalPurchasesIncludingProductInShop,
            float totalRevenue, float totalRevenueOfProduct, float totalRevenueInShop,
            float totalRevenueInShopWithProduct,
            String description, String shopID) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue,
                description);
        this.shopID = shopID;
        this.numberOfTotalPurchasesInShop = numberOfTotalPurchasesInShop;
        this.numberOfTotalPurchasesIncludingProductInShop = numberOfTotalPurchasesIncludingProductInShop;
        this.totalRevenueOfProduct = totalRevenueOfProduct;
        this.totalRevenueInShop = totalRevenueInShop;
        this.totalRevenueInShopWithProduct = totalRevenueInShopWithProduct;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public int getNumberOfTotalPurchasesInShop() {
        return numberOfTotalPurchasesInShop;
    }

    public void setNumberOfTotalPurchasesInShop(int numberOfTotalPurchasesInShop) {
        this.numberOfTotalPurchasesInShop = numberOfTotalPurchasesInShop;
    }

    public int getNumberOfTotalPurchasesIncludingProductInShop() {
        return numberOfTotalPurchasesIncludingProductInShop;
    }

    public void setNumberOfTotalPurchasesIncludingProductInShop(int numberOfTotalPurchasesIncludingProductInShop) {
        this.numberOfTotalPurchasesIncludingProductInShop = numberOfTotalPurchasesIncludingProductInShop;
    }

    public float getTotalRevenueOfProduct() {
        return totalRevenueOfProduct;
    }

    public void setTotalRevenueOfProduct(float totalRevenueOfProduct) {
        this.totalRevenueOfProduct = totalRevenueOfProduct;
    }

    public float getTotalRevenueInShop() {
        return totalRevenueInShop;
    }

    public void setTotalRevenueInShop(float totalRevenueInShop) {
        this.totalRevenueInShop = totalRevenueInShop;
    }

    public float getTotalRevenueInShopWithProduct() {
        return totalRevenueInShopWithProduct;
    }

    public void setTotalRevenueInShopWithProduct(float totalRevenueInShopWithProduct) {
        this.totalRevenueInShopWithProduct = totalRevenueInShopWithProduct;
    }

    private static SelledProductAnalyticsByShopID from(Row row) {
        return new SelledProductAnalyticsByShopID(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_purchases_including_product"),
                row.getInteger("number_of_total_purchases_in_shop"),
                row.getInteger("number_of_total_purchases_including_product_in_shop"),
                row.getFloat("total_revenue"),
                row.getFloat("total_revenue_of_product"),
                row.getFloat("total_revenue_in_shop"),
                row.getFloat("total_revenue_in_shop_with_product"),
                row.getString("description"),
                row.getString("shop_id"));
    }

    public static Multi<SelledProductAnalyticsByShopID> findAll(MySQLPool client) {
        return client.preparedQuery("SELECT * FROM SelledProductByShopID ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByShopID::from);
    }

    public static Multi<SelledProductAnalyticsByShopID> findByID(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT * FROM SelledProductByShopID WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                .onItem().transform(SelledProductAnalyticsByShopID::from);
    }

    public Uni<Boolean> save(MySQLPool client) {
        Tuple tuple = Tuple.tuple()
                .addString(this.getProduct())
                .addString(this.getShopID())
                .addLocalDateTime(this.getCalculatedAt())
                .addInteger(this.getNumberOfTotalPurchases())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(this.getNumberOfTotalPurchasesInShop())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProductInShop())
                .addFloat(this.getTotalRevenue())
                .addFloat(this.getTotalRevenueOfProduct())
                .addFloat(this.getTotalRevenueInShop())
                .addFloat(this.getTotalRevenueInShopWithProduct())
                .addString(this.getDescription());

        return client.preparedQuery("INSERT INTO SelledProductByShopID (" +
                "product, shop_id, calculated_at, " +
                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                "number_of_total_purchases_in_shop, number_of_total_purchases_including_product_in_shop, " +
                "total_revenue, total_revenue_of_product, total_revenue_in_shop, total_revenue_in_shop_with_product, " +
                "description" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByShopID> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getProduct())
                .addString(analytics.getShopID())
                .addLocalDateTime(analytics.getCalculatedAt())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(analytics.getNumberOfTotalPurchasesInShop())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProductInShop())
                .addFloat(analytics.getTotalRevenue())
                .addFloat(analytics.getTotalRevenueOfProduct())
                .addFloat(analytics.getTotalRevenueInShop())
                .addFloat(analytics.getTotalRevenueInShopWithProduct())
                .addString(analytics.getDescription())).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByShopID (" +
                        "product, shop_id, calculated_at, " +
                        "number_of_total_purchases, number_of_total_purchases_including_product, " +
                        "number_of_total_purchases_in_shop, number_of_total_purchases_including_product_in_shop, " +
                        "total_revenue, total_revenue_of_product, total_revenue_in_shop, total_revenue_in_shop_with_product, "
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
        return "SelledProductAnalyticsByShopID{" +
                "id=" + getId() +
                ", product='" + getProduct() + '\'' +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfTotalPurchasesIncludingProduct() +
                ", numberOfTotalPurchasesInShop=" + numberOfTotalPurchasesInShop +
                ", numberOfTotalPurchasesIncludingProductInShop=" + numberOfTotalPurchasesIncludingProductInShop +
                ", totalRevenue=" + getTotalRevenue() +
                ", totalRevenueOfProduct=" + totalRevenueOfProduct +
                ", totalRevenueInShop=" + totalRevenueInShop +
                ", totalRevenueInShopWithProduct=" + totalRevenueInShopWithProduct +
                ", description='" + getDescription() + '\'' +
                ", shopID='" + shopID + '\'' +
                '}';
    }
}
