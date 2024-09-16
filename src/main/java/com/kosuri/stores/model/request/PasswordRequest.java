package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordRequest {

    private String emailAddress;
    private String userContactNumber;
    private String password;
    private String confirmPassword;
    private String otp;
    private Boolean isForgetPassword;

}
