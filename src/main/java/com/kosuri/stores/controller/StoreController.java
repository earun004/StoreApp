package com.kosuri.stores.controller;

import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.dao.AdminStoreCategoryEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.RepositoryHandler;
import com.kosuri.stores.handler.StoreHandler;
import com.kosuri.stores.model.request.AdminStoreRequest;
import com.kosuri.stores.model.request.CreateStoreRequest;
import com.kosuri.stores.model.request.UpdateStoreRequest;
import com.kosuri.stores.model.response.CreateStoreResponse;
import com.kosuri.stores.model.response.GetStoreRelatedResponse;
import com.kosuri.stores.model.response.UpdateStoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private StoreHandler storeHandler;

    @Autowired
    private RepositoryHandler repositoryHandler;


    @PostMapping("/create")
    ResponseEntity<CreateStoreResponse> createStore(@RequestBody CreateStoreRequest request) {
        CreateStoreResponse createStoreResponse = new CreateStoreResponse();
        HttpStatus httpStatus;
        try {
            createStoreResponse.setId(storeHandler.addStore(request));
            createStoreResponse.setResponseMessage("Store Added successfully!");
            httpStatus = HttpStatus.OK;
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            createStoreResponse.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            createStoreResponse.setResponseMessage(e.getMessage());
        }
        return ResponseEntity.status(httpStatus).body(createStoreResponse);
    }

    @PostMapping("/upload")
    ResponseEntity<CreateStoreResponse> uploadStoreDocs(
            @RequestParam("storeId") String storeId,
            @RequestParam("storeFrontImage") MultipartFile storeFrontImage,
            @RequestParam("tradeLicense") MultipartFile tradeLicense,
            @RequestParam("drugLicense") MultipartFile drugLicense) {

        CreateStoreResponse createStoreResponse = new CreateStoreResponse();


        try {
            if (repositoryHandler.isStorePresent(storeId)){
                String directoryPath = storeId + "/";
                Map<String, MultipartFile> docMap = new HashMap<>();
                String storeFrontImagePath = directoryPath + storeFrontImage.getOriginalFilename();
                String tradeLicensePath = directoryPath + tradeLicense.getOriginalFilename();
                String drugLicensePath = directoryPath + drugLicense.getOriginalFilename();
                docMap.put(storeFrontImagePath, storeFrontImage);
                docMap.put(tradeLicensePath, tradeLicense);
                docMap.put(drugLicensePath, drugLicense);

                storeHandler.uploadFilesAndSaveFileLink(docMap, storeId);
                createStoreResponse.setResponseMessage("Store documents uploaded successfully for StoreId: " + storeId);
            } else{
                throw new APIException("Store Not Present");
            }

            return ResponseEntity.ok(createStoreResponse);
        } catch (APIException e) {
            createStoreResponse.setResponseMessage("Error for StoreId " + storeId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createStoreResponse);
        } catch (Exception e) {
            createStoreResponse.setResponseMessage("Internal error for StoreId " + storeId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createStoreResponse);
        }
    }

    @GetMapping("/downloadFiles")
    private ResponseEntity<byte[]> downloadStoreDocs(
            @RequestParam("storeId") String storeId) {


        try {
            if (repositoryHandler.isStorePresent(storeId)){
                byte[] zipBytes =  storeHandler.downloadStoreDocs(storeId);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/zip"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storeId + "_documents.zip\"")
                        .body(zipBytes);
            } else{
                throw new APIException("Store Not Present");
            }

        } catch (APIException e) {
            String errorMessage = "Error for StoreId " + storeId + ": " + e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            String errorMessage = "Internal error for StoreId " + storeId + ": " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage.getBytes(StandardCharsets.UTF_8));
        }
    }

    @PutMapping("/updateStoreDocVerification")
    ResponseEntity<CreateStoreResponse> updateStoreVerification(@RequestBody AdminStoreRequest adminStoreRequest) {
        CreateStoreResponse createStoreResponse = new CreateStoreResponse();
        try {
            if (repositoryHandler.isStorePresent(adminStoreRequest.getStoreId())){
                createStoreResponse = storeHandler.updateStoreDocumentVerification(adminStoreRequest);
                createStoreResponse.setResponseMessage("Store documents Verification successfully for StoreId: " + adminStoreRequest.getStoreId());
            } else{
                throw new APIException("Store Not Present");
            }

            return ResponseEntity.ok(createStoreResponse);
        } catch (APIException e) {
            createStoreResponse.setResponseMessage("Error for StoreId " + adminStoreRequest.getStoreId() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createStoreResponse);
        } catch (Exception e) {
            createStoreResponse.setResponseMessage("Internal error for StoreId " + adminStoreRequest.getStoreId() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createStoreResponse);
        }
    }

    @PostMapping("/update")
    ResponseEntity<UpdateStoreResponse> updateStore(@RequestBody UpdateStoreRequest request) {
        HttpStatus httpStatus;
        UpdateStoreResponse updateStoreResponse = new UpdateStoreResponse();

        try {
            String storeId = storeHandler.updateStore(request);
            httpStatus = HttpStatus.OK;
            updateStoreResponse.setId(storeId);
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            updateStoreResponse.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            updateStoreResponse.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(updateStoreResponse);
    }

    @GetMapping("/all")
    ResponseEntity<GetStoreRelatedResponse> getAllStores() {
        HttpStatus httpStatus;
        GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();

        try{
            List<StoreEntity> stores = storeHandler.getAllStores();
            getStoreRelatedResponse.setStores(stores);
            httpStatus = HttpStatus.OK;
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        }
        return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
    }

    @GetMapping("/storeDetails")
    ResponseEntity<GetStoreRelatedResponse> getAllStoresByUserId(@RequestParam(value = "location", required = false) String location,
                                                                 @RequestParam(value = "userId", required = false) String userId,
                                                                 @RequestParam(value = "store_type", required = false) String storeType,
                                                                 @RequestParam(value = "added_date", required = false) String addedDate) throws APIException{
        HttpStatus httpStatus;
        GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();

        try{
            List<StoreEntity> stores = storeHandler.searchStores(location, userId, storeType, addedDate);
            getStoreRelatedResponse.setStores(stores);
            httpStatus = HttpStatus.OK;
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        }
        return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
    }


    @GetMapping("/getAllStoreBusinessTypes")
    ResponseEntity<GetStoreRelatedResponse> getAllStoresBusinessTypes() throws APIException{
        HttpStatus httpStatus;
        GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();
        try{
            List<AdminStoreBusinessTypeEntity> storeBusinessTypeList = storeHandler.getAllStoreBusinessTypes();
            getStoreRelatedResponse.setStoreBusinessTypeList(storeBusinessTypeList);
            httpStatus = HttpStatus.OK;
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        }
        return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
    }

    @GetMapping("/getAllStoreCategories")
    ResponseEntity<GetStoreRelatedResponse> getAllStoresCategories() throws APIException{
        HttpStatus httpStatus;
        GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();
        try{
            List<AdminStoreCategoryEntity> storeCategoriesList = storeHandler.getAllStoreCategories();
            getStoreRelatedResponse.setStoreCategoriesList(storeCategoriesList);
            httpStatus = HttpStatus.OK;
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            getStoreRelatedResponse.setResponseMessage(e.getMessage());
        }
        return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
    }
}
