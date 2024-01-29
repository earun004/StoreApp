package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

public class GenerateReportRequest extends RequestEntity<GenerateReportRequest> {

    public GenerateReportRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    @Nonnull
    private String storeId;
    private String vendorName;
    private String dateFrom;
    private String dateTo;
    private String productType;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
