package com.kosuri.stores.model.request;

import org.springframework.http.HttpMethod;

import java.net.URI;

public class GenerateSaleReportRequest extends GenerateReportRequest {
    public GenerateSaleReportRequest(HttpMethod method, URI url) {
        super(method, url);
    }
}
