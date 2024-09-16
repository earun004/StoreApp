package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyOTPRequest {

    @NonNull
    private String otp;
    private String email;
    private String phoneNumber;
    private Boolean isForgetPassword;
}
