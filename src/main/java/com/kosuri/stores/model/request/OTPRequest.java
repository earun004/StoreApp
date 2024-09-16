package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OTPRequest {

    private String email;
    private String phoneNumber;
    private Boolean isForgetPassword;
}
