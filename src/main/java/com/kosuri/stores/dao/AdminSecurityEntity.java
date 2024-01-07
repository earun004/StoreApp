package com.kosuri.stores.dao;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin_security")
public class AdminSecurityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Column(name = "admin_id") Integer id;

    private @Column(name = "access_key", unique = true)  String awsAccessKey;
    private @Column(name = "secret_key", unique = true) String awsSecretKey;
}
