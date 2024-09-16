package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PharmasistRequest {

    private String name;
    private String pharmaUserEmail;
    private String pharmaUserContact;
    private String education;
    private String experience;
    private String pciCertified;
    private String pciExpiryDate;
    private String availableLocation;
}
