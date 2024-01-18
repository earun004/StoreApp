package com.kosuri.stores.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

@Getter
@Setter
@ToString
public class AdminStoreRequest extends RequestEntity<AdminStoreRequest> {

    public AdminStoreRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    @NotNull
    private String storeId;
    private boolean isStoreValid;
    private String comments;
    private String verifiedBy;

}
