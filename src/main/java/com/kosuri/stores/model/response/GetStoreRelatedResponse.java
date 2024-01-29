package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.dao.AdminStoreCategoryEntity;
import com.kosuri.stores.dao.StoreEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class GetStoreRelatedResponse extends GenericResponse{
    private List<StoreEntity> stores;

    private List<AdminStoreBusinessTypeEntity> storeBusinessTypeList;

    private List<AdminStoreCategoryEntity> storeCategoriesList;
}
