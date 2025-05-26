package org.acme;

import java.time.LocalDateTime;
import java.util.List;

import org.acme.model.SelledProduct;
import org.acme.model.SelledProductByDiscountCouponAnalytics;

public class AnalyticsCalculation {
    
    public static List<SelledProductByDiscountCouponAnalytics> calculateDiscountCouponsAnalytics(List<SelledProduct> selledProducts) {

        int numberOfTotalPurchases = selledProducts.size();

        List<String> products = selledProducts.stream()
                .map(SelledProduct::getProduct)
                .distinct()
                .toList();

        List<SelledProductByDiscountCouponAnalytics> analytics = products.stream()
                .map(product -> {
                    int numberOfPurchasesIncludingProduct = (int) selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .count();

                    int couponsUsed = (int) selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product) && sp.getDiscountCouponID() != null)
                            .count();

                    Float discountedValue = selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product) && sp.getDiscountCouponID() != null)
                            .map(SelledProduct::getDiscountedPrice)
                            .reduce(0f, Float::sum);

                    return new SelledProductByDiscountCouponAnalytics(
                            null,
                            LocalDateTime.now(),
                            numberOfTotalPurchases,
                            product,
                            numberOfPurchasesIncludingProduct,
                            couponsUsed,
                            discountedValue,
                            "placeholder"
                    );
                })
                .toList();

        return analytics;
    }

}
