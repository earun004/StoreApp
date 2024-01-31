package com.kosuri.stores.handler;

import com.kosuri.stores.dao.PrimaryCareCenterRepository;
import com.kosuri.stores.dao.PrimaryCareEntity;
import com.kosuri.stores.model.request.PrimaryCareUserRequest;
import com.kosuri.stores.model.response.GetAllPrimaryCareCentersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrimaryCareHandler {

@Autowired
private RepositoryHandler  repositoryHandler;

@Autowired
private PrimaryCareCenterRepository primaryCareCenterRepository;

@Autowired
private StoreHandler storeHandler;

        public boolean addPrimaryCare(PrimaryCareUserRequest request) throws Exception {
        if(!repositoryHandler.isPCActive(request)) {
            return false;
        }

        PrimaryCareEntity primaryCareEntity = setEntityFromPrimaryCareRequest(request);
        boolean isPCAdded = false;
            try {
                isPCAdded =  repositoryHandler.addPrimaryCareCenter(primaryCareEntity, request);
            } catch (DataIntegrityViolationException e) {
                throw new Exception(e.getCause().getCause().getMessage());
            }
            return isPCAdded;

    }

    private PrimaryCareEntity setEntityFromPrimaryCareRequest(PrimaryCareUserRequest request) {
        PrimaryCareEntity primaryEntity = new PrimaryCareEntity();
        primaryEntity.setUserServiceId(request.getUserId()+"_"+request.getServiceId());
        primaryEntity.setServiceId(request.getServiceId());
        primaryEntity.setUserId(request.getUserId());
        primaryEntity.setServiceName(request.getServiceName());
        primaryEntity.setServiceCategory(request.getServiceCategory());
        primaryEntity.setDescription(request.getDescription());
        primaryEntity.setUpdatedBy(request.getUpdatedBy());
        primaryEntity.setPrice(request.getPrice());
        return primaryEntity;
    }
    public boolean updatePrimaryCareCenter(PrimaryCareUserRequest request) throws Exception{

        String userServiceId = request.getUserId()+"_"+request.getServiceId();

        // Retrieve the Diagnostic Services Entity
      PrimaryCareEntity serviceEntity= repositoryHandler.findPrimaryServiceById(userServiceId);

        if (serviceEntity == null) {
            throw new Exception("Store not found");
        }

        // Check and update the price, status, and timestamps
        boolean isUpdated = false;
        serviceEntity.setServiceCategory(request.getServiceCategory());
        serviceEntity.setServiceName(request.getServiceName());
        serviceEntity.setDescription(request.getDescription());
        serviceEntity.setUserId(request.getUserId());
        serviceEntity.setUpdatedBy(request.getUserId());
        serviceEntity.setServiceId(request.getServiceId());

        if (request.getPrice() != null && !request.getPrice().equals(serviceEntity.getPrice())) {
            serviceEntity.setPrice(request.getPrice());
            serviceEntity.setAmountUpdatedDate((LocalDateTime.now()));
            isUpdated = true;
        }

        if (!repositoryHandler.isPCActive(request)) {
            serviceEntity.setStatus(repositoryHandler.isPCActive(request)?"1":"0");
            serviceEntity.setStatusUpdatedDate(LocalDateTime.now());
            isUpdated = true;
        }

        // Save the updated entity
        if (isUpdated) {
            repositoryHandler.savePrimaryServiceEntity(serviceEntity);
        }

        return isUpdated;
    }

    public GetAllPrimaryCareCentersResponse getAllPrimaryCareCenters() {
        GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();
        List<PrimaryCareEntity> primaryCareEntities = new ArrayList<>();
        primaryCareCenterRepository.findAll().forEach(primaryCareCenter -> primaryCareEntities.add(primaryCareCenter));
        response.setPrimaryCareCenters(primaryCareEntities);
        return response;

    }

    public GetAllPrimaryCareCentersResponse getPrimaryCareCenterByLocationOrUserId(String location, String userId) {
            GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();

        List<PrimaryCareEntity> primaryCareCenters = new ArrayList<>();

        if (location != null && !location.isEmpty()) {
            List<String> storeIds = storeHandler.getStoreIdFromLocation(location);
            getPrimaryCareCentreUsingStoreIds(storeIds, primaryCareCenters);
            response.setResponseMessage("Diagnostic Centers Fetched Successfully by Location");
        } else if (userId != null && !userId.isEmpty()) {
            primaryCareCenters = primaryCareCenterRepository.findByUserId(userId);
            response.setResponseMessage("Diagnostic Centers Fetched Successfully by User ID");
        } else {
            response.setResponseMessage("No location or user ID provided to fetch Diagnostic Centers");
            return response;
        }
        response.setPrimaryCareCenters(primaryCareCenters);
        return response;
    }

    private void getPrimaryCareCentreUsingStoreIds(List<String> storeIds,
                                                   List<PrimaryCareEntity> primaryCareCenters) {
        for (String storeId: storeIds){
            List<PrimaryCareEntity> primaryCareCenterList = primaryCareCenterRepository.findByStoreId(storeId);
            primaryCareCenters.addAll(primaryCareCenterList);
        }
    }
}


