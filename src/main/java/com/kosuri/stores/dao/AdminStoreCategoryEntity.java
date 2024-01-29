package com.kosuri.stores.dao;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@ToString
@Table(name = "admin_store_categories")
public class AdminStoreCategoryEntity {
    @Id
    private @Column(name = "Store_Category_ID") String storeCategoryId;
    private @Column(name = "Store_Category_Name") String storeCategoryName;
    private @Column(name = "updated_by") String updatedBy;
    private @Column(name = "updated_date") LocalDateTime updatedDate;
    private @Column(name = "status") String status;
    private @Column(name = "status_updated_date") String statusUpdatedDate;
}
