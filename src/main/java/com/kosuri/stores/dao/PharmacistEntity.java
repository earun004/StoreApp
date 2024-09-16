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
@Table(name = "pharmacist_profile")
@Entity
public class PharmacistEntity {

    @Id
    @Nonnull
    private @Column(name = "pharmacist_id") String pharmacistId;
    private @Column(name = "Name") String pharmacistName;
    private @Column(name = "mobile") String pharmacistContact;
    private @Column(name = "email") String pharmacistEmailAddress;
    private @Column(name = "education") String pharmacistEducation;
    private @Column(name = "experience") String pharmacistExperience;
    private @Column(name = "pci_certified") String pharmacistPciCertified;
    private @Column(name = "pci_expiry_date") String pharmacistPciExpiryDate;
    private @Column(name = "Available_location") String pharmacistAvailableLocation;
}
