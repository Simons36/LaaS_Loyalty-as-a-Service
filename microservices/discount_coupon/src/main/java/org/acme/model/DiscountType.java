package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscountType {
    
    // product, discountPercetage

    @JsonProperty("Product")
    public String product;

    @JsonProperty("DiscountPercentage")
    public Float discountPercentage;

    public DiscountType() {  }

    public DiscountType(String product, Float discountPercentage) {  
        this.product = product;  
        this.discountPercentage = discountPercentage;  
    }
    @Override
    public String toString() 
    {  
        return "DiscountType [product=" + product + ", discountPercentage=" + discountPercentage + "]";  
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Float getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

}
