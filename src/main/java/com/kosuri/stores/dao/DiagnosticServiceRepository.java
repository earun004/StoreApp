package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiagnosticServiceRepository extends JpaRepository<DiagnosticServicesEntity, String> {
    List<DiagnosticServicesEntity> findByUserId(String userId);

    List<DiagnosticServicesEntity> findByStoreId(String storeId);
}
