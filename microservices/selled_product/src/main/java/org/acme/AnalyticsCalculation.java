package org.acme;

import java.time.LocalDateTime;
import java.util.List;

import org.acme.model.SelledProduct;
import org.acme.model.analytics.SelledProductAnalyticsByCustomerID;
import org.acme.model.analytics.SelledProductAnalyticsByDiscountCoupon;
import org.acme.model.analytics.SelledProductAnalyticsByLocation;
import org.acme.model.analytics.SelledProductAnalyticsByLoyaltyCardID;
import org.acme.model.analytics.SelledProductAnalyticsByShopID;

public class AnalyticsCalculation {
    
    public static List<SelledProductAnalyticsByDiscountCoupon> calculateDiscountCouponsAnalytics(List<SelledProduct> selledProducts) {

        int numberOfTotalPurchases = selledProducts.size();

        List<String> products = selledProducts.stream()
                .map(SelledProduct::getProduct)
                .distinct()
                .toList();

        List<SelledProductAnalyticsByDiscountCoupon> analytics = products.stream()
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

                    return new SelledProductAnalyticsByDiscountCoupon(
                                null, // ID will be set by the database
                                product,
                                LocalDateTime.now(),
                                numberOfTotalPurchases,
                                numberOfPurchasesIncludingProduct,
                                selledProducts.stream()
                                        .filter(sp -> sp.getProduct().equals(product))
                                        .map(SelledProduct::getDiscountedPrice)
                                        .reduce(0f, Float::sum),
                                "Analytics for product: " + product,
                                couponsUsed,
                                discountedValue
                    );
                })
                .toList();

        return analytics;
    }

    public static List<SelledProductAnalyticsByCustomerID> calculateCustomerIDAnalytics(List<SelledProduct> selledProducts) {

        int numberOfTotalPurchases = selledProducts.size();

        List<String> products = selledProducts.stream()
                .map(SelledProduct::getProduct)
                .distinct()
                .toList();

        List<SelledProductAnalyticsByCustomerID> analytics = products.stream()
                .map(product -> {
                    int numberOfPurchasesIncludingProduct = (int) selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .count();

                    return selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .map(sp -> new SelledProductAnalyticsByCustomerID(
                                    null, // ID will be set by the database
                                    product,
                                    LocalDateTime.now(),
                                    numberOfTotalPurchases,
                                    numberOfPurchasesIncludingProduct,
                                    sp.getDiscountedPrice(),
                                    "Analytics for product: " + product,
                                    sp.getCustomerID()))
                            .toList();
                })
                .flatMap(List::stream)
                .toList();

        return analytics;
    }

    public static List<SelledProductAnalyticsByLocation> calculateLocationAnalytics(List<SelledProduct> selledProducts) {

        int numberOfTotalPurchases = selledProducts.size();

        List<String> products = selledProducts.stream()
                .map(SelledProduct::getProduct)
                .distinct()
                .toList();

        List<SelledProductAnalyticsByLocation> analytics = products.stream()
                .map(product -> {
                    int numberOfPurchasesIncludingProduct = (int) selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .count();

                    return selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .map(sp -> new SelledProductAnalyticsByLocation(
                                    null, // ID will be set by the database
                                    product,
                                    LocalDateTime.now(),
                                    numberOfTotalPurchases,
                                    numberOfPurchasesIncludingProduct,
                                    sp.getDiscountedPrice(),
                                    "Analytics for product: " + product,
                                    sp.getLocation()))
                            .toList();
                })
                .flatMap(List::stream)
                .toList();

        return analytics;
    }

    public static List<SelledProductAnalyticsByLoyaltyCardID> calculateLoyaltyCardIDAnalytics(List<SelledProduct> selledProducts) {

        int numberOfTotalPurchases = selledProducts.size();

        List<String> products = selledProducts.stream()
                .map(SelledProduct::getProduct)
                .distinct()
                .toList();

        List<SelledProductAnalyticsByLoyaltyCardID> analytics = products.stream()
                .map(product -> {
                    int numberOfPurchasesIncludingProduct = (int) selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .count();

                    return selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .map(sp -> new SelledProductAnalyticsByLoyaltyCardID(
                                    null, // ID will be set by the database
                                    product,
                                    LocalDateTime.now(),
                                    numberOfTotalPurchases,
                                    numberOfPurchasesIncludingProduct,
                                    sp.getDiscountedPrice(),
                                    "Analytics for product: " + product,
                                    sp.getLoyaltyCardID()))
                            .toList();
                })
                .flatMap(List::stream)
                .toList();

        return analytics;
    }

    public static List<SelledProductAnalyticsByShopID> calculateShopIDAnalytics(List<SelledProduct> selledProducts) {

        int numberOfTotalPurchases = selledProducts.size();

        List<String> products = selledProducts.stream()
                .map(SelledProduct::getProduct)
                .distinct()
                .toList();

        List<SelledProductAnalyticsByShopID> analytics = products.stream()
                .map(product -> {
                    int numberOfPurchasesIncludingProduct = (int) selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .count();

                    return selledProducts.stream()
                            .filter(sp -> sp.getProduct().equals(product))
                            .map(sp -> new SelledProductAnalyticsByShopID(
                                    null, // ID will be set by the database
                                    product,
                                    LocalDateTime.now(),
                                    numberOfTotalPurchases,
                                    numberOfPurchasesIncludingProduct,
                                    sp.getDiscountedPrice(),
                                    "Analytics for product: " + product,
                                    sp.getShopID()))
                            .toList();
                })
                .flatMap(List::stream)
                .toList();

        return analytics;
    }

}
