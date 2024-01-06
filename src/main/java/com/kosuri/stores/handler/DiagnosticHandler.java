package com.kosuri.stores.handler;


import com.kosuri.stores.dao.DiagnosticServiceRepository;
import com.kosuri.stores.dao.DiagnosticServicesEntity;
import com.kosuri.stores.model.request.DiagnosticCenterRequest;
import com.kosuri.stores.model.response.GetAllDiagnosticCentersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticHandler {

    @Autowired
    private RepositoryHandler repositoryHandler;

    @Autowired
    private DiagnosticServiceRepository diagnosticServiceRepository;

    @Autowired
    private StoreHandler storeHandler;

    public boolean addDiagnosticCenter(DiagnosticCenterRequest request) throws Exception {
        if (!repositoryHandler.isDCActive(request)){
            return false;
        }

        DiagnosticServicesEntity diagnosticServicesEntity = getDiagnosticServicesEntityFromDiagnosticRequest(request, true);
        boolean isDcAdded = false;
        try {
           isDcAdded =  repositoryHandler.addDiagnosticCenter(diagnosticServicesEntity,request);
        } catch (DataIntegrityViolationException e) {
            throw new Exception(e.getCause().getCause().getMessage());
        }
        return isDcAdded;
    }

    private DiagnosticServicesEntity getDiagnosticServicesEntityFromDiagnosticRequest(DiagnosticCenterRequest request, boolean isDCActive) {

        DiagnosticServicesEntity diagnosticServicesEntity = new DiagnosticServicesEntity();

        diagnosticServicesEntity.setServiceCategory(request.getServiceCategory());
        diagnosticServicesEntity.setServiceName(request.getServiceName());
        diagnosticServicesEntity.setPrice(request.getPrice());
        diagnosticServicesEntity.setUserId(request.getUserId());
        diagnosticServicesEntity.setDescription(request.getDescription());
        diagnosticServicesEntity.setUpdatedBy(request.getUserId());
        diagnosticServicesEntity.setStoreId(request.getStoreId());
        diagnosticServicesEntity.setServiceId(request.getServiceId());
        diagnosticServicesEntity.setUserServiceId(request.getUserId()+"_"+request.getServiceId());
        diagnosticServicesEntity.setStatus(isDCActive?"1":"0");
        diagnosticServicesEntity.setStatusUpdatedDate(LocalDateTime.now().toString());
        diagnosticServicesEntity.setAmountUpdatedDate((LocalDateTime.now().toString()));

        return diagnosticServicesEntity;
    }

    public boolean updateDiagnosticCenter(DiagnosticCenterRequest request) throws Exception{

        String userServiceId = request.getUserId()+"_"+request.getServiceId();

        // Retrieve the Diagnostic Services Entity
        DiagnosticServicesEntity serviceEntity = repositoryHandler.findServiceById(userServiceId);

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
        serviceEntity.setStoreId(request.getStoreId());

        if (request.getPrice() != null && !request.getPrice().equals(serviceEntity.getPrice())) {
            serviceEntity.setPrice(request.getPrice());
            serviceEntity.setAmountUpdatedDate((LocalDateTime.now().toString()));
            isUpdated = true;
        }

        if (!repositoryHandler.isDCActive(request)) {
            serviceEntity.setStatus(repositoryHandler.isDCActive(request)?"1":"0");
            serviceEntity.setStatusUpdatedDate(LocalDateTime.now().toString());
            isUpdated = true;
        }

        // Save the updated entity
        if (isUpdated) {
            repositoryHandler.saveDiagnosticServiceEntity(serviceEntity);
        }

        return isUpdated;
    }

    public GetAllDiagnosticCentersResponse getAllDiagnosticCenters() {
        GetAllDiagnosticCentersResponse response = new GetAllDiagnosticCentersResponse();
        List<DiagnosticServicesEntity> diagnosticServices = new ArrayList<>();
        diagnosticServiceRepository.findAll().forEach(diagnosticCenter -> diagnosticServices.add(diagnosticCenter));
        response.setResponseMessage("Diagnostic Centers Fetched SuccessFully");
        response.setDiagnosticCenters(diagnosticServices);
        return response;
    }
    public GetAllDiagnosticCentersResponse getDiagnosticCenterByLocationOrUserId(String location, String userId) {
        GetAllDiagnosticCentersResponse response = new GetAllDiagnosticCentersResponse();
        List<DiagnosticServicesEntity> diagnosticCenters = new ArrayList<>();

        if (location != null && !location.isEmpty()) {
            List<String> storeIds = storeHandler.getStoreIdFromLocation(location);
            getDiagnosticServicesEntityUsingStoreIds(storeIds, diagnosticCenters);
            response.setResponseMessage("Diagnostic Centers Fetched Successfully by Location");
        } else if (userId != null && !userId.isEmpty()) {
            diagnosticCenters = diagnosticServiceRepository.findByUserId(userId);
            response.setResponseMessage("Diagnostic Centers Fetched Successfully by User ID");
        } else {
            response.setResponseMessage("No location or user ID provided to fetch Diagnostic Centers");
            return response;
        }
        response.setDiagnosticCenters(diagnosticCenters);
        return response;
    }

    private void getDiagnosticServicesEntityUsingStoreIds(List<String> storeIds, List<DiagnosticServicesEntity> diagnosticCenters) {
        for (String storeId: storeIds){
            DiagnosticServicesEntity diagnosticCenter = diagnosticServiceRepository.findByStoreId(storeId);
            diagnosticCenters.add(diagnosticCenter);
        }

    }



}
