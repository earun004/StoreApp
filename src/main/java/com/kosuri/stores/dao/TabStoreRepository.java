package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TabStoreRepository extends JpaRepository<TabStoreUserEntity, String> {


    Optional<TabStoreUserEntity> findByStoreUserEmailOrStoreUserContact(String storeUserEmail, String storeUserContact);

    Optional<TabStoreUserEntity> findByStoreUserEmailAndStoreUserContact(String email, String phoneNumber);
}
