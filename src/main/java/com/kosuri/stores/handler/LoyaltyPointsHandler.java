package com.kosuri.stores.handler;

import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.request.ConfigureLoyaltyPointsRequest;
import com.kosuri.stores.model.request.CustomerLoyaltyRequest;
import com.kosuri.stores.model.request.RedeemLoyaltyPointsRequest;
import com.kosuri.stores.model.response.ConfigureLoyaltyPointsResponse;
import com.kosuri.stores.model.response.CustomerLoyaltyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@Service
public class LoyaltyPointsHandler {

    @Autowired
    LoyaltyRepository loyaltyRepository;

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    CustomerLoyaltyRepository customerLoyaltyRepository;

    @Autowired
    StoreRepository storeRepository;
    public ConfigureLoyaltyPointsResponse configureLoyaltyPoints(ConfigureLoyaltyPointsRequest request) throws Exception {
        Optional<StoreEntity> store = storeRepository.findById(request.getStoreId());
        if(!store.isPresent()){
            throw new APIException("No store found for given id");
        }
        LoyaltyEntity loyaltyEntity = new LoyaltyEntity();
        ConfigureLoyaltyPointsResponse response = new ConfigureLoyaltyPointsResponse();

        loyaltyEntity.setStoreId(request.getStoreId());
        loyaltyEntity.setSalesVolume(request.getSalesVolume());
        loyaltyEntity.setLoyaltyPoints(request.getLoyaltyPoints());
        loyaltyEntity.setFixedDiscountPercentage(request.getFixedDiscountPercentage());
        loyaltyEntity.setMinLoyaltyPoints(request.getMinLoyaltyPoints());

        Double totalSalesVolume = request.getMinLoyaltyPoints()*(request.getSalesVolume()/request.getLoyaltyPoints());
        Double totalDiscountPercentage = request.getFixedDiscountPercentage() * request.getMinLoyaltyPoints()/100D;
        Double totalDiscount = (totalSalesVolume * totalDiscountPercentage)/100;
        loyaltyEntity.setTotalSalesVolume(totalSalesVolume);

        loyaltyRepository.save(loyaltyEntity);

        response.setTotalSalesVolume(totalSalesVolume);
        response.setTotalDiscount(totalDiscount);

        return response;
    }

    public void redeemLoyaltyPointsForCustomer(RedeemLoyaltyPointsRequest request) throws Exception {
        Optional<StoreEntity> store = storeRepository.findById(request.getStoreId());
        if(!store.isPresent()){
            throw new APIException("No store found for given id");
        }
        CustomerLoyaltyEntity customerLoyaltyEntity = new CustomerLoyaltyEntity();
        if ((request.getFirstName() == null || request.getFirstName().isEmpty())
                && (request.getLastName() == null || request.getLastName().isEmpty())
                && (request.getCustomerPhone() == null || request.getCustomerPhone().isEmpty())) {
            throw new APIException("Please input customer name or phone no");
        }


        String name = null;
        if (request.getFirstName() == null && request.getLastName() != null) {
            name = request.getLastName().trim();
        } else if (request.getLastName() == null && request.getFirstName() != null) {
            name = request.getFirstName().trim();
        } else if (request.getLastName() != null && request.getFirstName() != null){
            name = request.getFirstName().trim() + " " + request.getLastName().trim();
        }

        if (name != null) {
            name = name.trim();
        }

        customerLoyaltyEntity.setLoyaltyPoints(request.getLoyaltyPoints());
        customerLoyaltyEntity.setCustomerName(name);
        customerLoyaltyEntity.setCustomerPhone(request.getCustomerPhone());
        customerLoyaltyEntity.setTotalSales(request.getTotalSales());
        customerLoyaltyEntity.setDiscountAmount(request.getDiscountAmount());
        customerLoyaltyEntity.setDiscountedDate(request.getDateOfDiscount());
        customerLoyaltyEntity.setStoreId(request.getStoreId());

        customerLoyaltyRepository.save(customerLoyaltyEntity);
    }

