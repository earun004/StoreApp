package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, String> {


    Optional<List<StoreEntity>> findByOwnerEmail(String ownerEmail);

    Optional<List<StoreEntity>> findByOwnerEmailOrOwnerContact(String ownerEmail, String ownerContact);

    Optional<List<StoreEntity>> findByLocation(String location);


    Optional<StoreEntity> findByPincodeAndDistrictAndStateAndLocation(String pincode, String district, String state, String location);

    Optional<StoreEntity> findById(String storeId);

    List<StoreEntity> findByLocationAndType(String location, String type);
    List<StoreEntity> findByOwnerEmailAndType(String ownerEmail, String type);
    List<StoreEntity> findByRegistrationDate(String registrationDate);

    Optional<StoreEntity> findByIdAndStoreBusinessType(String storeId, String businessType);
}


