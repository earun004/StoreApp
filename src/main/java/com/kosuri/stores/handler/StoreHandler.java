package com.kosuri.stores.handler;

import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.request.CreateStoreRequest;
import com.kosuri.stores.model.request.UpdateStoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreHandler {
    @Autowired
    private RepositoryHandler repositoryHandler;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OtpHandler otpHandler;

    private final S3Client s3Client;


    public StoreHandler(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String addStore(CreateStoreRequest createStoreRequest) throws Exception{
        if(validateStoreInputs(createStoreRequest)) {
            //uploadFileToS3Bucket(file);
            StoreEntity storeEntity = repositoryHandler.addStoreToRepository(createStoreEntityFromRequest(createStoreRequest));

            if (null != storeEntity){
                otpHandler.sendOtpToEmail(storeEntity.getOwnerEmail(), false, true);
            }
        }
        return createStoreRequest.getId();
    }

    private void uploadFileToS3Bucket(MultipartFile file) {

        String bucketName = "rxkolan.in";
        String key = "uploads/" + file.getOriginalFilename(); // or any other key structure you prefer

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

            s3Client.putObject(putObjectRequest, requestBody);


        } catch (Exception e) {
            e.printStackTrace();

        }
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
}
