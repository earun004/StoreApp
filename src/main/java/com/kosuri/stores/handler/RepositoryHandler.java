package com.kosuri.stores.handler;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.Status;
import com.kosuri.stores.model.enums.UserType;
import com.kosuri.stores.model.request.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RepositoryHandler {

	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private UserOTPRepository userOTPRepository;

	@Autowired
	private DiagnosticServiceRepository diagnosticServiceRepository;

	@Autowired
	private DiagnosticMasterRepository diagnosticMasterRepository;
	@Autowired
	private OtpHandler otpHandler;
	@Autowired
	private PrimaryCareCenterRepository primaryCareCenterRepository;

	@Autowired
	private PharmacistRepository pharmacistRepository;

	@Autowired
	private AdminStoreMembershipRepository adminStoreMembershipRepository;

	@Autowired
	private AdminStoreVerificationRepository adminStoreVerificationRepository;


	public StoreEntity addStoreToRepository(@Valid StoreEntity storeEntity) throws Exception {
		Optional<StoreEntity> store = storeRepository.findById(storeEntity.getId());
		Optional<UserOTPEntity> userOTPEntityOptional = userOTPRepository.findByUserEmailAndActive(storeEntity.getOwnerEmail(),1);
		if (store.isPresent()) {
			throw new APIException("Store with this id is already present");
		}else if (userOTPEntityOptional.isEmpty()){
			throw new APIException("Store User Not Found ");
		}
		return storeRepository.save(storeEntity);

	}

	public StoreEntity updateStore(@Valid StoreEntity storeEntity) throws Exception {
		Optional<StoreEntity> store = storeRepository.findById(storeEntity.getId());

		if (store.isEmpty()) {
			System.out.println("Entity not found");
			throw new APIException("Store with this id not found!");
		}

		return storeRepository.save(storeEntity);
	}

	public void addUser(@Valid StoreEntity storeEntity, AddUserRequest request) throws Exception {

		Optional<RoleEntity> role = roleRepository.findByRoleName(request.getRole());
		if (!role.isPresent()) {
			throw new APIException("Role does not exist. Please enter a valid role");
		}
		storeRepository.save(storeEntity);

	}

	public List<StoreEntity> getAllStores() throws Exception {
		List<StoreEntity> storeEntities = storeRepository.findAll();
		return storeEntities;
	}

	public Optional<List<PurchaseEntity>> getPurchaseRecordsByStore(String storeId) {
		return purchaseRepository.findByStoreId(storeId);
	}

	public Optional<List<SaleEntity>> getSaleRecordsByStore(String storeId) {
		return saleRepository.findByStoreId(storeId);
	}

	public Optional<List<StockEntity>> getStockRecordsByStore(String storeId) {
		return stockRepository.findByStoreId(storeId);
	}

	public boolean validateUser(AddUserRequest request) throws Exception {
		Optional<List<StoreEntity>> existingStores = storeRepository.findByOwnerEmailOrOwnerContact(request.getEmail(),
				request.getPhoneNumber());
		if (existingStores.isEmpty()) {
			return true;
		}
		for (StoreEntity store : existingStores.get()) {
			// TODO Update to query based on id
			if (store.getId().contains("DUMMY")) {
				System.out.println("User already exists in system");
				throw new APIException("User already exists in system");
			}
		}

		return true;
	}

	public boolean addStoreUser(@Valid TabStoreUserEntity storeEntity, AddTabStoreUserRequest request) throws Exception {

		Optional<RoleEntity> role = roleRepository.findByRoleName(request.getRole());

		TabStoreUserEntity user = tabStoreRepository.save(storeEntity);
		if ( user.getStoreUserEmail() != null) {
			//sets the EmailId and created Date for UserOTPEnitity
			UserOTPEntity userOtp = new UserOTPEntity();
			userOtp.setUserOtpId(storeEntity.getUserId());
			userOtp.setUserEmail(request.getUserEmail());
			userOtp.setActive(0);
			userOtp.setCreatedOn(LocalDateTime.now().toString());
			userOtp.setUserPhoneNumber(storeEntity.getStoreUserContact());
			userOTPRepository.save(userOtp);
			OTPRequest otpRequest = createOTPRequest(storeEntity.getStoreUserEmail(),storeEntity.getStoreUserContact());
			boolean isMessageSent = sendEmailOtp(otpRequest);
			boolean isPhoneOtpSent = sendOtpToSMS(otpRequest);
			return (isMessageSent||isPhoneOtpSent);
		}
		return false;
    }

	private OTPRequest createOTPRequest(String storeUserEmail, String storeUserContact) {
		OTPRequest otpRequest = new OTPRequest();
		otpRequest.setEmail(storeUserEmail);
		otpRequest.setPhoneNumber(storeUserContact);
		otpRequest.setIsForgetPassword(false);
		return otpRequest;
	}


	public TabStoreUserEntity loginUser(LoginUserRequest request) throws Exception {

		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository.findByStoreUserEmailOrStoreUserContact(request.getEmail(),
				request.getPhoneNumber());
		if (tabStoreUserEntityOptional.isEmpty()) {
			throw new APIException("Invalid Credentials!");
		}
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.orElse(null);
		if (tabStoreUserEntity.getPassword() != null && isValidPassword(request.getPassword(), tabStoreUserEntity.getPassword())) {
			System.out.println("User logged in successfully");
			return tabStoreUserEntity;
		}

		throw new APIException("Invalid Credentials!");
	}

	private boolean isValidPassword(String password, String encryptedPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), encryptedPassword);
		return result.verified;
	}

	public boolean validateStoreUser(AddTabStoreUserRequest request) throws Exception {
		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
				.findByStoreUserEmailOrStoreUserContact(request.getUserEmail(), request.getUserPhoneNumber());
		if (tabStoreUserEntityOptional.isEmpty()) {
			return true;
		}
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.orElse(null);
			if (tabStoreUserEntity.getStoreUserEmail().contains(request.getUserEmail())||
			tabStoreUserEntity.getStoreUserContact().contains(request.getUserPhoneNumber())) {
				System.out.println("User already exists in system");
				throw new APIException("User already exists in system");
			}
			boolean isStoreAdmin = storeAdminCheck(request.getUserType());
		if (!isStoreAdmin && (request.getStoreAdminEmail() == null || request.getStoreAdminEmail().isEmpty()) &&
				(request.getStoreAdminMobile() == null || request.getStoreAdminMobile().isEmpty())){
			throw new APIException("Store Admin Email or Mobile Should be Present For Store User");
		}
		return true;
	}

	private boolean storeAdminCheck(String userType) {
		return userType.equalsIgnoreCase(UserType.SA.toString());
	}

	public boolean verifyEmailOtp(VerifyOTPRequest verifyOTPRequest) throws APIException{
		String email = verifyOTPRequest.getEmail();
		String emailOtp = verifyOTPRequest.getOtp();

        UserOTPEntity userOtpEntity = userOTPRepository.findByUserEmailAndEmailOtpOrForgetEmailOtp(email,emailOtp);
        if (userOtpEntity != null) {
            if (verifyOTPRequest.getIsForgetPassword()) {
                return userOtpEntity.getActive() != null
                        && userOtpEntity.getActive() == 1;
            } else {
                if (userOtpEntity.isEmailVerify() || userOtpEntity.getActive() == 1) {
                    StoreConstants.IS_EMAIL_ALREADY_VERIFIED = true;
                }
                if (!userOtpEntity.isEmailVerify()) {
                    userOtpEntity.setEmailVerify(true);
                    userOtpEntity.setActive(1);
                    userOtpEntity.setUpdatedOn(LocalTime.now().toString());
                    userOTPRepository.save(userOtpEntity);
                    return true;
                }
            }
        }else{
			throw new APIException("Invalid Otp");
		}
        return false;
    }

	public boolean verifyPhoneOtp(VerifyOTPRequest verifyOTPRequest) {
		String phoneOtp = verifyOTPRequest.getOtp();
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		UserOTPEntity userOtpEntity = userOTPRepository.findByUserPhoneNumberAndPhoneOtpOrForgetEmailOtp(phoneNumber,phoneOtp);

		if (userOtpEntity != null) {
			if (verifyOTPRequest.getIsForgetPassword()) {
				return userOtpEntity.getActive() != null
						&& userOtpEntity.getActive() == 1;
			} else{
				if (userOtpEntity.getActive() == 1) {
					StoreConstants.IS_EMAIL_ALREADY_VERIFIED = true;
				}
				userOtpEntity.setSmsVerify(true);
				userOtpEntity.setActive(1);
				userOtpEntity.setUpdatedOn(LocalTime.now().toString());
				userOTPRepository.save(userOtpEntity);
				return true;
			}
		}
		return false;
	}

	public boolean sendEmailOtp(@Valid OTPRequest request) {
		Optional<TabStoreUserEntity> tabStoreUserOptional = tabStoreRepository.findByStoreUserEmail(request.getEmail());
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserOptional.orElse(null);
		if (null != tabStoreUserEntity &&
				null != tabStoreUserEntity.getUserType() &&
				tabStoreUserEntity.getUserType().equalsIgnoreCase(UserType.SA.toString())) {
			String storeUserEmail = request.getEmail();
			return (request.getIsForgetPassword() ? otpHandler.sendOtpToEmail(storeUserEmail, true, false):
					otpHandler.sendOtpToEmail(storeUserEmail, false, false));
		}
		return false;
	}


	public boolean sendOtpToSMS(@Valid OTPRequest request) {
		Optional<TabStoreUserEntity> tabStoreUserOptional = tabStoreRepository.findByStoreUserContact(request.getPhoneNumber());
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserOptional.orElse(null);
		if (null != tabStoreUserEntity && null != tabStoreUserEntity.getUserType() &&
				tabStoreUserEntity.getUserType().equalsIgnoreCase(UserType.SA.toString())) {
			String storeUserPhoneNumber = request.getPhoneNumber();
			return otpHandler.sendOtpToPhoneNumber(storeUserPhoneNumber);
		}
		return false;
	}


	public boolean addDiagnosticCenter(DiagnosticServicesEntity diagnosticServicesEntity, DiagnosticCenterRequest request) {
		DiagnosticServicesEntity dcEntity = diagnosticServiceRepository.save(diagnosticServicesEntity);
		return (null!=dcEntity);
	}

	public boolean isDCActive(DiagnosticCenterRequest request) throws APIException {
		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository.findById(request.getUserId());
		if(tabStoreUserEntityOptional.isEmpty()){
			throw new APIException("User Id Does not exists");
		}

		Optional<StoreEntity> storeInfoOptional = storeRepository.findById(request.getStoreId());
		if (storeInfoOptional.isPresent()) {
			StoreEntity storeEntity = storeInfoOptional.get();
			return storeEntity.getStatus().equalsIgnoreCase(Status.ACTIVE.toString());
		}

		return false;
	}

	public DiagnosticServicesEntity findServiceById(String userServiceId) {
		Optional<DiagnosticServicesEntity> diagnosticServicesEntityOptional = diagnosticServiceRepository.findById(userServiceId);
        return diagnosticServicesEntityOptional.orElse(null);
	}

	public void saveDiagnosticServiceEntity(DiagnosticServicesEntity serviceEntity) {
		diagnosticServiceRepository.save(serviceEntity);
	}

	public boolean isPCActive(PrimaryCareUserRequest request) {
		Optional<StoreEntity> storeInfoOptional = storeRepository.findById(request.getStoreId());
		StoreEntity storeEntity = storeInfoOptional.orElse(null);
		return (Objects.requireNonNull(storeEntity).getStatus().equalsIgnoreCase(Status.ACTIVE.toString()));

	}

	public boolean addPrimaryCareCenter(PrimaryCareEntity primaryCareEntity, PrimaryCareUserRequest request) {
		PrimaryCareEntity pcEntity = primaryCareCenterRepository.save(primaryCareEntity);
		return (null != pcEntity);
	}

	public PrimaryCareEntity findPrimaryServiceById(String userServiceId) {
		Optional<PrimaryCareEntity> primaryCareEntityOptional = primaryCareCenterRepository.findById(userServiceId);
		return primaryCareEntityOptional.orElse(null);
	}

	public void savePrimaryServiceEntity(PrimaryCareEntity serviceEntity) {
		primaryCareCenterRepository.save(serviceEntity);
	}

	public boolean updatePassword(TabStoreUserEntity tabStoreUserEntity) {
		TabStoreUserEntity tabStoreUserResponse = tabStoreRepository.save(tabStoreUserEntity);
		return (!ObjectUtils.isEmpty(tabStoreUserResponse));
	}

	public TabStoreUserEntity getTabStoreUser(String emailAddress, String userContactNumber) {
		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository.findByStoreUserEmailOrStoreUserContact(emailAddress,userContactNumber);
		return tabStoreUserEntityOptional.orElse(null);
	}

	public boolean addPharmacist(PharmacistEntity entity) {
		try{
            pharmacistRepository.save(entity);
            return true;
		}catch (Exception e) {
			return false;
		}

	}

	public boolean validatePharmacist(PharmasistRequest request) throws Exception{

		Optional<PharmacistEntity> pharmacistEntityOptional = pharmacistRepository
				.findByPharmacistEmailAddressOrPharmacistContact(request.getPharmaUserEmail(), request.getPharmaUserContact());
		if (pharmacistEntityOptional.isEmpty()) {
			return true;
		}
		PharmacistEntity pharmacistEntity = pharmacistEntityOptional.orElse(null);
		if (pharmacistEntity.getPharmacistEmailAddress().contains(request.getPharmaUserEmail())
		|| pharmacistEntity.getPharmacistContact().contains(request.getPharmaUserContact())) {
			throw new APIException("Pharmacist already exists in system");
		}
		return true;
    }

	public List<PharmacistEntity> findPharmacist(String mobileNumber,
												 String emailAddress,
												 String availableLocation) {
        return pharmacistRepository.
				findByPharmacistEmailAddressOrPharmacistContactOrPharmacistAvailableLocation(
				emailAddress, mobileNumber,availableLocation
		);
	}

	public boolean findPharmacistBasedOnContactNumberOrEmailAddress(String pharmaUserContact,
																	String pharmaUserEmail) {
		Optional<PharmacistEntity> pharmacistEntityOptional = pharmacistRepository.
				findByPharmacistEmailAddressOrPharmacistContact(pharmaUserEmail, pharmaUserContact);
		PharmacistEntity pharmacistEntity = pharmacistEntityOptional.orElse(null);
		return (pharmacistEntity != null);
	}

	public boolean updatePharmacist(PharmacistEntity entity) {
		try{
			pharmacistRepository.save(entity);
			return true;
		}catch (Exception e) {
			return false;
		}
	}

	public  boolean isStorePresent(CreateStoreRequest request) {
		Optional<StoreEntity> storeEntity = storeRepository.findByPincodeAndDistrictAndStateAndLocation(
				request.getPincode(),
				request.getDistrict(),
				request.getState(),
				request.getLocation()
		);
		return storeEntity.isPresent();
	}

	public  boolean isStorePresent(String storeId) {
		Optional<StoreEntity> storeEntity = storeRepository.findById(storeId);
		return storeEntity.isPresent();
	}

	public boolean isOwnerPresent(String ownerEmail, String ownerContact) {
		Optional<TabStoreUserEntity> storeUserEntity = tabStoreRepository.
				findByStoreUserEmailOrStoreUserContact(ownerEmail,ownerContact);
		return storeUserEntity.isPresent();
	}

	public AdminStoreMembershipEntity getStoreVerificationDetails(String storeCategory) {
		Optional<AdminStoreMembershipEntity> adminStoreMembershipEntity=  adminStoreMembershipRepository.
				findByStoreCategory(storeCategory);
		return adminStoreMembershipEntity.orElse(null);
	}

	public AdminStoreVerificationEntity getAdminStoreVerification(String storeId) {
		Optional<AdminStoreVerificationEntity> adminStoreVerificationEntity=  adminStoreVerificationRepository.
				findByStoreId(storeId);
		return adminStoreVerificationEntity.orElse(null);
	}

	public void saveAdminStoreVerificationEntity(AdminStoreVerificationEntity entity) {
		adminStoreVerificationRepository.save(entity);
	}
}