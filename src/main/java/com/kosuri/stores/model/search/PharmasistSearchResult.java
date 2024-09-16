package com.kosuri.stores.model.search;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PharmasistSearchResult {
    private String name;
    private String pharmaUserEmail;
    private String pharmaUserContact;
    private String education;
    private String experience;
    private String pciCertified;
    private String pciExpiryDate;
    private String availableLocation;

}



