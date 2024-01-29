package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "service_category")
public class ServiceCategoryEntity {

	@Id
	@Nonnull
	private @Column(name = "service_id") String serviceId;

	@Nonnull
	private @Column(name = "service_name") String serviceName;

	private @Column(name = "register_date") String registeredDate;

	private @Column(name = "category") String category;

	private @Column(name = "added_by") String addedBy;
	
}
