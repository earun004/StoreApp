package com.kosuri.stores.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserResponse extends GenericResponse {

    private String userId;
    private String userFullName;
    private String userType;
    private String userEmailAddress;
    private String userContact;

}
