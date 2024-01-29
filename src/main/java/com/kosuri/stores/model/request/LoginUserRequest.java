package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

@Setter
@Getter
@ToString
public class LoginUserRequest extends RequestEntity<LoginUserRequest> {
    public LoginUserRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    private String email;
    private String phoneNumber;
    @Nonnull
    private String password;

}

