package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
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

    @Nonnull
    private String storeId;
    private Boolean isStoreValid;
    private String comments;
    private String verifiedBy;

}
