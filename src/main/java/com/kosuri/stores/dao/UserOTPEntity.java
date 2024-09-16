package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_otp")
public class UserOTPEntity {
	@Id
	@Nonnull
	private @Column(name = "user_id") String userOtpId;
	private @Column(name = "active") Integer active;
	private @Column(name = "created_on") String createdOn;
	private @Column(name = "updated_on") String updatedOn;
	private @Column(name = "email") String userEmail;
	private @Column(name = "email_otp_date") Date emailOtpDate;
	private @Column(name = "phone_number") String userPhoneNumber;
	private @Column(name = "phone_otp_date") Date phoneOtpDate;
	private @Column(name = "email_otp") String emailOtp;
	private @Column(name = "phone_otp") String phoneOtp;
	private @Column(name = "email_verify") boolean emailVerify;
	private @Column(name = "sms_verify") boolean smsVerify;
	private @Column(name = "forget_password_otp") String forgetPasswordOtp;
	
}
