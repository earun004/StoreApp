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
public class CreateStoreRequest extends RequestEntity<CreateStoreRequest> {
    public CreateStoreRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    private String storeType;
    @Nonnull
    private String id;
    @Nonnull
    private String name;
    @Nonnull
    private String pincode;
    @Nonnull
    private String district;
    private String town;
    @Nonnull
    private String state;
    private String owner;
    private String ownerAddress;
    private String ownerContact;
    private String secondaryContact;
    private String ownerEmail;
    @Nonnull
    private String location;
    private String businessType;
    private String expirationDate;
    private String storeVerificationStatus;
}
