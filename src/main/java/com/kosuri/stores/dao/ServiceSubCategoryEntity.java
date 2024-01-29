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
@Table(name = "service_sub_category")
public class ServiceSubCategoryEntity {
	@Id
	@Nonnull
	private @Column(name = "sub_category_id") String subCategoryId;
	private @Column(name = "service_name") String service;
	private @Column(name = "service_status") String serviceStatus;
	private @Column(name = "service_description") String serviceDescription;
	private @Column(name = "service_updated_by") String serviceUpdatedBy;
	
}
