package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOTPRepository extends JpaRepository<UserOTPEntity, String> {


    Optional<UserOTPEntity> findByUserEmail(String email);

	Optional<UserOTPEntity> findByUserEmailAndActive(String email, Integer isActive);

	Optional<UserOTPEntity> findByUserPhoneNumber(String phoneNumber);

	@Query("SELECT u FROM UserOTPEntity u WHERE u.userEmail = :email AND (u.emailOtp = :otp OR u.forgetPasswordOtp = :otp)")
	UserOTPEntity findByUserEmailAndEmailOtpOrForgetEmailOtp(String email, String otp);

	@Query("SELECT u FROM UserOTPEntity u WHERE u.userPhoneNumber = :phoneNumber AND (u.phoneOtp = :otp OR u.forgetPasswordOtp = :otp)")
	UserOTPEntity findByUserPhoneNumberAndPhoneOtpOrForgetEmailOtp(String phoneNumber, String otp);
}
