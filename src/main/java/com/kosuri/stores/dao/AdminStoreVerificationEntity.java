package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin_store_verification")
public class AdminStoreVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Column(name = "verification_id") Integer verificationId;
    private @Column(name = "store_id") String storeId;
    private @Column(name = "store_category") String storeCategory;
    private @Column(name = "doc1") String doc1;
    private @Column(name = "doc2") String doc2;
    private @Column(name = "doc3") String doc3;
    private @Column(name = "doc4") String doc4;
    private @Column(name = "verifiedby") String verifiedBy;
    private @Column(name = "verification_date") LocalDateTime verificationDate;
    private @Column(name = "comment") String comment;
    private @Column(name = "verification_status") String verificationStatus;
    private @Column(name = "user_id") String userId;
}
