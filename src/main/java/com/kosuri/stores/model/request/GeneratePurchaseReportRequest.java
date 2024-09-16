package com.kosuri.stores.model.request;

import org.springframework.http.HttpMethod;

import java.net.URI;

public class GeneratePurchaseReportRequest extends GenerateReportRequest {
    public GeneratePurchaseReportRequest(HttpMethod method, URI url) {
        super(method, url);
    }
}
