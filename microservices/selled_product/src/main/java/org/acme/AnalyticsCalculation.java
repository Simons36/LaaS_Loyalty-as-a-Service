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

        public static List<SelledProductAnalyticsByDiscountCoupon> calculateDiscountCouponsAnalytics(
                        List<SelledProduct> selledProducts) {

                int numberOfTotalPurchases = selledProducts.size();

                List<String> products = selledProducts.stream()
                                .map(SelledProduct::getProduct)
                                .distinct()
                                .toList();

                // Total coupons used across all purchases
                int totalCouponsUsed = (int) selledProducts.stream()
                                .filter(sp -> sp.getDiscountCouponID() != null)
                                .count();

                // Total value discounted across all purchases
                float totalValueDiscounted = (float) selledProducts.stream()
                                .filter(sp -> sp.getDiscountCouponID() != null)
                                .mapToDouble(SelledProduct::getDiscountedPrice)
                                .sum();

                List<SelledProductAnalyticsByDiscountCoupon> analytics = products.stream()
                                .map(product -> {

                                        int numberOfPurchasesIncludingProduct = (int) selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product))
                                                        .count();

                                        int couponsUsedOnProduct = (int) selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product)
                                                                        && sp.getDiscountCouponID() != null)
                                                        .count();

                                        float valueDiscountedOnProduct = (float) selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product)
                                                                        && sp.getDiscountCouponID() != null)
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        return new SelledProductAnalyticsByDiscountCoupon(
                                                        null, // ID will be set by the database
                                                        LocalDateTime.now(),
                                                        product,
                                                        numberOfTotalPurchases,
                                                        numberOfPurchasesIncludingProduct,
                                                        totalCouponsUsed,
                                                        couponsUsedOnProduct,
                                                        totalValueDiscounted,
                                                        valueDiscountedOnProduct,
                                                        "Analytics for product: " + product);
                                })
                                .toList();

                return analytics;
        }

        public static List<SelledProductAnalyticsByCustomerID> calculateCustomerIDAnalytics(
                        List<SelledProduct> selledProducts) {

                int numberOfTotalPurchases = selledProducts.size();

                List<String> products = selledProducts.stream()
                                .map(SelledProduct::getProduct)
                                .distinct()
                                .toList();

                List<String> customerIDs = selledProducts.stream()
                                .map(SelledProduct::getCustomerID)
                                .distinct()
                                .toList();

                List<SelledProductAnalyticsByCustomerID> analytics = products.stream()
                                .flatMap(product -> customerIDs.stream().map(customerID -> {

                                        // Filter for current product
                                        List<SelledProduct> productPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product))
                                                        .toList();

                                        // Filter for current customer
                                        List<SelledProduct> customerPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getCustomerID().equals(customerID))
                                                        .toList();

                                        // Filter for product AND customer
                                        List<SelledProduct> productAndCustomerPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product)
                                                                        && sp.getCustomerID().equals(customerID))
                                                        .toList();

                                        int numberOfPurchasesIncludingProduct = productPurchases.size();
                                        int numberOfTotalPurchasesOfCustomer = customerPurchases.size();
                                        int numberOfTotalPurchasesIncludingProductOfCustomer = productAndCustomerPurchases
                                                        .size();

                                        float totalRevenueOfProduct = (float) productPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueOfCustomer = (float) customerPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueOfCustomerWithProduct = (float) productAndCustomerPurchases
                                                        .stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        return new SelledProductAnalyticsByCustomerID(
                                                        null, // ID will be set by DB
                                                        product,
                                                        LocalDateTime.now(),
                                                        numberOfTotalPurchases,
                                                        numberOfPurchasesIncludingProduct,
                                                        numberOfTotalPurchasesOfCustomer,
                                                        numberOfTotalPurchasesIncludingProductOfCustomer,
                                                        totalRevenueOfProduct, // here totalRevenue field → for this
                                                                               // product only
                                                        totalRevenueOfProduct,
                                                        totalRevenueOfCustomer,
                                                        totalRevenueOfCustomerWithProduct,
                                                        "Analytics for product: " + product,
                                                        customerID);

                                }))
                                .toList();

                return analytics;
        }

        public static List<SelledProductAnalyticsByLocation> calculateLocationAnalytics(
                        List<SelledProduct> selledProducts) {

                int numberOfTotalPurchases = selledProducts.size();

                List<String> products = selledProducts.stream()
                                .map(SelledProduct::getProduct)
                                .distinct()
                                .toList();

                List<String> locations = selledProducts.stream()
                                .map(SelledProduct::getLocation)
                                .distinct()
                                .toList();

                List<SelledProductAnalyticsByLocation> analytics = products.stream()
                                .flatMap(product -> locations.stream().map(location -> {

                                        // Filter for current product
                                        List<SelledProduct> productPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product))
                                                        .toList();

                                        // Filter for current location
                                        List<SelledProduct> locationPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getLocation().equals(location))
                                                        .toList();

                                        // Filter for product AND location
                                        List<SelledProduct> productAndLocationPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product)
                                                                        && sp.getLocation().equals(location))
                                                        .toList();

                                        int numberOfPurchasesIncludingProduct = productPurchases.size();
                                        int numberOfTotalPurchasesInLocation = locationPurchases.size();
                                        int numberOfTotalPurchasesIncludingProductInLocation = productAndLocationPurchases
                                                        .size();

                                        float totalRevenueOfProduct = (float) productPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueInLocation = (float) locationPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueInLocationWithProduct = (float) productAndLocationPurchases
                                                        .stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        return new SelledProductAnalyticsByLocation(
                                                        null, // ID will be set by DB
                                                        product,
                                                        LocalDateTime.now(),
                                                        numberOfTotalPurchases,
                                                        numberOfPurchasesIncludingProduct,
                                                        numberOfTotalPurchasesInLocation,
                                                        numberOfTotalPurchasesIncludingProductInLocation,
                                                        totalRevenueOfProduct, // this fills superclass totalRevenue
                                                                               // field too
                                                        totalRevenueOfProduct,
                                                        totalRevenueInLocation,
                                                        totalRevenueInLocationWithProduct,
                                                        "Analytics for product: " + product,
                                                        location);

                                }))
                                .toList();

                return analytics;
        }

        public static List<SelledProductAnalyticsByLoyaltyCardID> calculateLoyaltyCardIDAnalytics(
                        List<SelledProduct> selledProducts) {

                int numberOfTotalPurchases = selledProducts.size();

                List<String> products = selledProducts.stream()
                                .map(SelledProduct::getProduct)
                                .distinct()
                                .toList();

                List<String> loyaltyCardIDs = selledProducts.stream()
                                .map(SelledProduct::getLoyaltyCardID)
                                .distinct()
                                .toList();

                List<SelledProductAnalyticsByLoyaltyCardID> analytics = products.stream()
                                .flatMap(product -> loyaltyCardIDs.stream().map(loyaltyCardID -> {

                                        // Filter for current product
                                        List<SelledProduct> productPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product))
                                                        .toList();

                                        // Filter for current loyalty card
                                        List<SelledProduct> loyaltyCardPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getLoyaltyCardID().equals(loyaltyCardID))
                                                        .toList();

                                        // Filter for product AND loyalty card
                                        List<SelledProduct> productAndLoyaltyCardPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product)
                                                                        && sp.getLoyaltyCardID().equals(loyaltyCardID))
                                                        .toList();

                                        int numberOfPurchasesIncludingProduct = productPurchases.size();
                                        int numberOfTotalPurchasesWithLoyaltyCard = loyaltyCardPurchases.size();
                                        int numberOfTotalPurchasesIncludingProductWithLoyaltyCard = productAndLoyaltyCardPurchases
                                                        .size();

                                        float totalRevenueOfProduct = (float) productPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueWithLoyaltyCard = (float) loyaltyCardPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueWithLoyaltyCardWithProduct = (float) productAndLoyaltyCardPurchases
                                                        .stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        return new SelledProductAnalyticsByLoyaltyCardID(
                                                        null, // ID will be set by DB
                                                        product,
                                                        LocalDateTime.now(),
                                                        numberOfTotalPurchases,
                                                        numberOfPurchasesIncludingProduct,
                                                        numberOfTotalPurchasesWithLoyaltyCard,
                                                        numberOfTotalPurchasesIncludingProductWithLoyaltyCard,
                                                        totalRevenueOfProduct, // superclass totalRevenue field filled
                                                                               // as well
                                                        totalRevenueOfProduct,
                                                        totalRevenueWithLoyaltyCard,
                                                        totalRevenueWithLoyaltyCardWithProduct,
                                                        "Analytics for product: " + product,
                                                        loyaltyCardID);

                                }))
                                .toList();

                return analytics;
        }

        public static List<SelledProductAnalyticsByShopID> calculateShopIDAnalytics(
                        List<SelledProduct> selledProducts) {

                int numberOfTotalPurchases = selledProducts.size();

                List<String> products = selledProducts.stream()
                                .map(SelledProduct::getProduct)
                                .distinct()
                                .toList();

                List<String> shopIDs = selledProducts.stream()
                                .map(SelledProduct::getShopID)
                                .distinct()
                                .toList();

                List<SelledProductAnalyticsByShopID> analytics = products.stream()
                                .flatMap(product -> shopIDs.stream().map(shopID -> {

                                        // Filter for current product
                                        List<SelledProduct> productPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product))
                                                        .toList();

                                        // Filter for current shop
                                        List<SelledProduct> shopPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getShopID().equals(shopID))
                                                        .toList();

                                        // Filter for product AND shop
                                        List<SelledProduct> productAndShopPurchases = selledProducts.stream()
                                                        .filter(sp -> sp.getProduct().equals(product)
                                                                        && sp.getShopID().equals(shopID))
                                                        .toList();

                                        int numberOfPurchasesIncludingProduct = productPurchases.size();
                                        int numberOfTotalPurchasesInShop = shopPurchases.size();
                                        int numberOfTotalPurchasesIncludingProductInShop = productAndShopPurchases
                                                        .size();

                                        float totalRevenueOfProduct = (float) productPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueInShop = (float) shopPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        float totalRevenueInShopWithProduct = (float) productAndShopPurchases.stream()
                                                        .mapToDouble(SelledProduct::getDiscountedPrice)
                                                        .sum();

                                        return new SelledProductAnalyticsByShopID(
                                                        null, // ID will be set by DB
                                                        product,
                                                        LocalDateTime.now(),
                                                        numberOfTotalPurchases,
                                                        numberOfPurchasesIncludingProduct,
                                                        numberOfTotalPurchasesInShop,
                                                        numberOfTotalPurchasesIncludingProductInShop,
                                                        totalRevenueOfProduct, // superclass totalRevenue field filled
                                                                               // as well
                                                        totalRevenueOfProduct,
                                                        totalRevenueInShop,
                                                        totalRevenueInShopWithProduct,
                                                        "Analytics for product: " + product,
                                                        shopID);

                                }))
                                .toList();

                return analytics;
        }

}
