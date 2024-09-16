package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin_store_membersip")
public class AdminStoreMembershipEntity {

    @Id
    @Nonnull
    private @Column(name = "plan_id") String planId;
    private @Column(name = "price_per_user") Integer pricePerUser;
    private @Column(name = "store_category", unique = true) String storeCategory;
    private @Column(name = "updated_by") String updatedBy;
    private @Column(name = "state") String state;
    private @Column(name = "district") String district;
    private @Column(name = "no_of_days") String noOfDays;
    private @Column(name = "comment") String comment;
    private @Column(name = "status") String status;
    private @Column(name = "status_update_date") LocalDateTime statusUpdateDate;
    private @Column(name = "price_updated_date") LocalDateTime priceUpdateDate;
    private @Column(name = "planid_storecategory") String planIdStoreCategory;
}
