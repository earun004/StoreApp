package com.kosuri.stores.handler;


import com.kosuri.stores.dao.*;
import com.kosuri.stores.template.EmailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OtpHandler {


    private final EmailService emailService;

    private final SmsHandler smsHandler;
    @Autowired
    public OtpHandler(EmailService emailService, SmsHandler smsHandler) {
        this.emailService = emailService;
        this.smsHandler = smsHandler;
    }

    @Autowired
    private UserOTPRepository userOtpRepository;
    @Autowired
    private StoreRepository storeRepository;


    public boolean sendOtpToEmail(String email, Boolean isForgetPassword, boolean isNotification) {
        if (isNotification){
            Optional<List<StoreEntity>> storeDetails = storeRepository.findByOwnerEmail(email);
            String location = getLocationDetails(storeDetails);
            EmailTemplate template = new EmailTemplate("static/send-notification.html");
            Map<String, String> replacements = new HashMap<>();

            replacements.put("user", email);
            replacements.put("location", location);
            String message = template.getTemplate(replacements);
           return emailService.sendEmailMessage(email, message, "Notification From RxKolan");
        } else {
            Optional<UserOTPEntity> userOtpOptional = userOtpRepository.findByUserEmail(email);
            String otp = OtpHandler.generateOTP(true);
            boolean messageSent;
            EmailTemplate template = new EmailTemplate("static/send-otp.html");
            Map<String, String> replacements = new HashMap<>();

            replacements.put("user", email);
            replacements.put("otp", otp);
            String message = template.getTemplate(replacements);
            messageSent =  emailService.sendEmailMessage(email, message, "OTP For RxWala");
            if (messageSent) {
                UserOTPEntity userOtp = new UserOTPEntity();
                if (userOtpOptional.isPresent()) {
                    userOtp = userOtpOptional.get();
                    if (isForgetPassword){
                        userOtp.setForgetPasswordOtp(otp);
                    }else{
                        userOtp.setEmailOtp(otp);
                    }
                    userOtp.setEmailOtpDate(new Date());
                }
                userOtpRepository.save(userOtp);
            }
            return messageSent;
        }
    }


    private String getLocationDetails(Optional<List<StoreEntity>> storeDetails) {

        if (storeDetails.isPresent()) {
            for (StoreEntity store : storeDetails.get()) {
                if (store.getLocation() != null && !store.getLocation().isEmpty()) {
                    return store.getLocation();
                }
            }
        }
        return null;
    }

    public boolean sendOtpToPhoneNumber(String phoneNumber) {

        Optional<UserOTPEntity> userOtpOptional = userOtpRepository.findByUserPhoneNumber(phoneNumber);
        String otp = OtpHandler.generateOTP(true);
        boolean messageSent = smsHandler.sendSMSMessage(phoneNumber,otp);
        if (messageSent) {
            if (userOtpOptional.isPresent()) {
                UserOTPEntity userOtp = userOtpOptional.get();
                userOtp.setPhoneOtp(otp);
                userOtp.setPhoneOtpDate(new Date());
                userOtpRepository.save(userOtp);
            }
        }
        return messageSent;
    }


    public static String generateOTP(boolean isOTP) {
        Random random = new Random();
        int lowerBound = isOTP ? 100000 : 1000;
        return String.valueOf(lowerBound + random.nextInt(999999 - lowerBound + 1));
    }


}
