package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

@Getter
@Setter
@ToString
public class DiagnosticCenterRequest extends RequestEntity<DiagnosticCenterRequest> {


    private String phoneNumber;
    private String email;
    private String serviceName;
    private String userId;
    private String serviceCategory;
    private String serviceId;
    private String status;
    private String pinCode;
    private String price;
    private String description;
    private String updatedBy;
    private String storeId;
    private String priceUpdatedBy;
    public DiagnosticCenterRequest(HttpMethod method, URI url) {
        super(method, url);
    }
}

