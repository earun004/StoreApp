package com.kosuri.stores.handler;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.UserType;
import com.kosuri.stores.model.request.*;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginUserResponse;

import io.micrometer.common.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserHandler {
    @Autowired
    private RepositoryHandler repositoryHandler;

    @Autowired
    private StoreHandler storeHandler;

    @Autowired
    private RoleHandler roleHandler;


    public boolean addUser(AddUserRequest request) throws Exception {
        if(!repositoryHandler.validateUser(request)){
            return false;
        }
        StoreEntity userStoreEntity = getEntityFromUserRequest(request);
        try {
            repositoryHandler.addUser(userStoreEntity, request);
        } catch (DataIntegrityViolationException e) {
            throw new Exception(e.getCause().getCause().getMessage());
        }
        return true;
    }
    public boolean addUser(AddTabStoreUserRequest request) throws Exception {
        if (!repositoryHandler.validateStoreUser(request)) {
            return false;
        }
        TabStoreUserEntity userStoreEntity = getEntityFromStoreUserRequest(request);
        boolean isUserAdded;
        try {
            isUserAdded = repositoryHandler.addStoreUser(userStoreEntity, request);

        } catch (DataIntegrityViolationException e) {
            throw new Exception(e.getCause().getCause().getMessage());
        }
        return isUserAdded;
    }

    private TabStoreUserEntity getEntityFromStoreUserRequest(AddTabStoreUserRequest request) {
        TabStoreUserEntity storeEntity = new TabStoreUserEntity();
        
        storeEntity.setStatus(request.getStatus());
        storeEntity.setName(request.getUserFullName());
        storeEntity.setStoreUserEmail(request.getUserEmail());
        storeEntity.setStoreUserContact(request.getUserPhoneNumber());
        storeEntity.setType(request.getStore());
        storeEntity.setStoreAdminContact(request.getStoreAdminMobile());
        storeEntity.setStoreAdminEmail(request.getStoreAdminEmail());
        storeEntity.setPassword(getEncryptedPassword(request.getPassword()));
        storeEntity.setUserType((null!=request.getUserType())?
                request.getUserType(): UserType.SA.toString());
        storeEntity.setRegistrationDate(LocalDateTime.now());
        storeEntity.setUserId(genereateUserId());

        storeEntity.setAddedBy("admin");

        return storeEntity;
    }

    public GenericResponse changePassword(PasswordRequest request,boolean isForgetPassword) throws Exception {
        TabStoreUserEntity tabStoreUserEntity = repositoryHandler.getTabStoreUser(request.getEmailAddress(),request.getUserContactNumber());
        GenericResponse response = new GenericResponse();
        if (tabStoreUserEntity != null && request.getPassword().equals(request.getConfirmPassword()) ){
            boolean isPasswordNotSame= checkPassword(request.getPassword(), tabStoreUserEntity.getPassword());

            if(isPasswordNotSame) {
                if (isForgetPassword){
                    updatePassword(request, tabStoreUserEntity, response);
                }else{
                    response.setResponseMessage("Password is same. Please set a new Password");
                }
            }else{
                updatePassword(request, tabStoreUserEntity, response);
            }
            return response;
        }
        return response;
    }

    private void updatePassword(PasswordRequest request, TabStoreUserEntity tabStoreUserEntity, GenericResponse response) {
        tabStoreUserEntity.setPassword(getEncryptedPassword(request.getPassword()));
        boolean isPasswordUpdated = repositoryHandler.updatePassword(tabStoreUserEntity);
        if (isPasswordUpdated) {
            response.setResponseMessage("Password Updated Successfully");
        }else{
            response.setResponseMessage("Unable To Update Password.");
        }
    }

    public GenericResponse forgetPassword(PasswordRequest request) throws Exception{
        GenericResponse response = new GenericResponse();
        if (!StringUtils.isEmpty(request.getEmailAddress())
                || !StringUtils.isEmpty(request.getUserContactNumber())){
            TabStoreUserEntity tabStoreUserEntity =
                    repositoryHandler.getTabStoreUser(request.getEmailAddress(),request.getUserContactNumber());
            OTPRequest otpRequest = new OTPRequest();
            if (null != tabStoreUserEntity) {
                otpRequest.setIsForgetPassword(true);
               sendOTP(request, otpRequest);
               response.setResponseMessage("Forget Password Initiated");
            } else{
                response.setResponseMessage("Unable to Initiated Forget Password.");
            }
        }
        return response;
    }

    public GenericResponse verifyOTPAndChangePassword(PasswordRequest request) throws Exception{
        GenericResponse genericResponse = new GenericResponse();
        if (!StringUtils.isEmpty(request.getEmailAddress())
                || !StringUtils.isEmpty(request.getUserContactNumber())){
            boolean isOtpVerified = false;
            VerifyOTPRequest verifyOTPRequest =  new VerifyOTPRequest();
            verifyOTPRequest.setOtp(request.getOtp());
            verifyOTPRequest.setIsForgetPassword(true);
                if(null != request.getEmailAddress()) {
                    verifyOTPRequest.setEmail(request.getEmailAddress());
                    isOtpVerified = verifyEmailOTP(verifyOTPRequest);
                }else if (!StringUtils.isEmpty(request.getUserContactNumber())){
                    verifyOTPRequest.setPhoneNumber(request.getUserContactNumber());
                    isOtpVerified = verifySmsOTP(verifyOTPRequest);
                }
             if (isOtpVerified){
                 genericResponse =  changePassword(request,true);
             }
        }
        return genericResponse;
    }

    private boolean sendOTP(PasswordRequest request, OTPRequest otpRequest) {

        if (null != request.getEmailAddress()) {
            otpRequest.setEmail(request.getEmailAddress());
            return sendEmailOtp(otpRequest);
        } else {
            otpRequest.setPhoneNumber(request.getUserContactNumber());
           return  sendOTPToPhone(otpRequest);
        }
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
    }


    private String getEncryptedPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private String genereateUserId() {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestampStr = timestamp.format(formatter);
        return StoreConstants.RX_CONSTANT+"_"+timestampStr+"_"+OtpHandler.generateOTP(false);
    }

    public LoginUserResponse loginUser(LoginUserRequest request) throws Exception {
        LoginUserResponse response = new LoginUserResponse();

        if ((request.getEmail() == null || request.getEmail().isEmpty()) &&
                (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
            throw new APIException("Either email or phone number must be provided, both can't be null/empty");
        }
        TabStoreUserEntity tabStoreUserEntity = repositoryHandler.loginUser(request);
        if (null != tabStoreUserEntity) {
            response.setUserId(tabStoreUserEntity.getUserId());
            response.setUserFullName(tabStoreUserEntity.getName());
            response.setUserType(tabStoreUserEntity.getUserType());
            response.setUserEmailAddress(tabStoreUserEntity.getStoreUserEmail());
            response.setUserContact(tabStoreUserEntity.getStoreUserContact());
        }
        return response;
    }



    private StoreEntity getEntityFromUserRequest(AddUserRequest request){
        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setOwner(request.getName());
        storeEntity.setOwnerContact(request.getPhoneNumber());
        storeEntity.setOwnerEmail(request.getEmail());
        storeEntity.setLocation(request.getAddress());
        storeEntity.setRole(request.getRole());
        storeEntity.setCreationTimeStamp(LocalDateTime.now().toString());

        //setting dummy parameters.
        if(request.getPhoneNumber() != null){
            storeEntity.setId("DUMMY" + request.getPhoneNumber());

        }else{
            storeEntity.setId("DUMMY" + request.getEmail());
        }
        storeEntity.setAddedBy("admin");

        return storeEntity;
    }
	public boolean verifyEmailOTP(VerifyOTPRequest emailOtp) throws APIException {
		return repositoryHandler.verifyEmailOtp(emailOtp);
	}
	public boolean verifySmsOTP(VerifyOTPRequest smsOtp) {
		 return repositoryHandler.verifyPhoneOtp(smsOtp);
	}
	public boolean sendEmailOtp(OTPRequest request) {
		return repositoryHandler.sendEmailOtp(request);
	}

    public boolean sendOTPToPhone(OTPRequest request) {
        return repositoryHandler.sendOtpToSMS(request);
    }



}
