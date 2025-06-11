package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByDiscountCoupon extends SelledProductAnalytics {

    private int totalCouponsUsed;
    private int couponsUsedOnProduct;
    private float totalValueDiscounted;
    private float valueDiscountedOnProduct;

    public SelledProductAnalyticsByDiscountCoupon() {
    }

    public SelledProductAnalyticsByDiscountCoupon(Long id, LocalDateTime calculatedAt, String product,
            int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct,
            int totalCouponsUsed, int couponsUsedOnProduct,
            float totalValueDiscounted, float valueDiscountedOnProduct,
            String description) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct,
                totalValueDiscounted, description); // You can decide what to use in the totalRevenue field (I used
                                                    // totalValueDiscounted here)
        this.totalCouponsUsed = totalCouponsUsed;
        this.couponsUsedOnProduct = couponsUsedOnProduct;
        this.totalValueDiscounted = totalValueDiscounted;
        this.valueDiscountedOnProduct = valueDiscountedOnProduct;
    }

    public int getTotalCouponsUsed() {
        return totalCouponsUsed;
    }

    public void setTotalCouponsUsed(int totalCouponsUsed) {
        this.totalCouponsUsed = totalCouponsUsed;
    }

    public int getCouponsUsedOnProduct() {
        return couponsUsedOnProduct;
    }

    public void setCouponsUsedOnProduct(int couponsUsedOnProduct) {
        this.couponsUsedOnProduct = couponsUsedOnProduct;
    }

    public float getTotalValueDiscounted() {
        return totalValueDiscounted;
    }

    public void setTotalValueDiscounted(float totalValueDiscounted) {
        this.totalValueDiscounted = totalValueDiscounted;
    }

    public float getValueDiscountedOnProduct() {
        return valueDiscountedOnProduct;
    }

    public void setValueDiscountedOnProduct(float valueDiscountedOnProduct) {
        this.valueDiscountedOnProduct = valueDiscountedOnProduct;
    }

    private static SelledProductAnalyticsByDiscountCoupon from(Row row) {
        return new SelledProductAnalyticsByDiscountCoupon(
                row.getLong("id"),
                row.getLocalDateTime("calculated_at"),
                row.getString("product"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_total_purchases_including_product"),
                row.getInteger("total_coupons_used"),
                row.getInteger("coupons_used_on_product"),
                row.getFloat("total_value_discounted"),
                row.getFloat("value_discounted_on_product"),
                row.getString("description"));
    }

    public static Multi<SelledProductAnalyticsByDiscountCoupon> findAll(MySQLPool client) {
        return client.preparedQuery("SELECT * FROM SelledProductByDiscountCoupon ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(SelledProductAnalyticsByDiscountCoupon::from);
    }

    public static Uni<SelledProductAnalyticsByDiscountCoupon> findByID(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT * FROM SelledProductByDiscountCoupon WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Boolean> save(MySQLPool client) {
        Tuple tuple = Tuple.tuple()
                .addLocalDateTime(this.getCalculatedAt())
                .addString(this.getProduct())
                .addInteger(this.getNumberOfTotalPurchases())
                .addInteger(this.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(this.getTotalCouponsUsed())
                .addInteger(this.getCouponsUsedOnProduct())
                .addFloat(this.getTotalValueDiscounted())
                .addFloat(this.getValueDiscountedOnProduct())
                .addString(this.getDescription());

        return client.preparedQuery("INSERT INTO SelledProductByDiscountCoupon (" +
                "calculated_at, product, " +
                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                "total_coupons_used, coupons_used_on_product, " +
                "total_value_discounted, value_discounted_on_product, " +
                "description" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByDiscountCoupon> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addLocalDateTime(analytics.getCalculatedAt())
                .addString(analytics.getProduct())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addInteger(analytics.getNumberOfTotalPurchasesIncludingProduct())
                .addInteger(analytics.getTotalCouponsUsed())
                .addInteger(analytics.getCouponsUsedOnProduct())
                .addFloat(analytics.getTotalValueDiscounted())
                .addFloat(analytics.getValueDiscountedOnProduct())
                .addString(analytics.getDescription())).toList();

        return client.preparedQuery("INSERT INTO SelledProductByDiscountCoupon (" +
                "calculated_at, product, " +
                "number_of_total_purchases, number_of_total_purchases_including_product, " +
                "total_coupons_used, coupons_used_on_product, " +
                "total_value_discounted, value_discounted_on_product, " +
                "description" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .executeBatch(tuples)
                .onItem().transform(pgRowSet -> {
                    System.out.println("[DEBUG] saveAll(): batch executed for " + tuples.size() + " tuples.");
                    return true; // assume batch is OK if no exception was thrown
                });

    }

    @Override
    public String toString() {
        return "SelledProductAnalyticsByDiscountCoupon{" +
                "id=" + getId() +
                ", product='" + getProduct() + '\'' +
                ", calculatedAt=" + getCalculatedAt() +
                ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() +
                ", numberOfPurchasesIncludingProduct=" + getNumberOfTotalPurchasesIncludingProduct() +
                ", totalCouponsUsed=" + totalCouponsUsed +
                ", couponsUsedOnProduct=" + couponsUsedOnProduct +
                ", totalValueDiscounted=" + totalValueDiscounted +
                ", valueDiscountedOnProduct=" + valueDiscountedOnProduct +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}
