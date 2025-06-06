package org.acme.model.analytics;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalyticsByDiscountCoupon extends SelledProductAnalytics{
    
    private Integer purhcasesThatUsedCoupon;

    private Float totalDiscounted;

    public SelledProductAnalyticsByDiscountCoupon() {
    }

    public SelledProductAnalyticsByDiscountCoupon(Long id, String product, LocalDateTime calculatedAt, int numberOfTotalPurchases, int numberOfPurchasesIncludingProduct, Float totalRevenue, String description, Integer purhcasesThatUsedCoupon, Float totalDiscounted) {
        super(id, product, calculatedAt, numberOfTotalPurchases, numberOfPurchasesIncludingProduct, totalRevenue, description);
        this.purhcasesThatUsedCoupon = purhcasesThatUsedCoupon;
        this.totalDiscounted = totalDiscounted;
    }

    public Integer getPurhcasesThatUsedCoupon() {
        return purhcasesThatUsedCoupon;
    }

    public void setPurhcasesThatUsedCoupon(Integer purhcasesThatUsedCoupon) {
        this.purhcasesThatUsedCoupon = purhcasesThatUsedCoupon;
    }

    public Float getTotalDiscounted() {
        return totalDiscounted;
    }

    public void setTotalDiscounted(Float totalDiscounted) {
        this.totalDiscounted = totalDiscounted;
    }

    private static SelledProductAnalyticsByDiscountCoupon from(Row row) {
        return new SelledProductAnalyticsByDiscountCoupon(
                row.getLong("id"),
                row.getString("product"),
                row.getLocalDateTime("calculated_at"),
                row.getInteger("number_of_total_purchases"),
                row.getInteger("number_of_purchases_including_product"),
                row.getFloat("total_revenue"),
                row.getString("description"),
                row.getInteger("purhcases_that_used_coupon"),
                row.getFloat("total_discounted")
        );
    }

    public static Multi<SelledProductAnalyticsByDiscountCoupon> findAll(MySQLPool client){
        return client.preparedQuery("SELECT * FROM SelledProductByDiscountCoupon ORDER BY id ASC")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(SelledProductAnalyticsByDiscountCoupon::from);
    }

    public static Uni<SelledProductAnalyticsByDiscountCoupon> findByID(MySQLPool client, Long id){
        return client.preparedQuery("SELECT * FROM SelledProductByDiscountCoupon WHERE id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Boolean> save(MySQLPool client, String calculatedAt, int numberOfTotalPurchases, String product, int numberOfPurchasesIncludingProduct, int couponsUsed, Float discountedValue, String description) {
        Tuple tuple = Tuple.tuple()
                .addString(calculatedAt)
                .addInteger(numberOfTotalPurchases)
                .addString(product)
                .addInteger(numberOfPurchasesIncludingProduct)
                .addInteger(couponsUsed)
                .addFloat(discountedValue)
                .addString(description);

        return client.preparedQuery("INSERT INTO SelledProductByDiscountCoupon (calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, coupons_used, discounted_value, description) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .execute(tuple)
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> saveAll(MySQLPool client, List<SelledProductAnalyticsByDiscountCoupon> analyticsList) {
        if (analyticsList == null || analyticsList.isEmpty()) {
            return Uni.createFrom().item(false);
        }

        // Prepare a batch of tuples for all analytics
        List<Tuple> tuples = analyticsList.stream().map(analytics -> Tuple.tuple()
                .addString(analytics.getCalculatedAt().toString())
                .addInteger(analytics.getNumberOfTotalPurchases())
                .addString(analytics.getProduct())
                .addInteger(analytics.getNumberOfPurchasesIncludingProduct())
                .addInteger(analytics.getPurhcasesThatUsedCoupon())
                .addFloat(analytics.getTotalDiscounted())
                .addString(analytics.getDescription())
        ).toList();

        return client.preparedQuery(
                "INSERT INTO SelledProductByDiscountCoupon (calculated_at, number_of_total_purchases, product, number_of_purchases_including_product, coupons_used, discounted_value, description) VALUES (?, ?, ?, ?, ?, ?, ?)"
            )
            .executeBatch(tuples)
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == analyticsList.size());
    }


    @Override
    public String toString() {
        return "SelledProductAnalyticsByDiscountCoupon [purhcasesThatUsedCoupon=" + purhcasesThatUsedCoupon + ", totalDiscounted=" + totalDiscounted + ", id=" + getId() + ", product=" + getProduct() + ", calculatedAt=" + getCalculatedAt() + ", numberOfTotalPurchases=" + getNumberOfTotalPurchases() + ", numberOfPurchasesIncludingProduct=" + getNumberOfPurchasesIncludingProduct() + ", totalRevenue=" + getTotalRevenue() + ", description=" + getDescription() + "]";
    }

}
