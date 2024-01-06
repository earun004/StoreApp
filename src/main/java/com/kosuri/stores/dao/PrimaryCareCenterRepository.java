package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrimaryCareCenterRepository extends JpaRepository<PrimaryCareEntity,String> {
    List<PrimaryCareEntity> findByUserId(String userId);

    PrimaryCareEntity findByStoreId(String storeId);
}

