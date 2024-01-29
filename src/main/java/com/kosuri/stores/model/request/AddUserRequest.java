package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

public class AddUserRequest extends RequestEntity<AddUserRequest> {
    public AddUserRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    @Nonnull
    private String name;
    @Nonnull
    private String address;
    @Nonnull
    private String phoneNumber;
    @Nonnull
    private String email;
    @Nonnull
    private String role;
    @Nonnull
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
