package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacistRepository extends JpaRepository<PharmacistEntity, String> {
    Optional<PharmacistEntity> findByPharmacistEmailAddressOrPharmacistContact(String pharmaUserEmail, String pharmaUserContact);

    List<PharmacistEntity> findByPharmacistEmailAddressOrPharmacistContactOrPharmacistAvailableLocation(String emailAddress,
                                                                                                        String mobileNumber,
                                                                                                        String availableLocation);
}
