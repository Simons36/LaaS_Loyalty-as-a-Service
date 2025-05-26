package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;

public class SelledProduct {

    @JsonProperty("Product")
    private String product;

    @Nullable
    @JsonProperty("DiscountCoupon_ID")
    private String discountCouponID;

    
    @JsonProperty("LoyaltyCard_ID")
    private String loyaltyCardID;
    
    @JsonProperty("Customer_ID")
    private String customerID;
    
    @JsonProperty("Location")
    private String location;
    
    @JsonProperty("Shop_ID")
    private String shopID;
    
    @JsonProperty("BasePrice")
    private Float basePrice;

    @JsonProperty("DiscountedPrice")
    private Float discountedPrice;

    public SelledProduct() {
    }

    public SelledProduct(String product, String discountCouponID, String loyaltyCardID, String customerID, String location, String shopID, Float basePrice, Float discountedPrice) {
        this.product = product;
        this.discountCouponID = discountCouponID;
        this.loyaltyCardID = loyaltyCardID;
        this.customerID = customerID;
        this.location = location;
        this.shopID = shopID;
        this.basePrice = basePrice;
        this.discountedPrice = discountedPrice;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDiscountCouponID() {
        return discountCouponID;
    }

    public void setDiscountCouponID(String discountCouponID) {
        this.discountCouponID = discountCouponID;
    }

    public String getLoyaltyCardID() {
        return loyaltyCardID;
    }

    public void setLoyaltyCardID(String loyaltyCardID) {
        this.loyaltyCardID = loyaltyCardID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public Float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Float basePrice) {
        this.basePrice = basePrice;
    }

    public Float getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Float discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String toJsonString(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public String toString() {
        return "SelledProduct{" +
                "product='" + product + '\'' +
                ", discountCouponID='" + discountCouponID + '\'' +
                ", loyaltyCardID='" + loyaltyCardID + '\'' +
                ", customerID='" + customerID + '\'' +
                ", location='" + location + '\'' +
                ", shopID='" + shopID + '\'' +
                '}';
    }
    
}