    public List<CustomerLoyaltyResponse> getDiscountForCustomer(CustomerLoyaltyRequest request) throws Exception {
        Optional<StoreEntity> store = storeRepository.findById(request.getStoreId());
        List<CustomerLoyaltyResponse> responseList = new ArrayList<CustomerLoyaltyResponse>();
        if(!store.isPresent()){
            throw new APIException("No store found for given id");
        }

        Optional<LoyaltyEntity> storeLoyaltyOptional = loyaltyRepository.findById(request.getStoreId());
        if (storeLoyaltyOptional.isEmpty()) {
            throw new APIException("No loyalty points configured for this store");
        }

        LoyaltyEntity storeLoyalty = storeLoyaltyOptional.get();

        String name = null;

        if ((request.getFirstName() == null || request.getFirstName().isEmpty())
                && (request.getLastName() == null || request.getLastName().isEmpty())
                && (request.getCustomerPhone() == null || request.getCustomerPhone().isEmpty())) {
            throw new APIException("Please input customer name or phone no");
        }

        if (request.getFirstName() == null && request.getLastName() != null) {
            name = request.getLastName().trim();
        } else if (request.getLastName() == null && request.getFirstName() != null) {
            name = request.getFirstName().trim();
        } else if (request.getLastName() != null && request.getFirstName() != null){
            name = request.getFirstName().trim() + " " + request.getLastName().trim();
        }

        if (name != null) {
            name = name.trim();
        }

        if(name.trim().isEmpty()){
            name = null;
        }

        if (request.getCustomerPhone().isEmpty()) {
            request.setCustomerPhone(null);
        }

        Optional<List<CustomerLoyaltyEntity>> customerLoyaltyEntityOptional;
        if(request.getCustomerPhone() != null &&  name != null) {
            customerLoyaltyEntityOptional = customerLoyaltyRepository.findByCustomerNameAndCustomerPhoneAndStoreIdAndFirstByOrderByDiscountedDateDsc(name, request.getCustomerPhone(), request.getStoreId());
            Date lastDiscountedDate;
            if (customerLoyaltyEntityOptional.isPresent() && customerLoyaltyEntityOptional.isEmpty()) {
                lastDiscountedDate = customerLoyaltyEntityOptional.get().get(0).getDiscountedDate();
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                String dateInString = "1-Jan-1800";
                lastDiscountedDate = formatter.parse(dateInString);
            }
            List<Object[]> saleRecords = saleRepository.findTotalSalesForCustomerPhoneAndNameAndStoreIdAfterDate(request.getCustomerPhone(), name, lastDiscountedDate, request.getStoreId());
            responseList.addAll(getCustomerLoyaltyResponseList(saleRecords, storeLoyalty, request));
        } else {
            customerLoyaltyEntityOptional = customerLoyaltyRepository.findByCustomerNameOrCustomerPhoneAndStoreIdAndFirstByOrderByDiscountedDateDsc(name, request.getCustomerPhone(), request.getStoreId());

            List<CustomerLoyaltyEntity> customerLoyaltyEntities = new ArrayList<CustomerLoyaltyEntity>();

            if (customerLoyaltyEntityOptional.isPresent() && !customerLoyaltyEntityOptional.isEmpty()) {
                customerLoyaltyEntities = customerLoyaltyEntityOptional.get();
            }

            for (CustomerLoyaltyEntity entity: customerLoyaltyEntities) {
                List<Object[]> saleRecords = saleRepository.findTotalSalesForCustomerPhoneAndNameAndStoreIdAfterDate(request.getCustomerPhone(), name, entity.getDiscountedDate(), request.getStoreId());
                responseList.addAll(getCustomerLoyaltyResponseList(saleRecords, storeLoyalty, request));
            }

            final List<CustomerLoyaltyEntity> cList = customerLoyaltyEntities;
                
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            String dateInString = "1-Jan-1800";
            Date lastDiscountedDate = formatter.parse(dateInString);
            
            List<Object[]> saleRecords = saleRepository.findTotalSalesForCustomerPhoneOrNameAndStoreIdAfterDate(request.getCustomerPhone(), name, lastDiscountedDate, request.getStoreId());

            saleRecords.stream().filter(record -> cList.stream().noneMatch(customerEntity -> customerEntity.getCustomerPhone().equals(record[1]) && customerEntity.getCustomerName().equals(record[2])));
            responseList.addAll(getCustomerLoyaltyResponseList(saleRecords, storeLoyalty, request));
        }

        return responseList;
    }

    List<CustomerLoyaltyResponse> getCustomerLoyaltyResponseList(List<Object[]> saleRecords, LoyaltyEntity storeLoyalty, CustomerLoyaltyRequest request) {
        List<CustomerLoyaltyResponse> responseList = new ArrayList<CustomerLoyaltyResponse>();
        Double totalSaleAfterDate;

        for (Object[] obj: saleRecords) {
            CustomerLoyaltyResponse response = new CustomerLoyaltyResponse();
            String name = (String)obj[2];
            String[] n1 = name.split(" ");

            if (n1.length > 0)
            response.setFirstName(n1[0]);
            if (n1.length > 1)
            response.setLastName(n1[1]);
            response.setPhoneNumber((String)obj[1]);
            totalSaleAfterDate = (Double)obj[0];
            if (totalSaleAfterDate == null) {
                totalSaleAfterDate = 0D;
            }
            
            Long pointsEarned = Math.round((totalSaleAfterDate / storeLoyalty.getSalesVolume()) * storeLoyalty.getLoyaltyPoints());
            Double totalDiscPercentage = 0D;
            Double totalDiscountAmount = 0D;

            if (pointsEarned > storeLoyalty.getMinLoyaltyPoints()) {
                totalDiscPercentage = storeLoyalty.getFixedDiscountPercentage();
                totalDiscountAmount = (totalDiscPercentage * totalSaleAfterDate)/100;
            }

            response.setLoyaltyPoints(pointsEarned.intValue());
            response.setDiscountEligible(totalDiscountAmount);
            response.setTotalSalesVolume(totalSaleAfterDate);
            responseList.add(response);
        }
        return responseList;
    }
}
