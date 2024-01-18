package com.kosuri.stores.handler;

import com.kosuri.stores.config.AWSConfig;
import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.request.AdminStoreRequest;
import com.kosuri.stores.model.request.CreateStoreRequest;
import com.kosuri.stores.model.request.UpdateStoreRequest;
import com.kosuri.stores.model.response.CreateStoreResponse;
import com.kosuri.stores.model.response.StoreDocumentResponse;
import io.micrometer.common.util.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class StoreHandler {
    @Autowired
    private RepositoryHandler repositoryHandler;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OtpHandler otpHandler;

    @Autowired
    private AWSConfig awsConfig;

    @Autowired
    private TabStoreRepository tabStoreRepository;

    @Autowired
    private S3Client s3Client;

    private static final String BUCKET_NAME = "rxkolan.in";

    

    public String addStore(CreateStoreRequest createStoreRequest) throws Exception{
        if(validateStoreInputs(createStoreRequest)) {

            StoreEntity storeEntity = repositoryHandler.addStoreToRepository(createStoreEntityFromRequest(createStoreRequest));

            if (null != storeEntity){
                otpHandler.sendOtpToEmail(storeEntity.getOwnerEmail(), false, true);
            }
        }
        return createStoreRequest.getId();
    }

    public void uploadFilesAndSaveFileLink(Map<String, MultipartFile> docMap, String storeId) throws APIException {
        if(uploadFileToS3Bucket(docMap)){
               AdminStoreVerificationEntity entity =  createAdminStoreVerificationEntity(docMap.keySet(), storeId);
                repositoryHandler.saveAdminStoreVerificationEntity(entity);
        }
    }

    private AdminStoreVerificationEntity createAdminStoreVerificationEntity(Set<String> filePaths, String storeId) {

        AdminStoreVerificationEntity entity = new AdminStoreVerificationEntity();
        entity.setStoreId(storeId);
        for (String filePath : filePaths) {
            if (filePath != null && (filePath.endsWith(".png")
                    || filePath.endsWith(".jpg")
                    || filePath.endsWith(".jpeg"))) {
                entity.setDoc1(filePath);
            } else if (filePath != null && filePath.endsWith(".pdf")) {
                if (entity.getDoc2() == null) {
                    entity.setDoc2(filePath);
                } else if (entity.getDoc3() == null) {
                    entity.setDoc3(filePath);
                }
            }
        }
        return entity;
    }
    private boolean uploadFileToS3Bucket( Map<String, MultipartFile> docMap) throws APIException {
        for (Map.Entry<String, MultipartFile> entry : docMap.entrySet()) {
            MultipartFile file = entry.getValue();
            String fullPath = entry.getKey();
            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(fullPath)
                        .contentType(file.getContentType())
                        .build();


                RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
                S3Client s3Client = awsConfig.s3Client();
                s3Client.putObject(putObjectRequest, requestBody);
            } catch (Exception e) {
                throw new APIException("Failed to upload file: ");
            }
        }
        return true;
    }

    public void getStoreFilesByStoreId(String storeId){
      AdminStoreVerificationEntity adminStoreVerificationEntity = repositoryHandler.getAdminStoreVerification(storeId);

    }


    public String updateStore(UpdateStoreRequest updateStoreRequest) throws Exception {
        if(validateUpdateStoreInputs(updateStoreRequest)) {
            StoreEntity storeEntity = repositoryHandler.updateStore(updateStoreEntityFromRequest(updateStoreRequest));
        }
        return updateStoreRequest.getId();
    }

    public List<StoreEntity> getStoreIdFromStoreOwner(String emailId) {
        Optional<List<StoreEntity>> entity = storeRepository.findByOwnerEmail(emailId);
        return entity.orElse(null);
    }

    public List<String> getStoreIdFromLocation(String location) {
        Optional<List<StoreEntity>> entity = storeRepository.findByLocation(location);
        List<String> stores = new ArrayList<>();
        if (entity.isPresent()) {
            for (StoreEntity store: entity.get()) {
                stores.add(store.getId());
            }
        }
        return stores;
    }

    public List<StoreEntity> getAllStores() throws Exception{
        List<StoreEntity> storeEntities = repositoryHandler.getAllStores();

        List<StoreEntity> stores = new ArrayList<>();
        for(StoreEntity store: storeEntities){
            if(store.getId() != "DUMMY"){
                stores.add(store);
            }
        }
        return stores;
    }

    private StoreEntity createStoreEntityFromRequest(CreateStoreRequest createStoreRequest){

        LocalDate currentDate = LocalDate.now();



        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setName(createStoreRequest.getName());
        storeEntity.setId(createStoreRequest.getId());
        storeEntity.setType(createStoreRequest.getStoreType());
        storeEntity.setPincode(createStoreRequest.getPincode());
        storeEntity.setDistrict(createStoreRequest.getDistrict());
        storeEntity.setState(createStoreRequest.getState());
        storeEntity.setOwner(createStoreRequest.getOwner());
        storeEntity.setOwnerEmail(createStoreRequest.getOwnerEmail());
        storeEntity.setOwnerContact(createStoreRequest.getOwnerContact());
        storeEntity.setSecondaryContact(createStoreRequest.getSecondaryContact());
        storeEntity.setRegistrationDate(LocalDate.now().toString());
        storeEntity.setCreationTimeStamp(LocalDateTime.now().toString());
        storeEntity.setModifiedBy("test_user");
        storeEntity.setModifiedDate(LocalDate.now().toString());
        storeEntity.setModifiedTimeStamp(LocalDateTime.now().toString());
        try {
            setExpirationDateForStore(storeEntity, createStoreRequest.getStoreType());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String expirationDateString = createStoreRequest.getExpirationDate();
            LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
            if (!expirationDateString.isEmpty()) {
                storeEntity.setStatus(expirationDate.isAfter(currentDate) ? "Active" : "Inactive");
                storeEntity.setExpiryDate(expirationDate.format(formatter));
            } else {
                storeEntity.setStatus("Inactive");
                storeEntity.setExpiryDate(null);
            }
        } catch (DateTimeParseException e) {
            storeEntity.setStatus("Inactive");
            storeEntity.setExpiryDate(null);
        }
        storeEntity.setAddedBy(createStoreRequest.getOwner());
        storeEntity.setLocation(createStoreRequest.getLocation());
        storeEntity.setStoreVerifiedStatus(createStoreRequest.getStoreVerificationStatus());

        return storeEntity;
    }

    private void setExpirationDateForStore(StoreEntity storeEntity, String storeCategory) {
        AdminStoreMembershipEntity adminStoreMembershipEntity = repositoryHandler.
                getStoreVerificationDetails(storeCategory);
        if (adminStoreMembershipEntity != null && !StringUtils.isEmpty(storeEntity.getRegistrationDate())) {
            int noOfDays = Integer.parseInt(adminStoreMembershipEntity.getNoOfDays());
            LocalDate registrationDate = LocalDate.parse(storeEntity.getRegistrationDate());
            LocalDate expirationDate = registrationDate.plusDays(noOfDays);
            storeEntity.setExpiryDate(expirationDate.toString());
        }
    }

    private StoreEntity updateStoreEntityFromRequest(UpdateStoreRequest request){
        //TODO add location and other fields from request instead of default values.
        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setName(request.getName());
        storeEntity.setId(request.getId());
        storeEntity.setType(request.getStoreType());
        storeEntity.setPincode(request.getPincode());
        storeEntity.setPincode(request.getPincode());
        storeEntity.setDistrict(request.getDistrict());
        storeEntity.setState(request.getState());
        storeEntity.setOwner(request.getOwner());
        storeEntity.setOwnerEmail(request.getOwnerEmail());
        storeEntity.setOwnerContact(request.getOwnerContact());
        storeEntity.setSecondaryContact(request.getSecondaryContact());
        storeEntity.setRegistrationDate(LocalDate.now().toString());
        storeEntity.setCreationTimeStamp(LocalDateTime.now().toString());
        storeEntity.setRole("test");
        storeEntity.setModifiedBy("test_user");
        storeEntity.setModifiedDate(LocalDate.now().toString());
        storeEntity.setModifiedTimeStamp(LocalDateTime.now().toString());
        storeEntity.setStatus(request.getStatus());
        storeEntity.setAddedBy(request.getOwner());
        storeEntity.setLocation(request.getLocation());

        return storeEntity;
    }

    boolean validateStoreInputs(CreateStoreRequest request) throws Exception{
        boolean isStorePresent = repositoryHandler.isStorePresent(request);
        if(isStorePresent){
           throw new APIException("Store Is Already Present In System");
        }

        if(request.getOwnerEmail() != null && !request.getOwnerEmail().isEmpty() &&
                request.getOwnerContact() != null && !request.getOwnerContact().isEmpty() &&
                request.getExpirationDate()!=null && !request.getExpirationDate().isEmpty()){
            boolean isOwnerPresent = repositoryHandler.isOwnerPresent(request.getOwnerEmail(), request.getOwnerContact());
            if (!isOwnerPresent){
                throw new APIException("Owner Not Found");
            }
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String expirationDateString = request.getExpirationDate();
            LocalDate expirationDate = LocalDate.parse(expirationDateString,formatter);
            if (!expirationDate.isAfter(currentDate)) {
                throw new APIException("Cannot Add Store As Store Licence Expired.");
            }

        }
        return true;
    }

    boolean validateUpdateStoreInputs(UpdateStoreRequest request) throws Exception{
        Optional<StoreEntity> store = storeRepository.findById(request.getId());
        if(!store.isPresent()){
            throw new APIException("Store with id not found");
        }

        if(request.getOwnerEmail() != null && !request.getOwnerEmail().isEmpty() && request.getOwnerContact() != null && !request.getOwnerContact().isEmpty()){
        Optional<List<StoreEntity>> store2 = storeRepository.findByOwnerEmailOrOwnerContact(request.getOwnerEmail(), request.getOwnerContact());
            if(!store2.get().isEmpty()){
                for(StoreEntity s: store2.get()){
                    if(!s.getId().contains("DUMMY") && s.getId() != request.getId()){
                        throw new APIException("Store with owner email/contact is already present in system");
                    }
                }
            }
            boolean isUserPresent = false;
            if(!store2.get().isEmpty()){
                for(StoreEntity s: store2.get()){
                    if (s.getId().contains("DUMMY") && s.getRole().equals("STORE_MANAGER")) {
                        isUserPresent = true;
                        break;
                    }
                }
            }

            if(!isUserPresent){
                throw new APIException("Store owner not present as user in system");

            }
        }
        return true;
    }


    public byte[] downloadStoreDocs(String storeId) throws APIException, IOException {
        StoreDocumentResponse storeDocumentResponse = new StoreDocumentResponse();
        AdminStoreVerificationEntity adminStoreVerificationEntity = repositoryHandler.getAdminStoreVerification(storeId);
        List<String> storeDocFileList = new ArrayList<>();
        if (adminStoreVerificationEntity != null){
          storeDocFileList.add(adminStoreVerificationEntity.getDoc1());
          storeDocFileList.add(adminStoreVerificationEntity.getDoc2());
          storeDocFileList.add(adminStoreVerificationEntity.getDoc3());
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (String fileName : storeDocFileList) {
                if (fileName != null && !fileName.isEmpty()) {
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(fileName)
                            .build();

                    // Use try-with-resources to ensure the stream is closed
                    try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                        ZipEntry zipEntry = new ZipEntry(fileName);
                        zos.putNextEntry(zipEntry);
                        IOUtils.copy(s3Object, zos); // Use IOUtils from Apache Commons IO
                        zos.closeEntry();
                    } catch (Exception e) {
                        // Log and handle exception...
                        throw new APIException("Error While Downloading File: " + fileName);
                    }
                }
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            // Handle exception...
            throw new APIException("Error creating zip file.");
        }
    }

    public List<StoreEntity> searchStores(String location, String userId, String storeType, String addedDate) throws APIException{

        List<StoreEntity> stores = new ArrayList<>();

        if (userId != null && !userId.isEmpty()) {
            Optional<TabStoreUserEntity> storeUser = tabStoreRepository.findById(userId);

            if (storeUser.isPresent()) {

                String ownerEmail = storeUser.get().getStoreUserEmail();
                if (storeType != null && !storeType.isEmpty()) {
                    stores = storeRepository.findByOwnerEmailAndType(ownerEmail, storeType);
                } else {
                    stores = storeRepository.findByOwnerEmail(ownerEmail).orElse(new ArrayList<>());
                }
            }
        } else if (location != null && !location.isEmpty()) {
            if (storeType != null && !storeType.isEmpty()) {
                stores = storeRepository.findByLocationAndType(location, storeType);
            } else {
                stores = storeRepository.findByLocation(location).orElse(new ArrayList<>());
            }
        } else if (addedDate != null && !addedDate.isEmpty()) {

            stores = storeRepository.findByRegistrationDate(addedDate);
        } else {
            throw new APIException("No stores found.");
        }

        return stores;

    }

    public CreateStoreResponse updateStoreDocumentVerification(AdminStoreRequest adminStoreRequest) throws APIException{
        CreateStoreResponse response = new CreateStoreResponse();
        AdminStoreVerificationEntity storeVerificationEntity = repositoryHandler.getAdminStoreVerification(adminStoreRequest.getStoreId());
        if (storeVerificationEntity != null) {

            storeVerificationEntity.setVerificationStatus(adminStoreRequest.getIsStoreValid() ? "Verified" : "Rejected");
            storeVerificationEntity.setComment(adminStoreRequest.getComments());
            storeVerificationEntity.setVerifiedBy(adminStoreRequest.getVerifiedBy());
            storeVerificationEntity.setVerificationDate(LocalDateTime.now());
            if(repositoryHandler.saveAdminStoreVerificationEntity(storeVerificationEntity)){

                response.setId(storeVerificationEntity.getStoreId());
                response.setResponseMessage("Store verification status updated successfully.");
                return response;
            }
        } else {
            throw new APIException("Store with ID: " + adminStoreRequest.getStoreId() + " not found.");
        }
        return response;
    }
}
