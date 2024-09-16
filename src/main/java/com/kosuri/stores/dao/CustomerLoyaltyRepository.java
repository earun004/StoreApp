package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerLoyaltyRepository extends JpaRepository<CustomerLoyaltyEntity, Integer> {
    @Query("SELECT t1 FROM CustomerLoyaltyEntity t1 " +
    "JOIN (SELECT t2.customerPhone AS customerPhone, t2.customerName AS customerName, MAX(t2.discountedDate) AS maxDate " +
          "FROM CustomerLoyaltyEntity t2 " +
          "WHERE t2.customerName = ?1 OR t2.customerPhone = ?2 AND t2.storeId = ?3 " +
          "GROUP BY t2.customerPhone, t2.customerName) t3 " +
    "ON t1.customerPhone = t3.customerPhone " +
    "AND t1.customerName = t3.customerName " +
    "AND t1.discountedDate = t3.maxDate " +
    "ORDER BY t1.customerPhone ASC, t1.customerName ASC")
    Optional<List<CustomerLoyaltyEntity>> findByCustomerNameOrCustomerPhoneAndStoreIdAndFirstByOrderByDiscountedDateDsc(String customerName, String customerPhone, String storeId);

    @Query("select e from CustomerLoyaltyEntity e where e.customerName = ?1 AND e.customerPhone = ?2 and e.storeId = ?3 order by e.discountedDate limit 1")
    Optional<List<CustomerLoyaltyEntity>> findByCustomerNameAndCustomerPhoneAndStoreIdAndFirstByOrderByDiscountedDateDsc(String customerName, String customerPhone, String storeId);
}
