package com.kosuri.stores.model.request;


import jakarta.annotation.Nonnull;

public class ConfigureLoyaltyPointsRequest {

    @Nonnull
    private String storeId;
    private String storeCategory;
    private Integer loyaltyPoints;
    private Double salesVolume;
    private Double fixedDiscountPercentage;
    private Integer minLoyaltyPoints;

    public String getStoreId() {
        return storeId;
    }

    public Integer getMinLoyaltyPoints() {
        return minLoyaltyPoints;
    }

    public void setMinLoyaltyPoints(Integer minLoyaltyPoints) {
        this.minLoyaltyPoints = minLoyaltyPoints;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreCategory() {
        return storeCategory;
    }

    public void setStoreCategory(String storeCategory) {
        this.storeCategory = storeCategory;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Double getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(Double salesVolume) {
        this.salesVolume = salesVolume;
    }

    public Double getFixedDiscountPercentage() {
        return fixedDiscountPercentage;
    }

    public void setFixedDiscountPercentage(Double fixedDiscountPercentage) {
        this.fixedDiscountPercentage = fixedDiscountPercentage;
    }

}
