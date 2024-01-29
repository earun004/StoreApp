package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "diagnostic_master")
public class DiagnosticMasterEntity {

    @Id
    @Nonnull
    private @Column(name = "Diagnostic_Id") String diagnosticId;

    private @Column(name = "Phone_Number") String phoneNumber;
    private @Column(name = "Address") String address;
    private @Column(name = "Location") String location;
    private @Column(name = "Email_ID") String userEmail;
    private @Column(name = "Login_Phone") String loginPhone;
    private @Column(name = "Business Name") String businessName;
    private @Column(name = "Userid") String userId;
}
