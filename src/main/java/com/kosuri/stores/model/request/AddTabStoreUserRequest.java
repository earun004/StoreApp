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
public class AddTabStoreUserRequest extends RequestEntity<AddTabStoreUserRequest> {
    public AddTabStoreUserRequest(HttpMethod method, URI url) {
        super(method, url);
    }


    private String userFullName;
    @Nonnull
    private String userPhoneNumber;
    @Nonnull
    private String userEmail;

    private String status;

    private String addedBy;

    private String storeAdminEmail;

    private String storeAdminMobile;

    private String role;

    private String store;

    @Nonnull
    private String password;

	private String userType;
}
