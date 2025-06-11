package org.acme.model.analytics;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SelledProductAnalytics {
    
    private Long id;
    
    private String product;

    private LocalDateTime calculatedAt;

    private int numberOfTotalPurchases;

    private int numberOfTotalPurchasesIncludingProduct;

    private Float totalRevenue;

    private String description;

    public SelledProductAnalytics() {
    }

    public SelledProductAnalytics(Long id, String product, LocalDateTime calculatedAt, int numberOfTotalPurchases, int numberOfTotalPurchasesIncludingProduct, Float totalRevenue, String description) {
        this.id = id;
        this.product = product;
        this.calculatedAt = calculatedAt;
        this.numberOfTotalPurchases = numberOfTotalPurchases;
        this.numberOfTotalPurchasesIncludingProduct = numberOfTotalPurchasesIncludingProduct;
        this.totalRevenue = totalRevenue;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public int getNumberOfTotalPurchases() {
        return numberOfTotalPurchases;
    }

    public void setNumberOfTotalPurchases(int numberOfTotalPurchases) {
        this.numberOfTotalPurchases = numberOfTotalPurchases;
    }

    public int getNumberOfTotalPurchasesIncludingProduct() {
        return numberOfTotalPurchasesIncludingProduct;
    }

    public void setNumberOfTotalPurchasesIncludingProduct(int numberOfTotalPurchasesIncludingProduct) {
        this.numberOfTotalPurchasesIncludingProduct = numberOfTotalPurchasesIncludingProduct;
    }

    public Float getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toJsonString(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize SelledProductAnalyticsByLocation to JSON", e);
        }
    }

    @Override
    public String toString() {
        return "SelledProductAnalytics{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", calculatedAt=" + calculatedAt +
                ", numberOfTotalPurchases=" + numberOfTotalPurchases +
                ", numberOfTotalPurchasesIncludingProduct=" + numberOfTotalPurchasesIncludingProduct +
                ", totalRevenue=" + totalRevenue +
                ", description='" + description + '\'' +
                '}';
    }

    


}
