package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;


public class CreateRoleRequest extends RequestEntity<CreateRoleRequest> {
    public CreateRoleRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    @Nonnull
    private String roleId;
    @Nonnull
    private String roleName;
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

